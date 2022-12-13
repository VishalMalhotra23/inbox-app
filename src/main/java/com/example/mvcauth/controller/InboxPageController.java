package com.example.mvcauth.controller;


import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.example.mvcauth.entities.EmailsList;
import com.example.mvcauth.entities.Folder;
import com.example.mvcauth.repository.EmailsListRepository;
import com.example.mvcauth.repository.FolderRepository;
import com.example.mvcauth.service.FoldersService;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
public class InboxPageController {

    private final FolderRepository folderRepository;
    private final EmailsListRepository emailsListRepository;
    private final FoldersService foldersService;

    private final PrettyTime prettyTime;

    @Autowired
    public InboxPageController(FolderRepository folderRepository,
                               EmailsListRepository emailsListRepository,
                               FoldersService foldersService) {
        this.folderRepository = folderRepository;
        this.emailsListRepository = emailsListRepository;
        this.foldersService = foldersService;
        this.prettyTime = new PrettyTime();
    }



    @GetMapping(value = "/")
    public String getHomePage(@RequestParam(required = false) String folder,
                              @AuthenticationPrincipal OidcUser principal, Model model) {
        if (principal != null)  {
            String loginId = principal.getEmail();
            model.addAttribute("profile", principal.getClaims());

            List<Folder> folders = folderRepository.findAllById(loginId);
            List<Folder> initFolders = foldersService.init(loginId);
            // initFolders.stream().forEach(folderRepository::save);
            model.addAttribute("defaultFolders", initFolders);
            if (folders.size() > 0) {
                model.addAttribute("userFolders", folders);
            }
            if (StringUtils.isBlank(folder)) {
                folder = "Inbox";
            }
            model.addAttribute("currentFolder", folder);
            Map<String, Integer> folderToUnreadCounts = foldersService.getUnreadCountsMap(loginId);
            model.addAttribute("folderToUnreadCounts", folderToUnreadCounts);
            List<EmailsList> emails = emailsListRepository.findAllById_UserEmailAndId_Label(loginId, folder);
            emails.forEach(email -> {
                Date emailDate = new Date(Uuids.unixTimestamp(email.getId().getTimeId()));
                email.setAgoTimeString(prettyTime.format(emailDate));
            });
            model.addAttribute("folderEmails", emails);

            return "inbox-page";
        }
        return "index";

    }



}
