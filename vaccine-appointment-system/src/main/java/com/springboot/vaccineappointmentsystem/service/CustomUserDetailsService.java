package com.springboot.vaccineappointmentsystem.service;

import com.springboot.vaccineappointmentsystem.repository.AdminRepository;
import com.springboot.vaccineappointmentsystem.repository.SysUserRepository;
import com.springboot.vaccineappointmentsystem.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private SysUserRepository sysUserRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Primary: query unified sys_user table
        try {
            var sysUserOpt = sysUserRepository.findByUsername(username);
            if (sysUserOpt.isPresent()) {
                return sysUserOpt.get();
            }
        } catch (Exception e) {
            log.debug("sys_user 表查询失败（可能尚未迁移）: {}", e.getMessage());
        }

        // Fallback: legacy user table
        var userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            return userOpt.get();
        }

        // Fallback: legacy admin table
        var adminOpt = adminRepository.findByUsername(username);
        if (adminOpt.isPresent()) {
            return adminOpt.get();
        }

        throw new UsernameNotFoundException("User not found with username: " + username);
    }
}