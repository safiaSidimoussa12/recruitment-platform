package com.jobboard.jobboard.shared.config;

import com.jobboard.jobboard.module.messagerie.MessageCountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAdvice {

    private final MessageCountService messageCountService;

    @ModelAttribute("unreadMessages")
    public long unreadMessages() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()
                || auth.getPrincipal().equals("anonymousUser")) {
            return 0;
        }
        return messageCountService.countUnreadForUser(auth.getName());
    }
}