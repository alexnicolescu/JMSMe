import javax.jms.*;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Reader {

    private Topic topic;
    private TopicConnection connection;
    private TopicSession session;
    private List<News> subscribedNews = new ArrayList<>();


    private class MessageHandler implements MessageListener {

        public void onMessage(Message message) {
            try {
                ObjectMessage objMessage = (ObjectMessage) message;
                News news = (News) objMessage.getObject();
                if (news != null) {
                    System.out.println("Domain:" + news.getDomain());
                    System.out.println("Author:" + news.getAuthor());
                    System.out.println("Source:" + news.getSource());
                    System.out.println("Text:" + news.getText());
                    send(news.getDomain() + news.getSource() + news.getAuthor(), "Increment");
                    subscribedNews.add(news);
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }

    public Reader() {
        connect();
    }

    public void connect() {
        try {
            String url = ActiveMQConnection.DEFAULT_BROKER_URL;
            TopicConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
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
            unsubscribe();
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

    private void unsubscribe() {
        for(News news:subscribedNews) {
            send(news.getDomain() + news.getSource() + news.getAuthor(), "Decrement");
        }
    }

    public void subscribe(BufferedReader in) {
        try {
            System.out.println("Domain: ");
            String domain = in.readLine();
            System.out.println("Source: ");
            String source = in.readLine();

            topic = session.createTopic(domain + source);
            TopicSubscriber theReader = session.createSubscriber(topic);
            theReader.setMessageListener(new MessageHandler());
        } catch (IOException | JMSException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }

    }

    public void send(String topicName, String option) {
        try {
            Topic newTopic = session.createTopic(topicName);
            TopicPublisher publisher = session.createPublisher(newTopic);
            ObjectMessage message = session.createObjectMessage();
            message.setObject(option);
            publisher.send(message);
        } catch (JMSException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

    public static void main(String[] args) {

        Reader reader = new Reader();
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            System.out.println("1 Subscribe");
            System.out.println("2 Leave");
            try {
                int op = Integer.parseInt(in.readLine());
                switch (op) {
                    case 1:
                        reader.subscribe(in);
                        break;
                    case 2:
                        reader.close();
                        return;
                    default:
                        System.out.println("invalid operation");

                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
                System.exit(-1);
            }
        }
    }
}
