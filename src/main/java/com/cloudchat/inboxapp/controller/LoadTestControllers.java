package com.cloudchat.inboxapp.controller;

import com.cloudchat.inboxapp.entities.EmailDTO;
import com.cloudchat.inboxapp.models.Email;
import com.cloudchat.inboxapp.models.EmailsList;
import com.cloudchat.inboxapp.service.EmailService;
import com.cloudchat.inboxapp.service.EmailsListService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class LoadTestControllers {

    private final EmailService emailService;
    private final EmailsListService emailsListService;

    public LoadTestControllers(EmailService emailService, EmailsListService emailsListService) {
        this.emailService = emailService;
        this.emailsListService = emailsListService;
    }

    @PostMapping("/compose")
    public ResponseEntity<Void> saveEmail(@RequestBody EmailDTO emailDTO) {
        emailService.sendEmail(emailDTO.getFrom(), emailDTO.getTo(), emailDTO.getSubject(), emailDTO.getBody());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/email/{username}/i")
    public List<EmailsList> getAllEmailFromUserInbox(@PathVariable("username") String username) {
        return emailsListService.getEmailsByEmailAndLabel(username,"Inbox");
    }

    @GetMapping("/email/{username}/s")
    public List<EmailsList> getAllEmailFromUserSent(@PathVariable("username") String username) {
        return emailsListService.getEmailsByEmailAndLabel(username,"Sent");
    }

    @GetMapping("/email/{id}")
    public ResponseEntity<Email> getEmailById(@PathVariable String id) {
        Email email = emailService.GetById(UUID.fromString(id));
        return ResponseEntity.ok().body(email);
    }

    @DeleteMapping("/email/delete")
    public void deleteAll() {
        emailsListService.deleteAll();
        emailService.deleteAll();
    }

}
