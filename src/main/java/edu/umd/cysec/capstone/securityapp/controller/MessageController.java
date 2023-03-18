package edu.umd.cysec.capstone.securityapp.controller;

import static org.springframework.util.StreamUtils.BUFFER_SIZE;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.umd.cysec.capstone.securityapp.dao.mongo.MessageRepository;
import edu.umd.cysec.capstone.securityapp.db.Message;
import edu.umd.cysec.capstone.securityapp.service.Decryptor;
import edu.umd.cysec.capstone.securityapp.service.Encryptor;

@Controller
public class MessageController {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);
    ObjectMapper objectMapper = new ObjectMapper();

    public MessageController() {
        objectMapper.registerModule(new JavaTimeModule());
    }


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
                    message.setContent(decryptor.decryptString(message.getContent()));
                } catch (IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchPaddingException e) {
                    e.printStackTrace();
                }
            }

            model.addAttribute("messages",messageList);
        }
        return "home";
    }

    private String getUsername(Principal principal) {

        return principal.getName();
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
            dbMessage = new Message(currentUser,to,encryptor.encryptString(content));
            messageRepository.save(dbMessage);
        } catch (IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException
                 | InvalidAlgorithmParameterException | InvalidKeyException e) {
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
                logger.error(jsonProcessingException.getMessage());
        }
        return null;



    }
}
