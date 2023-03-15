package edu.umd.cysec.capstone.securityapp.db;

import java.io.Serializable;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document("messages")
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;
    public Message() {}
    @Id
    private String id;

    @Indexed(background = true)
    @Field
    String to;

    @Indexed(background = true)
    @Field
    String from;

    @Field
    String content;

    public LocalDate getLocalDate() {
        return localDate;
    }

    @Field
    LocalDate localDate;

    public String getId() {
        return id;
    }

    public String getTo() {
        return to;
    }

    public String getFrom() {
        return from;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Message(String from, String to, String content){
        this(from,to,content,LocalDate.now());
    }

    public Message(String from, String to, String content, LocalDate date) {
        this.from = from;
        this.to  = to;
        this.content  = content;
        this.localDate = date;
    }


}
