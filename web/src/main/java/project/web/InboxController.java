package project.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.service.InboxService;

import java.util.HashMap;

@RestController
public class InboxController {

    @Autowired
    private InboxService inboxService;

    protected InboxController(){}

    public InboxController(InboxService inboxService) {
        this.inboxService = inboxService;
    }

    @RequestMapping("/login/gmail")
    public boolean gmailConnection() throws Exception {
        return inboxService.gmailConnection();
    }

    @RequestMapping("/gmail/findBouncedEmails")
    public HashMap<String, String> findBouncedEmails(@RequestParam(value = "code") String code)
    {
        return inboxService.findBouncedEmails(code);
    }

    @RequestMapping("/gmail/BouncedEmailsBody")
    public HashMap<String, String> BouncedEmailsBody(@RequestParam(value = "code") String code)
    {
        return inboxService.BouncedEmailsBody(code);
    }

    @RequestMapping("/gmail/deleteBouncedEmails")
    public boolean deleteBouncedEmails(@RequestParam(value = "code") String code)
    {
        return inboxService.deleteBouncedEmails(code);
    }
}



