package edu.umd.cysec.capstone.securityapp.service;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import edu.umd.cysec.capstone.securityapp.dao.mongo.UserRepository;
import edu.umd.cysec.capstone.securityapp.db.User;

@Service
public class MongoUserDetailsService  implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username);
        if(user == null) {
            throw new UsernameNotFoundException(username);
        }
        Set<GrantedAuthority> grantedAuthorities = Set.of(new SimpleGrantedAuthority("ROLE_USER"));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),user.getPassword(), grantedAuthorities
        );

    }


    public void createUser(UserDetails user) {
        User dbUser = new User(user.getUsername(),user.getPassword());
        userRepository.save(dbUser);
    }
}
