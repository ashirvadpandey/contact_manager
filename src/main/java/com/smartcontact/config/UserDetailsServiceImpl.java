package com.smartcontact.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.smartcontact.entities.User;
import com.smartcontact.repo.UserRepository;

public class UserDetailsServiceImpl implements UserDetailsService {
	
	@Autowired
	private UserRepository userRepo;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User userByUserName = userRepo.getUserByUserName(username);
		if(userByUserName == null) {
			throw new UsernameNotFoundException("User not found");
		}
		CustomUserDetails customUserDetails = new CustomUserDetails(userByUserName);
		return  customUserDetails;
	}
	
	
}
