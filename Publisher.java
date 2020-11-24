
import javax.jms.*;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class Publisher {
    private Topic topic;
    private TopicConnection connection;
    private TopicSession session;

    public void connect() throws JMSException {
        String url = ActiveMQConnection.DEFAULT_BROKER_URL;
        TopicConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        connection = connectionFactory.createTopicConnection();
        session = connection.createTopicSession(false,Session.AUTO_ACKNOWLEDGE);
        topic = session.createTopic("Topic");
        connection.start();
    }

    public void send(String theMessage) throws JMSException {
        connect();
        TopicPublisher publisher = session.createPublisher(topic);
        TextMessage message = session.createTextMessage(theMessage);
        publisher.send(message);
    }

    public void close() throws JMSException {
        connection.stop();
        session.close();
        connection.close();
    }

    public static void main(String[] args) throws JMSException {

        Publisher p = new Publisher();
        p.send("ceva");
        p.close();
    }
}
