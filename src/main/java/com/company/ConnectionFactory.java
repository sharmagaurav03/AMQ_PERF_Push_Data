package com.company;

import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.jms.TopicConnectionFactory;
import javax.jms.QueueConnectionFactory;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.TopicConnection;
import javax.jms.TopicSubscriber;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSslConnectionFactory;


public class ConnectionFactory extends ActiveMQConnectionFactory {

	ActiveMQSslConnectionFactory activeMQSslConnectionFactory = null;

	public ConnectionFactory() throws NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException, JMSException{

		//ActiveMQSslConnectionFactory activeMQSslConnectionFactory = null;

		activeMQSslConnectionFactory = new ActiveMQSslConnectionFactory
				("ssl://gajpr03-i177112:61616?socket.enabledProtocols=TLSv1.1,TLSv1.2&jms.prefetchPolicy.all=1");
		System.out.println("created one entm connection");


		TrustManager[]  trustManager = new TrustManager[] { new X509TrustManager(){

			// @Override
			public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException{
				// do nothing
			}

			// @Override
			public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException{
				// do nothing
			}

			// @Override
			public X509Certificate[] getAcceptedIssuers(){
				return new X509Certificate[0];
			}
		}}; 
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmf.init(null, null);
		activeMQSslConnectionFactory.setKeyAndTrustManagers(kmf.getKeyManagers(), trustManager, null);

		//QueueConnectionFactory

		ActiveMQConnectionFactory activeMQConnectionFactory = activeMQSslConnectionFactory;

		Connection   queueConnection = activeMQConnectionFactory.createQueueConnection("admin", "admin");
		Session  queueSession = ((QueueConnection) queueConnection).createQueueSession(false, 1);
		Queue queue = queueSession.createQueue("queue/audit");
		
		for(int i=0;i<2;i++)
		{
			MessageConsumer consumer = queueSession.createConsumer(queue);
			QueueListener queueListener = new QueueListener();
			consumer.setMessageListener(queueListener); 
		}
		
		queueConnection.start();
		/*int messages = 0;
		final int MESSAGES_TO_CONSUME=10;
		do
		{
			TextMessage message = (TextMessage)consumer.receive();
			messages++;
			System.out.println ("Message #" + messages + ": " + message.getText());
		} while (messages < MESSAGES_TO_CONSUME);

		// Stop the connection — good practice but redundant here
		queueConnection.stop();*/

		//System.exit(0);


	}

	public ActiveMQSslConnectionFactory getActiveMQSslConnectionFactory() {
		return activeMQSslConnectionFactory;
	}

	/*public void setActiveMQSslConnectionFactory(ActiveMQSslConnectionFactory activeMQSslConnectionFactory) {
		this.activeMQSslConnectionFactory = activeMQSslConnectionFactory;
	}*/

	/*	private static void createActiveMqConnectionFactory()  throws Exception {
		ActiveMQSslConnectionFactory activeMQSslConnectionFactory = null;

		activeMQSslConnectionFactory = new ActiveMQSslConnectionFactory
				("ssl://sc14-dev-test.ca.com:61616?socket.enabledProtocols=TLSv1.1,TLSv1.2&jms.prefetchPolicy.all=1");
		System.out.println("created one entm connection");


		TrustManager[]  trustManager = new TrustManager[] { new X509TrustManager(){

			// @Override
			public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException{
				// do nothing
			}

			// @Override
			public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException{
				// do nothing
			}

			// @Override
			public X509Certificate[] getAcceptedIssuers(){
				return new X509Certificate[0];
			}
		}}; 
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmf.init(null, null);
		activeMQSslConnectionFactory.setKeyAndTrustManagers(kmf.getKeyManagers(), trustManager, null);

		//QueueConnectionFactory

		ActiveMQConnectionFactory activeMQConnectionFactory = activeMQSslConnectionFactory;

		Connection   queueConnection = activeMQConnectionFactory.createQueueConnection("admin", "firewall");
		Session  queueSession = ((QueueConnection) queueConnection).createQueueSession(false, 1);
		Queue queue = queueSession.createQueue("queue/audit");

		MessageConsumer consumer = queueSession.createConsumer(queue);

		int messages = 0;
		final int MESSAGES_TO_CONSUME=10;
		do
		{
			TextMessage message = (TextMessage)consumer.receive();
			messages++;
			System.out.println ("Message #" + messages + ": " + message.getText());
		} while (messages < MESSAGES_TO_CONSUME);

		// Stop the connection — good practice but redundant here
		queueConnection.stop();

		System.exit(0);
	}*/

	//return activeMQSslConnectionFactory;




	/*	public static void main(String[] args) throws Exception {

		ConnectionFactory.createActiveMqConnectionFactory();
	}*/

	
		
	
} 
