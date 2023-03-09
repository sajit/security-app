package edu.umd.cysec.capstone.securityapp.controller;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import edu.umd.cysec.capstone.securityapp.dao.MessageRepository;
import edu.umd.cysec.capstone.securityapp.db.Message;

@Controller
public class MessageController {

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private MessageRepository messageRepository;

    @GetMapping("/home")
    public String home(Model model,HttpSession session) {
        String currentUser = getUsername(session);
        List<Message> messageList = messageRepository.getMessagesForUser(currentUser);
        if(messageList != null && !messageList.isEmpty()) {
            model.addAttribute("messages",messageList);
        }
        return "home";
    }

    private String getUsername(HttpSession session) {
        SecurityContext sc = (SecurityContext) session.getAttribute(SPRING_SECURITY_CONTEXT_KEY);
        Authentication auth = sc.getAuthentication();
        String currentUser = auth.getPrincipal().toString();
        return currentUser;
    }

    @PostMapping(path = "/message",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = {
            MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Message> create(@RequestParam Map<String, String> body, HttpSession session) {
        String currentUser = getUsername(session);
        String content = body.get("content");
        String to = body.get("to");
         Message dbMessage = new Message(currentUser,to,content);
         dbMessage = messageRepository.save(dbMessage);
        return new ResponseEntity<>(dbMessage, HttpStatus.CREATED);

    }

}
