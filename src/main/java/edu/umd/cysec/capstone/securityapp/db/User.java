package edu.umd.cysec.capstone.securityapp.db;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document("users")
public class User {
    @Id
    private String id;

    @Field
    private String username;

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Field
    private String password;

    public User(String username,String password) {
        this.username = username;
        this.password = password;
    }

}
