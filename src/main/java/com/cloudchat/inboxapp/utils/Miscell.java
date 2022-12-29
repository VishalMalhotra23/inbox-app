package com.cloudchat.inboxapp.utils;

import com.cloudchat.inboxapp.models.Email;
import org.springframework.stereotype.Component;

@Component
public class Miscell {

    public static String getReplyBody(Email email) {
        return "\n\n\n------------------------------- \n" +
                "From: " + email.getFrom() + "\n" +
                "To: " + email.getTo() + "\n\n" +
                email.getBody();
    }

    public static String getReplySubject(Email email) {
        return "Re: " + email.getSubject();
    }
}
