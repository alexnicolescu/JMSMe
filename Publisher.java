
import javax.jms.*;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Publisher {
    private Topic topic;
    private TopicConnection connection;
    private TopicSession session;
    private List<News> news = new ArrayList<News>();
    private String name;

    public Publisher(String name) {
        this.name = name;
        connect();
    }

    public void connect(){
        try {
            String url = ActiveMQConnection.DEFAULT_BROKER_URL;
            TopicConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
            connection = connectionFactory.createTopicConnection();
            session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            connection.start();
        }catch(JMSException e){
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

    public void send(News theNews,String topicName){
        try {
            topic = session.createTopic(topicName);
            TopicPublisher publisher = session.createPublisher(topic);
            ObjectMessage message = session.createObjectMessage();
            message.setObject(theNews);
            publisher.send(message);
        }catch (JMSException e){
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

    public void addNews(BufferedReader in){

        try {
            System.out.println("Domain: ");
            String domain = in.readLine();
            System.out.println("Source: ");
            String source = in.readLine();
            System.out.println("Text: ");
            String text = in.readLine();

            News theNews = new News(domain,source,this.name,text);
            news.add(theNews);
            this.send(theNews,domain+source);
        }catch(IOException e){
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

    public void modifyNews(BufferedReader in) {
    }

    public void deleteNews(BufferedReader in) {
    }

    public void getTheNumberOfReaders() {
    }

    public void close() throws JMSException {
        try {
            if (connection != null) {
                connection.stop();
                session.close();
                connection.close();
            }
        }catch(JMSException e){
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

    public static void main(String[] args) throws JMSException {

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
                        publisher.getTheNumberOfReaders();
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
