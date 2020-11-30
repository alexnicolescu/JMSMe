package event;

import data.News;

import java.io.Serializable;

public class NewsEvent implements Serializable {

    public enum EventType {
        NewsDeleted, NewsAdded, NewsRead, NewsModified
    }

    public News news;

    public EventType type;

    public NewsEvent(News news, EventType type) {
        this.news = news;
        this.type = type;
    }

}
