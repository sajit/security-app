package edu.umd.cysec.capstone.securityapp.controller;

import static org.springframework.util.StreamUtils.BUFFER_SIZE;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.umd.cysec.capstone.securityapp.dao.mongo.MessageRepository;
import edu.umd.cysec.capstone.securityapp.db.Message;
import edu.umd.cysec.capstone.securityapp.service.Decryptor;
import edu.umd.cysec.capstone.securityapp.service.Encryptor;

@Controller
public class MessageController {

    ObjectMapper objectMapper = new ObjectMapper();


    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private Encryptor encryptor;

    @Autowired
    private Decryptor decryptor;

    @GetMapping("/home")
    public String home(Model model, Principal principal) {
        String currentUser =  getUsername(principal);
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

    private String getUsername(Principal principal) {
        Authentication auth = (Authentication) principal;
        String currentUser = auth.getPrincipal().toString();
        return currentUser;
    }

    @PostMapping(path = "/message",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = {
            MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
    public String create(@RequestParam Map<String, String> body, Principal principal) {
        String currentUser = getUsername(principal);
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

    @GetMapping("/dbdump/file")
    public StreamingResponseBody downloadFile(HttpServletResponse response)  {

        List<Message> messageList =  messageRepository.findAll();




        try {
            String jsonString = objectMapper.writeValueAsString(messageList);
            InputStream is = new ByteArrayInputStream(jsonString.getBytes());
            response.setContentType("application/json");
            response.setHeader(
                    HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"messages.json\"");
            return outputStream -> {
                int bytesRead;
                byte[] buffer = new byte[BUFFER_SIZE];

                while ((bytesRead = is.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            };
        } catch (JsonProcessingException jsonProcessingException) {

        }
        return null;



    }
}
