package com.mtorres6739codefellowship.codefellowship.controllers;

import com.mtorres6739codefellowship.codefellowship.models.ApplicationUser;
import com.mtorres6739codefellowship.codefellowship.models.Post;
import com.mtorres6739codefellowship.codefellowship.repositories.ApplicationUserRepository;
import com.mtorres6739codefellowship.codefellowship.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;
import java.util.Date;

@Controller
public class PostController {

    @Autowired
    PostRepository postRepository;

    @Autowired
    ApplicationUserRepository applicationUserRepository;

    @PostMapping("/addpost")
    public RedirectView addPost(Principal p, Model m, String body, String subject) {
        if (p != null) {
            String username = p.getName();
            ApplicationUser applicationUser = (ApplicationUser) applicationUserRepository.findByUsername(username);
            m.addAttribute("applicationUser", applicationUser);
            Post post = new Post(body, subject);
            post.setCreatedAt(new Date());
            post.setApplicationUser(applicationUser);
            postRepository.save(post);
        }
        return new RedirectView("/myprofile");
    }
}
