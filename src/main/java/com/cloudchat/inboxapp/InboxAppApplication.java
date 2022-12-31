package com.cloudchat.inboxapp;

import com.cloudchat.inboxapp.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class InboxAppApplication {

    @Autowired
    private EmailService emailService;

    public static void main(String[] args) {
        SpringApplication.run(InboxAppApplication.class, args);
    }

    @PostConstruct
    public void loadData() {
        emailService.sendEmail("yadav117uday@gmail.com", "VMalhotra362@gmail.com", "Status on Cloud Deployment", "Hi Vishal,\n\nCan you share the current status on cloud deployment ?\nPranav and I are waiting for cloud to get ready to test some of the important feature related to the project.\nPlz reply asap with the status update\n\nThanks,\nUday Yadav");
        emailService.sendEmail("yadav117uday@gmail.com", "pranavnigam36@gmail.com", "Status Report Codebase", "Hi Pranav,\n\nI think the progress on codebase is going great, ayye !\nGovind while working on the frontend noticed the bug of showing the sender email in to.userId in viewing the message\nThis is probably a bug in the model of emailpagecontroller.java.\nPlease take a look and let me know if a viable solution is available\n\nThanks,\nUday Yadav");
        emailService.sendEmail("yadav117uday@gmail.com", "govindgund718@gmail.com", "Status Report on Frontend Design", "Hi Govind,\n\nWork on backend is almost complete and dev setup for cloud env is up.\nPlease let me know when you are done with frontend changes so that we can pull everything together and bring project to life\n\nThanks,\nUday Yadav");

        emailService.sendEmail("govindgund718@gmail.com", "VMalhotra362@gmail.com", "t2", "b2");
        emailService.sendEmail("govindgund718@gmail.com", "pranavnigam36@gmail.com", "t2", "b2");
        emailService.sendEmail("govindgund718@gmail.com", "yadav117uday@gmail.com", "Raised Frontend Design PR", "Hi Uday,\n\nWork on frontend is done, I have raised the PR on Git.\nUI looks beautiful and minimalistic. I am waiting for our everyone approval for logo to complete it.\n\nThanks,\nGovind Gund");

        emailService.sendEmail("pranavnigam36@gmail.com", "VMalhotra362@gmail.com", "t2", "b2");
        emailService.sendEmail("pranavnigam36@gmail.com", "govindgund718@gmail.com", "t2", "b2");
        emailService.sendEmail("pranavnigam36@gmail.com", "yadav117uday@gmail.com", "Fix: Removed duplicate to.userId bug", "Hi Uday,\n\nI looked into the bug and was able to resolve it.\nSolution involves removing duplicate from list structure. Now the response is as expected\n\nThanks,\nPranav Nigam");

        emailService.sendEmail("VMalhotra362@gmail.com", "pranavnigam36@gmail.com", "t2", "b2");
        emailService.sendEmail("VMalhotra362@gmail.com", "govindgund718@gmail.com", "t2", "b2");
        emailService.sendEmail("VMalhotra362@gmail.com", "yadav117uday@gmail.com", "Dev Env Cloud Ready", "Hi Uday,\n\nBoth Dev and Prod env are ready for code to roll out.\nBefore we push the jar, lets have a meet and discuss the final set of environment variable that's needs to be available\n\nThanks,\nVishal Malhotra");

        emailService.sendEmail("yadav117uday@gmail.com", "govindgund718@gmail.com,pranavnigam36@gmail.com,VMalhotra362@gmail.com,yadav117uday@gmail.com", "We did it !", "Hi Team,\n\nCongratulations on completing the project.\nIt wouldn't be possible if we'all didnt come together and work in synchrony.\n\nThanks,\nUday Yadav");
        emailService.sendEmail("govindgund718@gmail.com", "govindgund718@gmail.com,pranavnigam36@gmail.com,VMalhotra362@gmail.com,yadav117uday@gmail.com", "Congrats team", "Hi Team,\n\nCongratulations on completing the project.\nIt wouldn't be possible if we'all didnt come together and work in synchrony.\n\nThanks,\nGovind Gund");
        emailService.sendEmail("pranavnigam36@gmail.com", "govindgund718@gmail.com,pranavnigam36@gmail.com,VMalhotra362@gmail.com,yadav117uday@gmail.com", "Finished and Dusted", "Hi Team,\n\nCongratulations on completing the project.\nIt wouldn't be possible if we'all didnt come together and work in synchrony.\n\nThanks,\nPranav Nigam");
        emailService.sendEmail("VMalhotra362@gmail.com", "govindgund718@gmail.com,pranavnigam36@gmail.com,VMalhotra362@gmail.com,yadav117uday@gmail.com", "Done ! Less Go", "Hi Team,\n\nCongratulations on completing the project.\nIt wouldn't be possible if we'all didnt come together and work in synchrony.\n\nThanks,\nVishal Malhotra");
    }

}
