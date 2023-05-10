package com.cloudchat.inboxapp.service;

import com.cloudchat.inboxapp.models.key.EmailsListPrimaryKey;
import com.cloudchat.inboxapp.repository.EmailsListRepository;
import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.cloudchat.inboxapp.models.Email;
import com.cloudchat.inboxapp.models.EmailsList;
import com.cloudchat.inboxapp.repository.EmailRepository;
import com.cloudchat.inboxapp.repository.UnreadEmailStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EmailService {

    @Autowired
    private EmailsListRepository emailsListRepository;
    @Autowired
    private EmailRepository emailRepository;
    @Autowired
    private UnreadEmailStatsRepository unreadEmailStatsRepository;

    public void sendEmail(String fromUserId, String toUserIds, String subject, String body) {

        UUID timeUuid = Uuids.timeBased();

        List<String> toUserIdList = Arrays.stream(toUserIds.split(","))
                .map(StringUtils::trimWhitespace).filter(StringUtils::hasText)
                .collect(Collectors.toList());

        // Add to sent items of sender
        EmailsList sentItemEntry = prepareEmailsListEntry("Sent", fromUserId, fromUserId, toUserIdList, subject,
                timeUuid);
        sentItemEntry.setRead(true);
        emailsListRepository.save(sentItemEntry);

        // Add to inbox of each receiver
        toUserIdList.forEach(toUserId -> {
            EmailsList inboxEntry = prepareEmailsListEntry("Inbox", toUserId, fromUserId, toUserIdList, subject,
                    timeUuid);
            inboxEntry.setRead(false);
            emailsListRepository.save(inboxEntry);
            unreadEmailStatsRepository.incrementUnreadCounter(toUserId, "Inbox");
        });

        // Save email entity
        Email email = Email.builder()
                .id(timeUuid).from(fromUserId).to(toUserIdList)
                .subject(subject).body(body)
                .build();
        emailRepository.save(email);

    }

    private EmailsList prepareEmailsListEntry(String folderName, String forUser, String fromUserId,
                                              List<String> toUserIds, String subject, UUID timeUuid) {

        EmailsListPrimaryKey emailKey = new EmailsListPrimaryKey();
        emailKey.setLabel(folderName);
        emailKey.setUserEmail(forUser);
        emailKey.setTimeId(timeUuid);

        EmailsList emailsListEntry = new EmailsList();
        emailsListEntry.setId(emailKey);
        emailsListEntry.setFrom(fromUserId);
        emailsListEntry.setTo(toUserIds);
        emailsListEntry.setSubject(subject);

        return emailsListEntry;
    }

    public Email GetById(UUID emailId) {
        Optional<Email> opEmail = emailRepository.findById(emailId);
        return opEmail.orElse(null);
    }

    public void markEmailRead(String loginId, Email email, String folder) {
        EmailsListPrimaryKey key = EmailsListPrimaryKey.builder()
                .userEmail(loginId).label(folder)
                .timeId(email.getId())
                .build();

        Optional<EmailsList> optionalEmailListItem = emailsListRepository.findById(key);

        if (optionalEmailListItem.isEmpty()) throw new IllegalArgumentException();

        EmailsList emailListItem = optionalEmailListItem.get();
        if (!emailListItem.isRead()) {
            unreadEmailStatsRepository.decrementUnreadCounter(loginId, folder);
        }

        emailListItem.setRead(true);
        emailsListRepository.save(emailListItem);
    }

    public void moveToFolder(String toFolder, UUID emailId, String fromEmail, String currentFolder) {
        EmailsListPrimaryKey key = EmailsListPrimaryKey.builder()
                .userEmail(fromEmail).label(currentFolder)
                .timeId(emailId)
                .build();
        EmailsListPrimaryKey key2 = EmailsListPrimaryKey.builder()
                .userEmail(fromEmail).label(currentFolder)
                .timeId(emailId)
                .build();

        Optional<EmailsList> optionalEmailListItem = emailsListRepository.findById(key);
        if (optionalEmailListItem.isEmpty()) throw new IllegalArgumentException();
        EmailsList emailListItem = optionalEmailListItem.get();
        key2.setLabel(toFolder);
        emailListItem.setId(key2);
        emailsListRepository.save(emailListItem);
        emailsListRepository.deleteById(key);

    }

    public void deleteMessage(UUID timeID, String fromEmail, String currentFolder) {
        EmailsListPrimaryKey key = EmailsListPrimaryKey.builder()
                .userEmail(fromEmail).label(currentFolder)
                .timeId(timeID)
                .build();

        EmailsListPrimaryKey key2 = EmailsListPrimaryKey.builder()
                .userEmail(fromEmail).label("Sent")
                .timeId(timeID)
                .build();

        emailsListRepository.deleteById(key);
        emailsListRepository.deleteById(key2);

        emailRepository.deleteById(timeID);
    }

    public void deleteAll() {
        emailsListRepository.deleteAll();
        emailRepository.deleteAll();
    }
}
