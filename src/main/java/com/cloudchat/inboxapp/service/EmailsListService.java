package com.cloudchat.inboxapp.service;

import com.cloudchat.inboxapp.models.EmailsList;
import com.cloudchat.inboxapp.repository.EmailsListRepository;
import com.datastax.oss.driver.api.core.uuid.Uuids;
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class EmailsListService {

    @Autowired
    private EmailsListRepository emailsListRepository;
    private final PrettyTime prettyTime = new PrettyTime();

    public List<EmailsList> getEmailsByEmailAndLabel(String loginId, String folder) {
        List<EmailsList> emails = emailsListRepository.findAllById_UserEmailAndId_Label(loginId, folder);
        emails.forEach(email -> {
            Date emailDate = new Date(Uuids.unixTimestamp(email.getId().getTimeId()));
            email.setAgoTimeString(prettyTime.format(emailDate));
        });
        return emails;
    }


}
