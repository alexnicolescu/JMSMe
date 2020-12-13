import lombok.Getter;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class Connector {

    @Getter
    private TopicConnection connection;

    @Getter
    private TopicSession session;

    public Connector() {
        try {
            String url = ActiveMQConnection.DEFAULT_BROKER_URL;
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
            connectionFactory.setTrustAllPackages(true);
            connection = connectionFactory.createTopicConnection();
            session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            connection.start();
        } catch (JMSException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

    public void close() {
        try {
            if (connection != null) {
                connection.stop();
                session.close();
                connection.close();
            }
        } catch (JMSException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

}
