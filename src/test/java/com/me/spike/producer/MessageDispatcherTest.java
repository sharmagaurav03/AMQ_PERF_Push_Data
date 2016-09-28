package com.me.spike.producer;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.me.spike.producer.MessageDispatcher;

/**
 * The class <code>MessageDispatcherTest</code> contains tests for the class
 * <code>{@link MessageDispatcher}</code>.
 *
 * @author shaga12
 */
public class MessageDispatcherTest {

	private MessageDispatcher dispatcher;

	@BeforeTest
	public void setBuilder() {
		dispatcher = new MessageDispatcher();
	}

	@Test(enabled=false)
	public void testRun() throws JMSException {
		MessageProducer producer = mock(MessageProducer.class);
		TextMessage message = mock(TextMessage.class);
		Connection connection = mock(Connection.class);
		doNothing().when(connection).close();
		int count = 1;
		dispatcher.setProducer(producer);
		dispatcher.setMessage(message);
		dispatcher.setCount(count);
		dispatcher.setConnection(connection);
		dispatcher.run();
		verify(producer, times(count)).send(message);
	}
	
	@Test(expectedExceptions=RuntimeException.class)
	public void testRunWithConnectionException() throws JMSException {
		MessageProducer producer = mock(MessageProducer.class);
		TextMessage message = mock(TextMessage.class);
		Connection connection = mock(Connection.class);
		doThrow(new JMSException("")).when(connection).close();
		int count = 1;
		dispatcher.setProducer(producer);
		dispatcher.setMessage(message);
		dispatcher.setCount(count);
		dispatcher.setConnection(connection);
		dispatcher.run();
		verify(producer, times(count)).send(message);
	}
	
	@Test(expectedExceptions = RuntimeException.class)
	public void testRunWithException() throws JMSException {
		MessageProducer producer = mock(MessageProducer.class);
		Connection connection = mock(Connection.class);
		TextMessage message = mock(TextMessage.class);
		
		doThrow(new JMSException("")).when(producer).send(message);
		doNothing().when(connection).close();
		int count = 1;
		dispatcher.setProducer(producer);
		dispatcher.setMessage(message);
		dispatcher.setConnection(connection);
		dispatcher.setCount(count);
		
		dispatcher.run();
		
	}

}
