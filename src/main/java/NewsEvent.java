import java.io.Serializable;

public class NewsEvent implements Serializable {

    public enum EventType {
        NewsDeleted, NewsAdded, NewsModified, NewsRead, NewsUnsubscribed
    }

    public News news;

    public EventType type;

    public NewsEvent(News news, EventType type) {
        this.news = news;
        this.type = type;
    }

}
