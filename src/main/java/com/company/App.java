package com.company;

import org.apache.activemq.ActiveMQConnectionFactory;

import java.util.Date;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

public class App {

	public static void main(String[] args) throws Exception {
		thread(new HelloWorldProducer(), false);
//		thread(new HelloWorldProducer(), false);
//		thread(new HelloWorldConsumer(), false);
//		Thread.sleep(1000);
//		thread(new HelloWorldConsumer(), false);
//		thread(new HelloWorldProducer(), false);
//		thread(new HelloWorldConsumer(), false);
//		thread(new HelloWorldProducer(), false);
//		Thread.sleep(1000);
//		thread(new HelloWorldConsumer(), false);
//		thread(new HelloWorldProducer(), false);
//		thread(new HelloWorldConsumer(), false);
//		thread(new HelloWorldConsumer(), false);
//		thread(new HelloWorldProducer(), false);
//		thread(new HelloWorldProducer(), false);
//		Thread.sleep(1000);
//		thread(new HelloWorldProducer(), false);
//		thread(new HelloWorldConsumer(), false);
//		thread(new HelloWorldConsumer(), false);
//		thread(new HelloWorldProducer(), false);
//		thread(new HelloWorldConsumer(), false);
//		thread(new HelloWorldProducer(), false);
//		thread(new HelloWorldConsumer(), false);
//		thread(new HelloWorldProducer(), false);
//		thread(new HelloWorldConsumer(), false);
//		thread(new HelloWorldConsumer(), false);
//		thread(new HelloWorldProducer(), false);
	}

	public static void thread(Runnable runnable, boolean daemon) {
		Thread brokerThread = new Thread(runnable);
		brokerThread.setDaemon(daemon);
		brokerThread.start();
	}

	public static class HelloWorldProducer implements Runnable {
		public void run() {
			try {
				// Create a ConnectionFactory
				ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("nio://SHAGA12:61616");

				// Create a Connection
				Connection connection = connectionFactory.createConnection();
				connection.start();

				// Create a Session
				Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

				// Create the destination (Topic or Queue)
				Destination destination = session.createQueue("TEST.FOO");

				// Create a MessageProducer from the Session to the Topic or
				// Queue
				MessageProducer producer = session.createProducer(destination);
				producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
				producer.send(session.createTextMessage(getMessage()));
				
				long time = System.currentTimeMillis();
				for(int i =0; i< 1200; i++)
				{
				TextMessage message = session.createTextMessage(getMessage());
				producer.send(message);
				}
				System.out.println((System.currentTimeMillis()-time));

				session.close();
				connection.close();
			} catch (Exception e) {
				System.out.println("Caught: " + e);
				e.printStackTrace();
			}
		}
	}
	
	private static String getMessage() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < 300; i++) {

			builder.append(
					"EVENT_HEADER:1  Target host:DMS__@kamvi03-I173137 Event type:Login event Status:Permitted User name:ac_entm_pers Terminal:10.131.65.16 Program:acws Date:22 Jul 2016 Time:22:21:00 Details:Resource ACL check User Logon Session ID:21eb614c-5ce9-4084-82bf-730dd73ace31 Audit flags:0 nStatus:80 Time Stamp:");
			builder.append(String.valueOf(new Date().getTime()));
			builder.append(" nReason:10 nStage:55 EVENT_END");
		}
		return builder.toString();
	}

	public static class HelloWorldConsumer implements Runnable, ExceptionListener {
		public void run() {
			try {

				// Create a ConnectionFactory
				ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost");

				// Create a Connection
				Connection connection = connectionFactory.createConnection();
				connection.start();

				connection.setExceptionListener(this);

				// Create a Session
				Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

				// Create the destination (Topic or Queue)
				Destination destination = session.createQueue("TEST.FOO");

				// Create a MessageConsumer from the Session to the Topic or
				// Queue
				MessageConsumer consumer = session.createConsumer(destination);

				// Wait for a message
				Message message = consumer.receive(1000);

				if (message instanceof TextMessage) {
					TextMessage textMessage = (TextMessage) message;
					String text = textMessage.getText();
					System.out.println("Received: " + text);
				} else {
					System.out.println("Received: " + message);
				}

				consumer.close();
				session.close();
				connection.close();
			} catch (Exception e) {
				System.out.println("Caught: " + e);
				e.printStackTrace();
			}
		}

		public synchronized void onException(JMSException ex) {
			System.out.println("JMS Exception occured. Shutting down client.");
		}
	}
}