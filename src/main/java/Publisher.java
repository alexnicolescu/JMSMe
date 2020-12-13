import javax.jms.*;

import connection.Connector;
import lombok.Getter;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Publisher {

    private Topic topic;

    private final Connector connector;

    private final List<News> news = new ArrayList<>();

    private final String name;

    @Getter
    private long numberOfReaders;

    private class MessageHandler implements MessageListener {

        public void onMessage(Message message) {
            try {
                ObjectMessage objMessage = (ObjectMessage) message;
                String option = (String) objMessage.getObject();
                if (option != null) {
                    switch (option) {
                        case "Increment":
                            numberOfReaders++;
                            break;
                        case "Decrement":
                            numberOfReaders--;
                            break;
                        default:
                            System.out.println("Invalid option");
                    }
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }

    public Publisher(String name) {
        this.name = name;
        connector = new Connector();
    }

    public void send(News theNews, String topicName) {
        try {
            TopicSession session = connector.getSession();
            topic = session.createTopic(topicName);
            TopicPublisher publisher = session.createPublisher(topic);
            ObjectMessage message = session.createObjectMessage();
            message.setObject(theNews);
            publisher.send(message);
        } catch (JMSException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

    public void addNews(BufferedReader in) {

        try {
            System.out.println("Domain: ");
            String domain = in.readLine();
            System.out.println("Source: ");
            String source = in.readLine();
            System.out.println("Text: ");
            String text = in.readLine();

            News theNews = new News(domain, source, this.name, text);
            news.add(theNews);
            this.send(theNews, domain + source);
            subscribe(theNews);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

    public void modifyNews(BufferedReader in) {

    }

    public void deleteNews(BufferedReader in) {

    }

    public void subscribe(News news) {
        try {
            TopicSession session = connector.getSession();
            topic = session.createTopic(news.getDomain() + news.getSource() + news.getAuthor());
            TopicSubscriber theReader = session.createSubscriber(topic);
            theReader.setMessageListener(new Publisher.MessageHandler());
        } catch (JMSException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }

    }

    public void close() {
        connector.close();
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java Publisher <name>");
            System.exit(1);
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        Publisher publisher = new Publisher(args[0]);
        while (true) {
            System.out.println("1 Add news");
            System.out.println("2 Modify news");
            System.out.println("3 Delete news");
            System.out.println("4 Get the number of readers");
            System.out.println("5 Leave");
            try {
                int op = Integer.parseInt(in.readLine());
                switch (op) {
                    case 1:
                        publisher.addNews(in);
                        break;
                    case 2:
                        publisher.modifyNews(in);
                        break;
                    case 3:
                        publisher.deleteNews(in);
                        break;
                    case 4:
                        System.out.println("Number of active readers:" + publisher.getNumberOfReaders());
                        break;
                    case 5: {
                        publisher.close();
                        return;
                    }
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
