
import javax.jms.*;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class Reader{

    private Topic topic;
    private TopicConnection connection;
    private TopicSession session;


    private class MessageHandler implements MessageListener {

        public void onMessage(Message message) {
            try {
                TextMessage theMessage = (TextMessage) message;
                System.out.println("received: " + theMessage.getText());
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }

    public void connect() throws JMSException {
        String url = ActiveMQConnection.DEFAULT_BROKER_URL;
        TopicConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        connection = connectionFactory.createTopicConnection();
        session = connection.createTopicSession(false,Session.AUTO_ACKNOWLEDGE);
        topic = session.createTopic("Topic");
        TopicSubscriber theReader = session.createSubscriber(topic);
        theReader.setMessageListener(new MessageHandler());
        connection.start();
    }

    public void close() throws JMSException {
        connection.stop();
        session.close();
        connection.close();
    }

    public static void main(String[] args) throws JMSException {
        Reader reader = new Reader();
        reader.connect();
        while(true);
    }
}
