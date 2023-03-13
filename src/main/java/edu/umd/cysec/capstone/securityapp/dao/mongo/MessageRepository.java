package edu.umd.cysec.capstone.securityapp.dao.mongo;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import edu.umd.cysec.capstone.securityapp.db.Message;

public interface MessageRepository extends MongoRepository<Message,String> {
    @Query("{from:'?0'}")
    List<Message> getMessagesFromUser(String username);
    @Query("{to:'?0'}")
    List<Message> getMessagesForUser(String username);
}
