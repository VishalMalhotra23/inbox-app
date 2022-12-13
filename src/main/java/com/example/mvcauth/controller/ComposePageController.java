package com.example.mvcauth.controller;


import com.example.mvcauth.entities.Folder;
import com.example.mvcauth.repository.FolderRepository;
import com.example.mvcauth.service.EmailService;
import com.example.mvcauth.service.FoldersService;
import org.ietf.jgss.Oid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
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

@Controller
public class ComposePageController {

    private final FolderRepository folderRepository;
    private final EmailService emailService;
    private final FoldersService foldersService;

    @Autowired
    public ComposePageController(FolderRepository folderRepository,
                                 EmailService emailService,
                                 FoldersService foldersService) {
        this.emailService = emailService;
        this.folderRepository = folderRepository;
        this.foldersService = foldersService;
    }


    @GetMapping(value = "/compose")
    public String getComposePage(@RequestParam(required = false) String to, @RequestParam(required = false) String replayToEmailId,
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

            Map<String, Integer> folderToUnreadCounts = foldersService.getUnreadCountsMap(loginId);
            model.addAttribute("folderToUnreadCounts", folderToUnreadCounts);

            return "compose-page";
        }

        return "index";
    }

    @PostMapping(value = "/sendEmail")
    public ModelAndView sendEmail(@RequestBody MultiValueMap<String, String> formData, @AuthenticationPrincipal OidcUser principal) {

        if (principal == null) {
            return null;
        }

        String toUserIds = formData.getFirst("toUserIds");
        String subject = formData.getFirst("subject");
        String body = formData.getFirst("body");
        String from = principal.getEmail();

        emailService.sendEmail(from, toUserIds, subject, body);

        return new ModelAndView("redirect:/");
    }

}
