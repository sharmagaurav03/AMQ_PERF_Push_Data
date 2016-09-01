package com.me.spike;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.me.spike.MessageDispatcher.MessageDispatcherBuilder;

public class MessageDispatcherBuilderTest {

	private MessageDispatcherBuilder builder;

	@BeforeTest
	public void setBuilder() {
		builder = new MessageDispatcherBuilder();
	}

	@Test
	public void testBuilderForURI() {
		builder.forURI("");
		Assert.assertNotNull(builder.getConnectionFactory());

	}

	@Test
	public void testForPrepareConnection() throws JMSException {
		ActiveMQConnectionFactory factory = mock(ActiveMQConnectionFactory.class);
		Connection connection = mock(Connection.class);
		when(factory.createConnection()).thenReturn(connection);
		builder.setConnectionFactory(factory);
		builder.prepareConnection();
		verify(factory, times(1)).createConnection();
		verify(connection, times(1)).start();
	}
	
	@SuppressWarnings("unchecked")
	@Test(expectedExceptions = RuntimeException.class)
	public void testForPrepareConnectionWithException() throws JMSException {
		ActiveMQConnectionFactory factory = mock(ActiveMQConnectionFactory.class);
		when(factory.createConnection()).thenThrow(JMSException.class);
		builder.setConnectionFactory(factory);
		builder.prepareConnection();
	}
	
	@Test
	public void testThenBeginSession() throws JMSException
	{
		Connection connection = mock(Connection.class);
		builder.setConnection(connection);
		builder.thenBeginSession();
		verify(connection, times(1)).createSession(false, Session.AUTO_ACKNOWLEDGE);
	}
	
	@SuppressWarnings("unchecked")
	@Test(expectedExceptions = RuntimeException.class)
	public void testThenBeginSessionWithException() throws JMSException
	{
		Connection connection = mock(Connection.class);
		when(connection.createSession(false, Session.AUTO_ACKNOWLEDGE)).thenThrow(JMSException.class);
		builder.setConnection(connection);
		builder.thenBeginSession();
	}
	
	@Test
	public void testToDestination() throws JMSException
	{
		Session session = mock(Session.class);
		builder.setSession(session);
		builder.toDestination("");
		verify(session, times(1)).createQueue("");
	}
	
	@SuppressWarnings("unchecked")
	@Test(expectedExceptions = RuntimeException.class)
	public void testToDestinationWithException() throws JMSException
	{
		Session session = mock(Session.class);
		when(session.createQueue("")).thenThrow(JMSException.class);
		builder.setSession(session);
		builder.toDestination("");
	}
	
	@Test
	public void testAndToProduce() throws JMSException
	{
		Destination destination = mock(Destination.class);
		MessageProducer producer = mock(MessageProducer.class);
		Session session = mock(Session.class);
		when(session.createProducer(destination)).thenReturn(producer);
		builder.setSession(session);
		builder.setDestination(destination);
		builder.andToProduce();
		verify(producer, times(1)).setDeliveryMode(DeliveryMode.NON_PERSISTENT);
	}
	
	@Test
	public void testGetDispatcher()
	{
		Connection connection = mock(Connection.class);
		MessageProducer producer = mock(MessageProducer.class);
		TextMessage message = mock(TextMessage.class);
		int count = 0;
		builder.setConnection(connection);
		builder.setProducer(producer);
		builder.setMessage(message);
		builder.numberOfTimes(count);
		MessageDispatcher messageDispatcher=builder.getDispatcher();
		assertEquals(messageDispatcher.getConnection(), connection);
		assertEquals(messageDispatcher.getProducer(), producer);
		assertEquals(messageDispatcher.getMessage(), message);
		assertEquals(messageDispatcher.getCount(), count);
		
		
	}
	
	@SuppressWarnings("unchecked")
	@Test(expectedExceptions = RuntimeException.class)
	public void testAndToProduceWithException() throws JMSException
	{
		Destination destination = mock(Destination.class);
		Session session = mock(Session.class);
		when(session.createProducer(destination)).thenThrow(JMSException.class);
		builder.setSession(session);
		builder.setDestination(destination);
		builder.andToProduce();
	}

	
	@Test
	public void testTheMessages() throws JMSException
	{
		Session session = mock(Session.class);
		builder = new MessageDispatcherBuilder(){
			String getMessage(String fileName) {
				return "";
			}
		};
		builder.setSession(session);
		builder.theMessages("");
		verify(session, times(1)).createTextMessage("");
	}
	
	@SuppressWarnings("unchecked")
	@Test(expectedExceptions = RuntimeException.class)
	public void testTheMessagesWithException() throws JMSException
	{
		Session session = mock(Session.class);
		builder = new MessageDispatcherBuilder(){
			String getMessage(String fileName) {
				return "";
			}
		};
		when(session.createTextMessage("")).thenThrow(JMSException.class);
		builder.setSession(session);
		builder.theMessages("");
	}
	
	@Test
	public void testNumberOf()
	{
		int number =0;
		builder.numberOfTimes(number);
		assertEquals(builder.getCount(), number);
	}
	
}
