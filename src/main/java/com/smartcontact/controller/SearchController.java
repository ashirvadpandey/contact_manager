package com.smartcontact.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.smartcontact.entities.Contact;
import com.smartcontact.entities.User;
import com.smartcontact.repo.ContactRepository;
import com.smartcontact.repo.UserRepository;



@RestController
public class SearchController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	//Search Handler
	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query, Principal principal){
		
//		System.out.println("Query: " + query);
		String currentUserEmail = principal.getName();
		User currentUser = userRepository.getUserByUserName(currentUserEmail);
		List<Contact> searchContacts = contactRepository.findByFirstNameContainingAndUser(query, currentUser);
		
		return ResponseEntity.ok(searchContacts);
		
	}
	
}
