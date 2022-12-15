package com.cloudchat.inboxapp.controller;


import com.cloudchat.inboxapp.service.EmailService;
import com.cloudchat.inboxapp.models.Email;
import com.cloudchat.inboxapp.models.Folder;
import com.cloudchat.inboxapp.service.FoldersService;
import com.cloudchat.inboxapp.utils.Miscell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class ComposePageController {

    @Autowired
    private EmailService emailService;
    @Autowired
    private FoldersService foldersService;

    @GetMapping(value = "/compose")
    public String getComposePage(@RequestParam(required = false) String to,
                                 @RequestParam(required = false) String replayToEmailId,
                                 @RequestParam(value = "id", required = false) UUID emailId,
                                 @AuthenticationPrincipal OidcUser principal,
                                 Model model) {

        if (principal != null) {

            String loginId = principal.getEmail();
            model.addAttribute("profile", principal.getClaims());

            List<Folder> initFolders = foldersService.init(loginId);
            model.addAttribute("defaultFolders", initFolders);

            Map<String, Integer> folderToUnreadCounts = foldersService.getUnreadCountsMap(loginId);
            model.addAttribute("folderToUnreadCounts", folderToUnreadCounts);

            // for reply and reply all functionality
            if (emailId != null) {
                Email email = emailService.GetById(emailId);
                model.addAttribute("subject", Miscell.getReplySubject(email));
                model.addAttribute("body", Miscell.getReplyBody(email));
            }
            return "compose-page";
        }

        return "index";
    }

    @PostMapping(value = "/sendEmail")
    public ModelAndView sendEmail(@RequestBody MultiValueMap<String, String> formData,
                                  @AuthenticationPrincipal OidcUser principal) {

        if (principal == null) {
            return null;
        }

        String toUserIds = formData.getFirst("toUserIds");
        String subject = formData.getFirst("subject");
        String body = formData.getFirst("body");
        String from = principal.getEmail();

        if (toUserIds == null) {
            return null;
        }

        emailService.sendEmail(from, toUserIds, subject, body);

        return new ModelAndView("redirect:/");
    }

}
