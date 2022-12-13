package com.example.mvcauth.controller;

import com.example.mvcauth.entities.Email;
import com.example.mvcauth.entities.EmailsList;
import com.example.mvcauth.entities.Folder;
import com.example.mvcauth.entities.key.EmailsListPrimaryKey;
import com.example.mvcauth.repository.EmailRepository;
import com.example.mvcauth.repository.EmailsListRepository;
import com.example.mvcauth.repository.FolderRepository;
import com.example.mvcauth.repository.UnreadEmailStatsRepository;
import com.example.mvcauth.service.FoldersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Controller
public class EmailPageController {

    private final FolderRepository folderRepository;
    private final EmailRepository emailRepository;
    private final EmailsListRepository emailsListRepository;
    private final UnreadEmailStatsRepository unreadEmailStatsRepository;
    private final FoldersService foldersService;

    @Autowired
    public EmailPageController(FolderRepository folderRepository,
                               FoldersService foldersService,
                               EmailRepository emailRepository,
                               EmailsListRepository emailsListRepository,
                               UnreadEmailStatsRepository unreadEmailStatsRepository) {
        this.folderRepository = folderRepository;
        this.emailRepository = emailRepository;
        this.emailsListRepository = emailsListRepository;
        this.unreadEmailStatsRepository = unreadEmailStatsRepository;
        this.foldersService = foldersService;
    }


    @GetMapping(value = "/email/{id}")
    public String getEmailPage(@PathVariable String id, @RequestParam String folder,
                               @AuthenticationPrincipal OidcUser principal,
                               Model model) {

        if (principal != null ) {

            String loginId = principal.getEmail();
            model.addAttribute("profile", principal.getClaims());

            List<Folder> folders = folderRepository.findAllById(loginId);
            List<Folder> initFolders = foldersService.init(loginId);
            // initFolders.stream().forEach(folderRepository::save);

            model.addAttribute("defaultFolders", initFolders);
            if (folders.size() > 0) {
                model.addAttribute("userFolders", folders);
            }

            try {

                UUID uuid = UUID.fromString(id);
                Optional<Email> optionalEmail = emailRepository.findById(uuid);

                if (optionalEmail.isPresent()) {

                    Email email = optionalEmail.get();
                    String toIds = String.join(",", email.getTo());
                    model.addAttribute("email", optionalEmail.get());
                    model.addAttribute("toIds", toIds);
                    EmailsListPrimaryKey key = new EmailsListPrimaryKey();
                    key.setUserEmail(loginId);
                    key.setLabel(folder);
                    key.setTimeId(email.getId());

                    Optional<EmailsList> optionalEmailListItem = emailsListRepository.findById(key);

                    if (!optionalEmailListItem.isPresent()) throw new IllegalArgumentException();

                    EmailsList emailListItem = optionalEmailListItem.get();
                    if (!emailListItem.isRead()) {
                        unreadEmailStatsRepository.decrementUnreadCounter(loginId, folder);
                    }

                    emailListItem.setRead(true);
                    emailsListRepository.save(emailListItem);

                    Map<String, Integer> folderToUnreadCounts = foldersService.getUnreadCountsMap(loginId);
                    model.addAttribute("folderToUnreadCounts", folderToUnreadCounts);

                    return "email-page";
                }
            } catch (IllegalArgumentException e) {
                return "inbox-page";
            }
        }
        return "index";
    }

}

