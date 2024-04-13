package com.smartcontact.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smartcontact.entities.User;
import com.smartcontact.repo.UserRepository;
import com.smartcontact.services.EmailService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ForgotController {
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	// use to generate random OTP
	Random random = new Random(1000);
	
	@GetMapping("forgot")
	public String openForgotEmailForm(Model model) {
		model.addAttribute("title", "Forgot password");
		return "forgotEmailForm";
	}
	
	@PostMapping("/send-otp")
	public String sendOTP(@RequestParam("email") String email, HttpSession session, Model model) {
//		System.out.println("Email: "+email);
		model.addAttribute("title", "Enter OTP");
		// Generation OTP of 4 digit
		int otp = random.nextInt(9999);
		System.out.println("OTP: "+otp);
		
		//write code to send email
		String subject = "OTP from SmartContactManager";
		String message = ""
	            + "<div style='border:1px solid #e2e2e2; padding:20px;'>"
	            + "<h1>"
	            + "OTP is: "
	            + "<b>" + otp
	            + "</b>"
	            + "</h1>"
	            + "</div>";

		String to = email;
		boolean flag = emailService.sendEmail(subject, message, to);
		
		if(flag) {
			session.setAttribute("myotp", otp);
			session.setAttribute("email", email);
			return "verify_otp";
		}else {
			session.setAttribute("message", "Check your email!!");
			return "forgotEmailForm";
		}
		
	}
	
	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam("otp") int otp, HttpSession session, Model model) {
		model.addAttribute("title", "Change password");
		int myOtp = (int)session.getAttribute("myotp");
		String email = (String)session.getAttribute("email");
		if(myOtp==otp) {
			
			User user = userRepository.getUserByUserName(email);
			if(user==null) {
				// send email form 
				session.setAttribute("message", "User doesn't exist with this email !!");
				return "forgotEmailForm";
			}else {
				// send change password form
				return "change_password_form";
			}
			
		}else {
			session.setAttribute("message", "You have entered wrong OTP !!");
			return "verify_otp";
		}
	}
	
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("newpassword") String newPass, @RequestParam("confirmpassword") String confirmPass, HttpSession session) {
		String email = (String)session.getAttribute("email");
		User user = userRepository.getUserByUserName(email);
		
		if(!newPass.equals(confirmPass)) {
			session.setAttribute("message", "Confirm password doesn't match!!");
			return "change_password_form";
		}
		
		user.setPassword(bCryptPasswordEncoder.encode(newPass));
		userRepository.save(user);
		return "redirect:/signin?change=Password changed successfully! Login now..";
		

		
	}
}
