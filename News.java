import java.io.Serializable;
import java.util.Date;

public class News implements Serializable {
    private String source;
    private String author;
    private String publicationDate;
    private String lastModifiedDate;
    private String domain;
    private String text;


    public String getAuthor() {
        return author;
    }

    public News(String domain, String source, String author, String text) {
        this.domain = domain;
        this.source = source;
        this.author = author;
        this.publicationDate = new Date().toString();
        this.lastModifiedDate = new Date().toString();
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public String getPublicationDate() {
        return publicationDate;
    }

    public String getDomain() {
        return domain;
    }

    private void setText(String text) {
        this.text = text;
    }

    public void modify(String text) {
        setText(text);
        this.lastModifiedDate = new Date().toString();
    }

    public String getSource() {
        return source;
    }
}
