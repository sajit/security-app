package edu.umd.cysec.capstone.securityapp.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import edu.umd.cysec.capstone.securityapp.service.MongoUserDetailsService;

@Controller
public class UserController {

    @Autowired
    private MongoUserDetailsService userDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String index(HttpServletRequest request, HttpSession session) {
        session.setAttribute(
                "error", getErrorMessage(request, "SPRING_SECURITY_LAST_EXCEPTION")
        );
        return "index";
    }


    String passwordRegex =  "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&-+=()])(?=\\S+$).{8,20}$";
    Pattern p = Pattern.compile(passwordRegex);
    @PostMapping(
            value = "/register",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = {
            MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }
    )
    public String addUser(@RequestParam Map<String, String> body, Model model) {
        //validate username and password
        String username = body.get("username");
        String password = body.get("password");
        Matcher m = p.matcher(password);
        boolean userFound = true;
        try {
            UserDetails existingUser = userDetailsService.loadUserByUsername(username);
        } catch (UsernameNotFoundException unfe) {
            userFound = false;
        }
        if(m.matches() && !userFound) {
            UserDetails userDetails = new User(username,password, Collections.emptyList());

            userDetailsService.createUser(userDetails);
        }
        if(!m.matches()) {
            model.addAttribute("passwordError","Invalid password");

        }
        if(userFound) {
            model.addAttribute("userError","Existing username");
        }
        return "login";
    }
    private String getErrorMessage(HttpServletRequest request, String key) {
        Exception exception = (Exception) request.getSession().getAttribute(key);
        String error = "";
        if(exception != null) {

                error = "Invalid username and password!";

        }

        return error;
    }
}