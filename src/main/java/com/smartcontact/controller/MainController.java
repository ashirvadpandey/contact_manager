package com.smartcontact.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smartcontact.entities.User;
import com.smartcontact.helper.Message;
import com.smartcontact.repo.UserRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class MainController {
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private UserRepository userRepo;
	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("title", "Home- Smart contact manager");
		return "home";
	}
	
	@GetMapping("/about")
	public String about(Model model) {
		model.addAttribute("title", "About- Smart contact manager");
		return "about";
	}
	
	@GetMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("title", "Signup- Smart contact manager");
		model.addAttribute("user", new User());
		return "signup";
	}
	
	
	@PostMapping("/register")
	public String register( @Valid @ModelAttribute("user") User user,BindingResult result1, @RequestParam( value = "agreement", defaultValue= "false") boolean agreement, Model model, HttpSession session) {
		try {
			
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			if(!agreement) {
				System.out.println("Terms and coditions not accepted!!");
				throw new Exception("You have not agreed the terms and conditions");
			}
			
			if(result1.hasErrors()) {
				System.out.println("errors: " + result1.toString());
				model.addAttribute("user", user);
				return "signup";
			}
			
			String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
			user.setPassword(encodedPassword);
			User result = userRepo.save(user);
			
			session.setAttribute("message", new Message("Successfully registered", "alert-success"));
			System.out.println(agreement);
			System.out.println("User"+result);
			return "redirect:/signup";
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("Something went wrong !! "+e.getMessage(), "alert-danger"));
			return "signup";
		}
	}
	
	@GetMapping("/signin")
	public String login(Model model) {
		model.addAttribute("title", "Login- Login Page");
		return "login";
	}
}
