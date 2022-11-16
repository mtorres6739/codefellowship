package com.mtorres6739codefellowship.codefellowship.controllers;

import com.mtorres6739codefellowship.codefellowship.models.ApplicationUser;
import com.mtorres6739codefellowship.codefellowship.repositories.ApplicationUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.servlet.view.RedirectView;

import javax.persistence.EntityNotFoundException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.time.LocalDate;

@Controller
public class ApplicationUserController {

    @Autowired
    ApplicationUserRepository applicationUserRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private HttpServletRequest request;

    @GetMapping("/signup")
    public String getSignUpPage() {
        return "signup.html";
    }


    // GET MAPPING ROUTES
    @GetMapping("/login")
    public String getLoginPage(Principal p, Model m) {
        if (p != null) {
            ApplicationUser applicationUser = applicationUserRepository.findByUsername(p.getName());
            m.addAttribute("username", p.getName());
        }
        return "login.html";
    }

    @GetMapping("/myprofile")
    public String getMyProfile(Principal p, Model m) {
        if (p != null) {
            ApplicationUser applicationUser = (ApplicationUser) applicationUserRepository.findByUsername(p.getName());
            m.addAttribute("applicationUser", applicationUser);
        }
        return "myprofile.html";
    }


    @GetMapping("/profile/{userID}")
    public String getUserProfile(@PathVariable long userID, Principal p, Model m) {
        ApplicationUser publicUser = applicationUserRepository.getReferenceById(userID);
        if (p != null) {
            ApplicationUser applicationUser = applicationUserRepository.findByUsername(p.getName());
            if (applicationUser != null)
                m.addAttribute("applicationUser", applicationUser);
            boolean isFollowing = applicationUser.getFollowingSet().contains(publicUser);
            m.addAttribute("isFollowing", isFollowing);
        }

        try {
            publicUser.getFirstName();
        } catch (EntityNotFoundException enfe) {
            m.addAttribute("errorMessage", "Could not find user.");
            return "index.html";
        }
        m.addAttribute("publicUser", publicUser);
        return "profile.html";

    }



    // POST MAPPING ROUTES

    @PostMapping("/login")
    public RedirectView loginToApplication(String username, String password) {
        return new RedirectView("/");
    }

    @PostMapping("/logout")
    public RedirectView logoutFromApplication(Principal p) {
        if (p != null) {
            try {
                request.logout();
            } catch (ServletException e) {
                System.out.println("There seems to be an error with your logout.");
                e.printStackTrace();;
            }
        }
        return new RedirectView("/");
    }

    @PostMapping("/createuser")
    public RedirectView addUser(String username, String password, String firstName, String lastName, @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date, String bio) {
//        if (applicationUserRepository.findByUsername(username) != null)
//            return new RedirectView("/");
        String passwordHash = passwordEncoder.encode(password);
        ApplicationUser applicationUser = new ApplicationUser(username, passwordHash, firstName, lastName, date, bio);
        applicationUserRepository.save(applicationUser);
        authWithHttpServletRequest(username, password);
        return new RedirectView("/");
    }

    public void authWithHttpServletRequest(String username, String password) {
        try {
            request.login(username, password);
        } catch(ServletException se) {
            System.out.println("There was an error with your login.");
            se.printStackTrace();
        }
    }



    // PUT MAPPING ROUTES

    @PutMapping("/follow/{profileId}")
    RedirectView followUser(@PathVariable long profileId, Principal p, Model m) {
        if (p != null) {
            ApplicationUser applicationUser = applicationUserRepository.findByUsername(p.getName());
            ApplicationUser publicUser = applicationUserRepository.getReferenceById(profileId);
            if (applicationUser != null && publicUser != null) {
                m.addAttribute("applicationUser", applicationUser);
                applicationUser.getFollowingSet().add(publicUser);
                applicationUserRepository.save(applicationUser);
            }
        }
        return new RedirectView("/profile/" + profileId);
    }

}
