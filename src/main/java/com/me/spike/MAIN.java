package com.me.spike;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class MAIN {

	public static void main(String[] args) throws Exception {
		// Create a ConnectionFactory
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("nio://SHAGA12:61616");

		// ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("nio://SHAGA12-I180614:61616");

		// Create a Connection
		Connection connection = connectionFactory.createConnection();
		connection.start();

		// Create a Session
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		// Create the destination (Topic or Queue)
		Destination destination = session.createQueue("TEST.SPIKE");

		// Create a MessageProducer from the Session to the Topic or Queue
		MessageProducer producer = session.createProducer(destination);
		TextMessage message = session.createTextMessage(getMessage());
		producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

		producer.send(message);

		long time = System.currentTimeMillis();

//		useMultipleThreadProduer(producer, message);
		sendMessages(producer, message);

		System.out.println(System.currentTimeMillis() - time);

		producer.close();
		session.close();
		connection.close();

	}

	private static void sendMessages(MessageProducer producer, TextMessage message) throws JMSException {
		for (int i = 0; i < 1200; i++) {
			producer.send(message);
		}
	}

	@SuppressWarnings("unused")
	private static void useMultipleThreadProduer(MessageProducer producer, TextMessage message)
			throws InterruptedException {
		CountDownLatch countDownLatch = new CountDownLatch(2);
		new Thread(new Producer(producer, message, countDownLatch)).start();
		new Thread(new Producer(producer, message, countDownLatch)).start();
		countDownLatch.await();

	}

	private static String getMessage() {
		String content = null;
		try {
			Scanner scanner = new Scanner(new File("C:\\SustenanceDefects\\UARM\\UARM_Temp\\AMQ_ES_Perf\\events.txt"));
			content = scanner.useDelimiter("\\Z").next();
			scanner.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		return content;
	}

	public static class Producer implements Runnable {
		private MessageProducer producer = null;
		private TextMessage message = null;
		private CountDownLatch latch = null;

		Producer(MessageProducer producer, TextMessage message, CountDownLatch countDownLatch) {
			this.producer = producer;
			this.message = message;
			this.latch = countDownLatch;
		}

		public void run() {
			try {
				for (int i = 0; i < 600; i++) {
					producer.send(message);
				}
				latch.countDown();
			} catch (Exception e) {
				System.out.println("Caught: " + e);
				e.printStackTrace();
			}
		}
	}

}