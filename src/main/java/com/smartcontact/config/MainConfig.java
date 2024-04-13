package com.smartcontact.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class MainConfig {

    @Bean
    UserDetailsService getUserDetailsService() {
		return new UserDetailsServiceImpl();
	}

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

    @Bean
    DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(getUserDetailsService());
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
		return daoAuthenticationProvider;

	}

    @SuppressWarnings("removal")
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.authorizeHttpRequests().requestMatchers("/user/**").hasRole("USER")
			.requestMatchers("/admin/**").hasRole("ADMIN")
			.requestMatchers("/**").permitAll().and().formLogin()
			.loginPage("/signin")
			.loginProcessingUrl("/dologin")
			.defaultSuccessUrl("/user/index")
			.and().csrf().disable();
		
			
		return httpSecurity.build();
	}

}
