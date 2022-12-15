package com.cloudchat.inboxapp.controller;

import com.cloudchat.inboxapp.models.EmailsList;
import com.cloudchat.inboxapp.models.Folder;
import com.cloudchat.inboxapp.service.EmailsListService;
import com.cloudchat.inboxapp.service.FoldersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
public class InboxPageController {

    @Autowired
    private FoldersService foldersService;
    @Autowired
    private EmailsListService emailsListService;

    @GetMapping(value = "/")
    public String getHomePage(@RequestParam(value = "folder", required = false, defaultValue = "Inbox") String folder,
                              @AuthenticationPrincipal OidcUser principal, Model model) {
        if (principal != null) {
            String loginId = principal.getEmail();
            model.addAttribute("profile", principal.getClaims());

            List<Folder> initFolders = foldersService.init(loginId);
            model.addAttribute("defaultFolders", initFolders);
            model.addAttribute("currentFolder", folder);

            Map<String, Integer> folderToUnreadCounts = foldersService.getUnreadCountsMap(loginId);
            model.addAttribute("folderToUnreadCounts", folderToUnreadCounts);

            List<EmailsList> emails = emailsListService.getEmailsByEmailAndLabel(loginId, folder);
            model.addAttribute("folderEmails", emails);

            return "inbox-page";
        }
        return "index";

    }

}
