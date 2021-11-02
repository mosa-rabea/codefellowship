package com.example.codefellowship.controller;


import com.example.codefellowship.model.ApplicationUser;
import com.example.codefellowship.repository.ApplicationUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class ApplicationUserController {

    @Autowired
    ApplicationUserRepository applicationUserRepository;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;


    @GetMapping("/")
    public String start(){
        return"start.html";
    }

    @GetMapping("/home")
    public String home(Model model){

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails)principal).getUsername();
            model.addAttribute("username" , username);
        } else {
            String username = principal.toString();
        }
        return"home.html";
    }

    @GetMapping("/signup")
    public String signUp(){
        return "signup.html";
    }

    @GetMapping("/login")
    public String logIn(){
        return "signin.html";
    }

    @PostMapping("/signup")
    public RedirectView signUp(@ModelAttribute ApplicationUser object){

        ApplicationUser newUser = new ApplicationUser(object.getUsername(),bCryptPasswordEncoder.encode(object.getPassword()) , object.getFirstName(), object.getLastName(), object.getDateOfBirth(), object.getBio());
        applicationUserRepository.save(newUser);
        return new RedirectView("login");
    }


    @GetMapping("/users/{id}")
    public String userInfo(Model model, @PathVariable("id") int id){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails)principal).getUsername();
            model.addAttribute("username" , username);
        } else {
            String username = principal.toString();
        }

        model.addAttribute("user" , applicationUserRepository.findById(id).get());


        return "user.html";
    }
    @GetMapping("/myprofile")
    public String profile(Model model){
      
        return "profile";
    }
}