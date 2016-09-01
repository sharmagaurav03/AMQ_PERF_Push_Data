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

		MessageDispatcher dispatcher = new MessageDispatcherBuilder().forURI("nio://SHAGA12-I180614:61616").prepareConnection()
				.thenBeginSession().toDestination("TEST.SPIKE").andToProduce()
				.theMessages("C:\\SustenanceDefects\\UARM\\UARM_Temp\\AMQ_ES_Perf\\events.txt").numberOfTimes(600).getDispatcher();

		MessageDispatcher dispatcher1 = new MessageDispatcherBuilder().forURI("nio://SHAGA12:61616").prepareConnection()
				.thenBeginSession().toDestination("TEST.SPIKE").andToProduce()
				.theMessages("C:\\SustenanceDefects\\UARM\\UARM_Temp\\AMQ_ES_Perf\\events.txt").numberOfTimes(600).getDispatcher();

		Thread t1 = new Thread(dispatcher);
		Thread t2 = new Thread(dispatcher1);

		long temp = System.currentTimeMillis();
		t1.start();
		t2.start();
		t1.join();
		t2.join();
		System.out.println(System.currentTimeMillis() - temp);

	}

}

class MessageDispatcher implements Runnable {

	private MessageProducer producer;
	private TextMessage message;
	private Connection connection;
	private int count;

	private MessageDispatcher(MessageProducer producer, TextMessage message, Connection connection, int count) {
		this.producer = producer;
		this.message = message;
		this.connection = connection;
		this.count = count;
	}

	public void run() {
		try {
			long time = System.currentTimeMillis();

			for (int i = 0; i < count; i++) {
				producer.send(message);
			}

			System.out.println(Thread.currentThread().getName() + " " + (System.currentTimeMillis() - time));

		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally
		{
			try {
				this.connection.close();
			} catch (JMSException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	//Below messages are package private and to be used only in Unit test cases.
	
	MessageDispatcher() {
	}
	
	void setProducer(MessageProducer producer) {
		this.producer=producer;
	}
	
	void setConnection(Connection connection) {
		this.connection=connection;
	}

	void setMessage(TextMessage message) {
		this.message=message;
		
	}

	MessageProducer getProducer() {
		return producer;
	}
	
	void setCount(int count) {
		this.count=count;
	}

	TextMessage getMessage() {
		return message;
	}

	Connection getConnection() {
		return connection;
	}
	
	int getCount() {
		return count;
	}

	public static class MessageDispatcherBuilder {
		private ActiveMQConnectionFactory connectionFactory;
		private Connection connection;
		private Session session;
		private Destination destination;
		private MessageProducer producer;
		private TextMessage message;
		private int count;

		public MessageDispatcherBuilder forURI(String uri) {
			this.connectionFactory = new ActiveMQConnectionFactory(uri);
			return this;
		}

		public MessageDispatcherBuilder prepareConnection() {
			try {
				connection = connectionFactory.createConnection();
				connection.start();
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

		public MessageDispatcherBuilder numberOfTimes(int count) {
			this.count = count;
			return this;
		}

		public MessageDispatcher getDispatcher() {
			return new MessageDispatcher(producer, message, connection, count);
		}

		// Below methods are private protected to enable junit test cases.
		
		//TODO: extract below to implementation of interface Readable
		
		 String getMessage(String fileName) {
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
		void setConnectionFactory(ActiveMQConnectionFactory connectionFactory) {
			this.connectionFactory = connectionFactory;
		}

		ActiveMQConnectionFactory getConnectionFactory() {
			return connectionFactory;
		}

		void setConnection(Connection connection) {
			this.connection = connection;
		}

		Connection getConnection() {
			return connection;
		}

		void setSession(Session session) {
			this.session = session;
		}

		Session getSession() {
			return session;
		}

		Destination getDestination() {
			return destination;
		}
		
		void setDestination(Destination destination) {
			this.destination=destination;
		}

		MessageProducer getProducer() {
			return producer;
		}
		
		void setProducer(MessageProducer producer) {
			this.producer=producer;
		}

		TextMessage getMessage() {
			return message;
		}
		
		void setMessage(TextMessage message) {
			this.message=message;
		}

		int getCount() {
			return count;
		}

	}

}