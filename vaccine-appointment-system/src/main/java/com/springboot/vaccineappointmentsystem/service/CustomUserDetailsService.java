package com.springboot.vaccineappointmentsystem.service;

import com.springboot.vaccineappointmentsystem.entity.Admin;
import com.springboot.vaccineappointmentsystem.entity.User;
import com.springboot.vaccineappointmentsystem.repository.AdminRepository;
import com.springboot.vaccineappointmentsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // First try to find in User table
        var userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            return userOpt.get();
        }

        // If not found, try Admin table
        var adminOpt = adminRepository.findByUsername(username);
        if (adminOpt.isPresent()) {
            return adminOpt.get();
        }

        throw new UsernameNotFoundException("User not found with username: " + username);
    }
}