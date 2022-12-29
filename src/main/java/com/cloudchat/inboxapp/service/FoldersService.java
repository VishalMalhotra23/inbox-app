package com.cloudchat.inboxapp.service;


import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.cloudchat.inboxapp.models.Folder;
import com.cloudchat.inboxapp.models.UnreadEmailStats;
import com.cloudchat.inboxapp.repository.UnreadEmailStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FoldersService {

    @Autowired private UnreadEmailStatsRepository unreadEmailStatsRepository;

    public List<Folder> init(String userId) {
        return Arrays.asList(
                new Folder(userId, "Inbox", "blue"),
                new Folder(userId, "Sent", "purple"),
                new Folder(userId, "Important", "red"),
                new Folder(userId, "Done", "green")
        );
    }

    public Map<String, Integer> getUnreadCountsMap(String loginId) {
        List<UnreadEmailStats> unreadStats = unreadEmailStatsRepository.findAllById(loginId);
        return unreadStats.stream()
                .collect(Collectors.toMap(UnreadEmailStats::getLabel, UnreadEmailStats::getUnreadCount));
    }

}
