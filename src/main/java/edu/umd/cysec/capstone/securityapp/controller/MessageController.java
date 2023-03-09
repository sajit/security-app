package edu.umd.cysec.capstone.securityapp.controller;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.servlet.http.HttpSession;
import java.security.InvalidKeyException;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import edu.umd.cysec.capstone.securityapp.dao.MessageRepository;
import edu.umd.cysec.capstone.securityapp.db.Message;
import edu.umd.cysec.capstone.securityapp.service.Decryptor;
import edu.umd.cysec.capstone.securityapp.service.Encryptor;

@Controller
public class MessageController {



    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private Encryptor encryptor;

    @Autowired
    private Decryptor decryptor;

    @GetMapping("/home")
    public String home(Model model,HttpSession session) {
        String currentUser = "hello1"; //getUsername(session);
        List<Message> messageList = messageRepository.getMessagesForUser(currentUser);
        if(messageList != null && !messageList.isEmpty()) {
            for(Message message : messageList) {
                try {
                    message.setContent(decryptor.decrypt(message.getContent()));
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                }
            }

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
    public String create(@RequestParam Map<String, String> body, HttpSession session) {
        String currentUser = "hello1";
        String content = body.get("content");
        String to = body.get("to");
        Message dbMessage = null;
        try {
            dbMessage = new Message(currentUser,to,encryptor.encrypt(content));
            messageRepository.save(dbMessage);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

        return "redirect:/home";

    }

}
