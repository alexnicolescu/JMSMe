import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class News implements Serializable {

    @Getter
    private final String source;

    @Getter
    private final String author;

    @Getter
    private final String publicationDate;

    @Getter
    private String lastModifiedDate;

    @Getter
    private final String domain;

    @Getter
    private String text;

    public News(String domain, String source, String author, String text) {
        this.domain = domain;
        this.source = source;
        this.author = author;
        this.publicationDate = LocalDateTime.now().toString();
        this.lastModifiedDate = LocalDateTime.now().toString();
        this.text = text;
    }

    public void setText(String text) {
        this.text = text;
        this.lastModifiedDate = LocalDateTime.now().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof News) {
            News newsToCompare = (News) o;
            return Objects.equals(newsToCompare.domain, domain) &&
                   Objects.equals(newsToCompare.source, source);
        }
        return false;
    }

}
