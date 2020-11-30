import javax.jms.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Reader {

    private Topic topic;

    private final Connector connector;

    private final List<News> subscribedNews = new ArrayList<>();

    private class MessageHandler implements MessageListener {

        @Override
        public void onMessage(Message message) {
            try {
                ObjectMessage objMessage = (ObjectMessage) message;
                NewsEvent event = (NewsEvent) objMessage.getObject();
                News news = event.news;
                if (news != null) {
                    switch (event.type) {
                        case NewsAdded:
                            System.out.println("News created: ");
                            subscribedNews.add(news);
                            send(news.getDomain() + news.getSource() + news.getAuthor(), "Increment");
                            break;
                        case NewsDeleted:
                            System.out.println("News deleted: ");
                            subscribedNews.remove(news);
                            break;
                        case NewsModified:
                            System.out.println("News modified: ");
                            //Stuff for modified here
                            break;
                    }
                    System.out.println("Domain:" + news.getDomain());
                    System.out.println("Author:" + news.getAuthor());
                    System.out.println("Source:" + news.getSource());
                    System.out.println("Text:" + news.getText());
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }

    public Reader() {
        connector = new Connector();
    }

    public void close() {
        unsubscribe();
        connector.close();
    }

    private void unsubscribe() {
        subscribedNews.forEach(news -> send(news.getDomain() + news.getSource() + news.getAuthor(), "Decrement"));
    }

    public void subscribe(BufferedReader in) {
        try {
            System.out.println("Domain: ");
            String domain = in.readLine();
            System.out.println("Source: ");
            String source = in.readLine();

            TopicSession session = connector.getSession();
            topic = session.createTopic(domain + source);
            TopicSubscriber reader = session.createSubscriber(topic);
            reader.setMessageListener(new MessageHandler());
        } catch (IOException | JMSException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }

    }

    public void send(String topicName, String option) {
        try {
            TopicSession session = connector.getSession();
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
