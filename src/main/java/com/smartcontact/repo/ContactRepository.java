package com.smartcontact.repo;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smartcontact.entities.Contact;
import com.smartcontact.entities.User;

public interface ContactRepository extends JpaRepository<Contact, Integer>{
	
	// Pagination
	@Query("select c from Contact c where c.user.id= :userId")
	// Pageable contains 
	// current page - page
	// Record per page -5
	public Page<Contact> findContactsByUser(@Param("userId") int userId, Pageable pageable);
	
	// For searching by name
	public List<Contact> findByFirstNameContainingAndUser(String name, User user);

}