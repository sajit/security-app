package edu.umd.cysec.capstone.securityapp.dao.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import edu.umd.cysec.capstone.securityapp.db.User;

public interface UserRepository extends MongoRepository<User,String> {

    @Query("{username:'?0'}")
    User findUserByUsername(String name);

}
