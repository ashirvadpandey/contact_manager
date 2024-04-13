package com.smartcontact.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.smartcontact.entities.Contact;
import com.smartcontact.entities.User;
import com.smartcontact.helper.Message;
import com.smartcontact.repo.ContactRepository;
import com.smartcontact.repo.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContactRepository contactRepository;

	// Method for add common data in response
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		String userName = principal.getName();
		User userByUserName = userRepository.getUserByUserName(userName);
		model.addAttribute("user", userByUserName);
	}

	// method for dashboard
	@GetMapping("/index")
	public String dashboard(Model model) {
		model.addAttribute("title", "User- user dashboard");
		return "normal/user_dashboard";
	}

	// Open to addContact form
	@GetMapping("/addContact")
	public String openContactForm(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "normal/addContact";
	}

	// Method for open user profile
	@GetMapping("/profile")
	public String openProfile(@ModelAttribute User user, Model model) {
		model.addAttribute("title", "Your profile");
		return "normal/profile";
	}

	// Method to save the contact data
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Principal principal, Model model, HttpSession session) {

		try {
			model.addAttribute("title", "Save Contact");
			// here currectUserName means email
			String currentUserName = principal.getName();
			User currentUser = userRepository.getUserByUserName(currentUserName);
			if (!file.isEmpty()) {
				// if the file is not empty then save the file
				contact.setImageUrl(file.getOriginalFilename());
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("Image is uploaded");
			}
			// otherwise set the default Profile
			contact.setImageUrl("defaultpic.jpg");
			contact.setUser(currentUser);
			currentUser.getContacts().add(contact);
			userRepository.save(currentUser);
			System.out.println("Added to database");
			// success message
			session.setAttribute("message", new Message("Contact Added Successfully!! Add more..", "success"));
		} catch (Exception e) {
			e.printStackTrace();
			// message error
			session.setAttribute("message", new Message("Something went wrong!! Try again..", "danger"));

		}
		return "normal/addContact";
	}

	// Show all contacts
	// Per page = 5[5]
	// current page = 0[page]
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page, Model m, Principal principal, HttpSession session) {
		m.addAttribute("title", "View Contacts");
		// first way
		/*
		 * String currentUserEmail = principal.getName(); User currentUser =
		 * userRepository.getUserByUserName(currentUserEmail); List<Contact> contacts =
		 * currentUser.getContacts(); for (Contact contact : contacts) {
		 * System.out.println(contact.getFirstName()); }
		 */

		// second way, also use in pagination
		String currentUserEmail = principal.getName();
		User currentUser = userRepository.getUserByUserName(currentUserEmail);
		int userId = currentUser.getId();
		// Pageable contains: current page - page, Record per page -5
		Pageable pageable = PageRequest.of(page, 8);
		Page<Contact> contactsByUser = contactRepository.findContactsByUser(userId, pageable);
		m.addAttribute("contacts", contactsByUser);
		m.addAttribute("currentPage", page);
		// set the current page into session to retrieve in delete method
		session.setAttribute("currntPage", page);
		m.addAttribute("totalPages", contactsByUser.getTotalPages());
		return "normal/showContacts";
	}

	// showing individual contact detail
	@GetMapping("/{cid}/contactDetail")
	public String showContactDetail(@PathVariable("cid") Integer cid, Model model, Principal principal,
			HttpSession session) {
		// model.addAttribute("title", "Contact details");
		Optional<Contact> optContact = contactRepository.findById(cid);
		Contact oneContact = optContact.get();
		String currentUserEmail = principal.getName();
		User currentUser = userRepository.getUserByUserName(currentUserEmail);
		// If the contact belongs to the current user, then add(contact data) it to the
		// model
		// Add the contact to the model if it belongs to the current user
		if (oneContact.getUser().getId() == currentUser.getId()) {
			model.addAttribute("oneContact", oneContact);
			model.addAttribute("title", oneContact.getFirstName() + " " + oneContact.getLastName());
		} else {
			model.addAttribute("title", "Permission denied!");
		}
		return "normal/contactDetail";
	}

	// Delete the contact
	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cid, HttpSession session, Principal principal,
			Model model) {
		Optional<Contact> contactOptional = contactRepository.findById(cid);
		Contact contact = contactOptional.get();
		String currentUserEamil = principal.getName();
		User currentUser = userRepository.getUserByUserName(currentUserEamil);

		if (currentUser.getId() == contact.getUser().getId()) {
			contactRepository.delete(contact);
			session.setAttribute("message", new Message("Contact deleted successfully", "success"));

		} else {
			session.setAttribute("message", new Message("You are not authorize to delete this contact!!", "warning"));
		}
		Integer currentPageNumber = (Integer) session.getAttribute("currntPage");
		return "redirect:/user/show-contacts/" + currentPageNumber;
	}

	// open update form handler
	@PostMapping("/{cid}/update-contact")
	public String openContactUpdate(@PathVariable("cid") Integer cid, Model model) {
		model.addAttribute("title", "Update contact");
		Optional<Contact> contactOptional = contactRepository.findById(cid);
		Contact contact = contactOptional.get();
		model.addAttribute("updateContact", contact);
		return "normal/updateContact";
	}

	// update form process handler
	@PostMapping("/process-update")
	public String updateContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Model model, Principal principal, HttpSession session) {
		try {
			// Old contact detail
			Contact oldContact = contactRepository.findById(contact.getCid()).get();
			if (!file.isEmpty()) {
				// File work
				// Rewrite
				// Delete old photo
				// if old photo is defaultPhoto(defaultpic.jpg) then don't delete otherwise
				// delete photo
				if (!oldContact.getImageUrl().equalsIgnoreCase("defaultpic.jpg")) {
					File deleteFile = new ClassPathResource("static/img").getFile();
					File file1 = new File(deleteFile, oldContact.getImageUrl());
					file1.delete();
				}
				// Upload new photo
				File saveFile = new ClassPathResource("static/img").getFile();
				Path saveFilePath = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), saveFilePath, StandardCopyOption.REPLACE_EXISTING);
				contact.setImageUrl(file.getOriginalFilename());
			} else {
				contact.setImageUrl(oldContact.getImageUrl());
			}
			String currentUserEmail = principal.getName();
			User currentUser = userRepository.getUserByUserName(currentUserEmail);
			contact.setUser(currentUser);
			contactRepository.save(contact);
			session.setAttribute("message", new Message("Contact updated successfully", "success"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		Integer currentPageNumber = (Integer) session.getAttribute("currntPage");

		return "redirect:/user/show-contacts/" + currentPageNumber;
	}

	// Setting page
	@GetMapping("/settings")
	public String settings(Model m) {
		m.addAttribute("title", "Settings");
		return "normal/settings";
	}
}
