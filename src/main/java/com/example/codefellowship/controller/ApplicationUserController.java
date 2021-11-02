package com.example.codefellowship.controller;



import com.example.codefellowship.model.UserDataModel;
import com.example.codefellowship.repository.DbUserDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;
import java.util.List;

@Controller
public class ApplicationUserController {

    @Autowired
    DbUserDataRepository dbUserDataRepository;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;


    @GetMapping("/")
    public String start(Principal principl ,Model model){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails)principal).getUsername();
            model.addAttribute("username" , username);
        } else {
            String username = principal.toString();
        }
        return principl != null ? "home.html" : "start.html";
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
    public RedirectView signUp(@ModelAttribute UserDataModel object){

        UserDataModel newUser = new UserDataModel(object.getUsername(),bCryptPasswordEncoder.encode(object.getPassword()) , object.getFirstName(), object.getLastName(), object.getDateOfBirth(), object.getBio());
        dbUserDataRepository.save(newUser);
        return new RedirectView("login");
    }


    @GetMapping("/users/{id}")
    public String userInfo(Model model, @PathVariable("id") int id){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails)principal).getUsername();
            model.addAttribute("username" , username);
            UserDataModel user= dbUserDataRepository.findByUsername(username);
            if (!(user.getFollowing().contains(dbUserDataRepository.findById(id).get())) && dbUserDataRepository.findById(id).get() != user){
                model.addAttribute("status" , true);
            }else{
                model.addAttribute("status" , false);
            }
        } else {
            String username = principal.toString();
        }

        model.addAttribute("user" , dbUserDataRepository.findById(id).get());


        return "user.html";
    }


    @GetMapping("/myprofile")
    public String profile(Model model){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails)principal).getUsername();
            UserDataModel user = dbUserDataRepository.findByUsername(username);
            model.addAttribute("username" , username);


            model.addAttribute("user" , user);
        } else {
            String username = principal.toString();
        }
        return "profile";
    }

    @PostMapping("/follow/{id}")
    public RedirectView follow(Principal p , @PathVariable("id") int id){
        UserDataModel me = dbUserDataRepository.findByUsername(p.getName());
        UserDataModel followed = dbUserDataRepository.findById(id).get();

        me.getFollowing().add(followed);
        followed.getFollowers().add(me);
        dbUserDataRepository.save(me);
        dbUserDataRepository.save(followed);
        return new RedirectView("/users/{id}");
    }

    @GetMapping("/feed")
    public String feed(Principal p, Model model){
        UserDataModel me = dbUserDataRepository.findByUsername(p.getName());
        List<UserDataModel> following = me.getFollowing();
        model.addAttribute("following",following);
        model.addAttribute("username",me.getUsername());
        return "feed.html";
    }

    @GetMapping("/findfriends")
    public String findFriends(Model model , Principal p){
        List<UserDataModel> allUsers = (List<UserDataModel>)dbUserDataRepository.findAll();
        model.addAttribute("username",p.getName());
        model.addAttribute("users" , allUsers);

        return "findfriends.html";
    }
}