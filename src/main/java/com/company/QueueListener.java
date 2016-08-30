package com.company;
 
import java.io.UnsupportedEncodingException;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
 
import org.springframework.stereotype.Component;
 
public class QueueListener implements MessageListener
{
    public void onMessage( final Message message )
    {
        /*if ( message instanceof TextMessage )
        {
            final TextMessage textMessage = (TextMessage) message;
            try
            {
                System.out.println( textMessage.getText() );
            }
            catch (final JMSException e)
            {
                e.printStackTrace();
            }
        }*/
    	
    	BytesMessage msg = (BytesMessage) message;
//    	BytesMessage bytesMsg = (BytesMessage) msg;
//		bytesMsg.reset();
		byte[] data = null;
		String textMessage = "";
		try {
			if(msg.getBodyLength() > 0)
			{
				data = new byte[(int) msg.getBodyLength()];
				msg.readBytes(data);
				textMessage = new String(data, "UTF-8");
				System.out.println(textMessage); 
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
}