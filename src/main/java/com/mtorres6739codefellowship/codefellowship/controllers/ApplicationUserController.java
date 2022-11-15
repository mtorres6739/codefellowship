package com.mtorres6739codefellowship.codefellowship.controllers;

import com.mtorres6739codefellowship.codefellowship.models.ApplicationUser;
import com.mtorres6739codefellowship.codefellowship.repositories.ApplicationUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;

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
    public RedirectView addUser(String username, String password, String firstName, String lastName, @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
//        if (applicationUserRepository.findByUsername(username) != null)
//            return new RedirectView("/");
        String passwordHash = passwordEncoder.encode(password);
        ApplicationUser applicationUser = new ApplicationUser(username, passwordHash, firstName, lastName, date);
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
}
