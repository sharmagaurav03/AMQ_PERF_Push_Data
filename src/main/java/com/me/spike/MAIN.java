package com.me.spike;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

import com.me.spike.MessageDispatcher.MessageDispatcherBuilder;

public class MAIN {

	public static void main(String[] args) throws Exception {

		MessageDispatcher dispatcher = new MessageDispatcherBuilder().forURI("nio://SHAGA12:61616").prepareConnection()
				.thenBeginSession().toDestination("TEST.SPIKE").andToProduce()
				.theMessages("C:\\SustenanceDefects\\UARM\\UARM_Temp\\AMQ_ES_Perf\\events.txt").getDispatcher();
		
		MessageDispatcher dispatcher1 = new MessageDispatcherBuilder().forURI("nio://SHAGA12:61616").prepareConnection()
				.thenBeginSession().toDestination("TEST.SPIKE").andToProduce()
				.theMessages("C:\\SustenanceDefects\\UARM\\UARM_Temp\\AMQ_ES_Perf\\events.txt").getDispatcher();
		
		Thread t1= new Thread(dispatcher);
		Thread t2 = new Thread(dispatcher1);
		
		long temp=System.currentTimeMillis();
		t1.start();t2.start();
		t1.join();t2.join();
		System.out.println(System.currentTimeMillis()-temp);

	}

}

class MessageDispatcher implements Runnable {

	private MessageProducer producer;
	private TextMessage message;
	private Connection connection;

	private MessageDispatcher(MessageProducer producer, TextMessage message, Connection connection) {
		this.producer = producer;
		this.message = message;
		this.connection = connection;
	}

	public void run() {
		try {
			long time = System.currentTimeMillis();

			for (int i = 0; i < 600; i++) {
				producer.send(message);
			}

			System.out.println(System.currentTimeMillis() - time);

			this.connection.close();

		} catch (Exception e) {
			System.out.println("Caught: " + e);
			e.printStackTrace();
		}
	}
	
	MessageProducer getProducer() {
		return producer;
	}

	TextMessage getMessage() {
		return message;
	}

	Connection getConnection() {
		return connection;
	}

	public static class MessageDispatcherBuilder {
		private ActiveMQConnectionFactory connectionFactory;
		private Connection connection;
		private Session session;
		private Destination destination;
		private MessageProducer producer;
		private TextMessage message;

		public MessageDispatcherBuilder forURI(String uri) {
			this.connectionFactory = new ActiveMQConnectionFactory(uri);
			return this;
		}

		public MessageDispatcherBuilder prepareConnection() {
			try {
				this.connection = connectionFactory.createConnection();
				this.connection.start();
			} catch (JMSException e) {
				throw new RuntimeException(e);
			}
			return this;
		}

		public MessageDispatcherBuilder thenBeginSession() {
			try {
				this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			} catch (JMSException e) {
				throw new RuntimeException(e);
			}
			return this;
		}

		public MessageDispatcherBuilder toDestination(String destinationName) {
			try {
				this.destination = session.createQueue(destinationName);
			} catch (JMSException e) {
				throw new RuntimeException(e);
			}
			return this;
		}

		public MessageDispatcherBuilder andToProduce() {
			try {
				this.producer = session.createProducer(this.destination);
				producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			} catch (JMSException e) {
				throw new RuntimeException(e);
			}
			return this;
		}

		public MessageDispatcherBuilder theMessages(String filePath) {
			try {
				this.message = session.createTextMessage(getMessage(filePath));
			} catch (JMSException e) {
				throw new RuntimeException(e);
			}
			return this;
		}

		public MessageDispatcher getDispatcher() {
			return new MessageDispatcher(producer, message, connection);
		}

		private static String getMessage(String fileName) {
			String content = null;
			try {
				Scanner scanner = new Scanner(new File(fileName));
				content = scanner.useDelimiter("\\Z").next();
				scanner.close();
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
			return content;
		}

	}
}