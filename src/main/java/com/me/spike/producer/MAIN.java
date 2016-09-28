package com.me.spike.producer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

import com.me.spike.producer.MessageDispatcher.MessageDispatcherBuilder;

public class MAIN {

	public static void main(String[] args) throws Exception {

//		String uri = "nio://shaga12-I180958:61616?socketBufferSize=131072&ioBufferSize=16384&wireFormat.cacheSize=4096";
		long temp =0;

		
		String uri = "nio://shaga12-i181930:61616?socketBufferSize=131072&ioBufferSize=16384&wireFormat.cacheSize=4096";
		int count = 100;
		String queueName = "firstQueue";
		String messageFilePath = "C:\\SustenanceDefects\\UARM\\UARM_Temp\\AMQ_ES_Perf\\events.txt";
//		while(true)
//\\		{
		
		MessageDispatcher dispatcher1 = new MessageDispatcherBuilder().forURI(uri).prepareConnection()
				.thenBeginSession().toDestination(queueName).andToProduce()
				.theMessages(messageFilePath).numberOfTimes(count).getDispatcher();

//		MessageDispatcher dispatcher2 = new MessageDispatcherBuilder().forURI(uri).prepareConnection()
//				.thenBeginSession().toDestination(queueName).andToProduce()
//				.theMessages(messageFilePath).numberOfTimes(count).getDispatcher();
//		
//		MessageDispatcher dispatcher3 = new MessageDispatcherBuilder().forURI(uri).prepareConnection()
//				.thenBeginSession().toDestination(queueName).andToProduce()
//				.theMessages(messageFilePath).numberOfTimes(count).getDispatcher();
//
//		MessageDispatcher dispatcher4 = new MessageDispatcherBuilder().forURI(uri).prepareConnection()
//				.thenBeginSession().toDestination(queueName).andToProduce()
//				.theMessages(messageFilePath).numberOfTimes(count).getDispatcher();
//		
//		MessageDispatcher dispatcher5 = new MessageDispatcherBuilder().forURI(uri).prepareConnection()
//				.thenBeginSession().toDestination(queueName).andToProduce()
//				.theMessages(messageFilePath).numberOfTimes(count).getDispatcher();
		

		CountDownLatch startLatch=new CountDownLatch(1);
		CountDownLatch endLatch = new CountDownLatch(1);
		
		dispatcher1.setStartLatch(startLatch);/*dispatcher2.setStartLatch(startLatch);
		dispatcher3.setStartLatch(startLatch);dispatcher4.setStartLatch(startLatch);
		dispatcher5.setStartLatch(startLatch);*/
		
		dispatcher1.setEndLatch(endLatch);/*dispatcher2.setEndLatch(endLatch);
		dispatcher3.setEndLatch(endLatch);dispatcher4.setEndLatch(endLatch);
		dispatcher5.setEndLatch(endLatch);*/
		

		Thread t1 = new Thread(dispatcher1);
		/*Thread t2 = new Thread(dispatcher2);
		Thread t3 = new Thread(dispatcher3);
		Thread t4 = new Thread(dispatcher4);
		Thread t5 = new Thread(dispatcher5);*/
		
		t1.start();//t2.start();t3.start();t4.start();t5.start();
		startLatch.countDown();
		
		while(true)
		{
		temp=System.currentTimeMillis();
		
		dispatcher1.run();
		long time=1000-(System.currentTimeMillis() - temp);
		if(time>0)
		Thread.sleep(time);
		System.out.println(System.currentTimeMillis() - temp);
		}
//		endLatch.await();
	}

}

class MessageDispatcher implements Runnable {

	private MessageProducer producer;
	private Message message;
	private Connection connection;
	private int count;
	private CountDownLatch startLatch;
	private CountDownLatch endLatch;

	CountDownLatch getStartLatch() {
		return startLatch;
	}

	void setStartLatch(CountDownLatch startLatch) {
		this.startLatch = startLatch;
	}

	CountDownLatch getEndLatch() {
		return endLatch;
	}

	void setEndLatch(CountDownLatch endLatch) {
		this.endLatch = endLatch;
	}

	private MessageDispatcher(MessageProducer producer, Message message, Connection connection, int count) {
		this.producer = producer;
		this.message = message;
		this.connection = connection;
		this.count = count;
	}

	public void run() {
		try {
			startLatch.await();
			long time = System.currentTimeMillis();
			int i = 0;

			for ( ; i < count; i++) {
				producer.send(message);
			}

			System.out.println(Thread.currentThread().getName() + " " + (System.currentTimeMillis() - time) + " messages send "+ (i));

		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally
		{
//			endLatch.countDown();
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

	Message getMessage() {
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
		private Message message;
		private int count;

		public MessageDispatcherBuilder forURI(String uri) {
			this.connectionFactory = new ActiveMQConnectionFactory(uri);
			connectionFactory.setUseAsyncSend(true);
			connectionFactory.setProducerWindowSize(Integer.MAX_VALUE);
			connectionFactory.setUseCompression(true);
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

		public MessageDispatcherBuilder theMessages(String filePath) throws UnsupportedEncodingException {
			try {
				BytesMessage bMessage=session.createBytesMessage();
				bMessage.writeBytes(getMessage(filePath).getBytes("UTF-8"));
				this.message = bMessage;
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

		Message getMessage() {
			return message;
		}
		
		void setMessage(Message message) {
			this.message=message;
		}

		int getCount() {
			return count;
		}

	}

}