package com.springboot.vaccineappointmentsystem.controller;

import com.springboot.vaccineappointmentsystem.config.JwtTokenProvider;
import com.springboot.vaccineappointmentsystem.entity.SysUser;
import com.springboot.vaccineappointmentsystem.repository.SysUserRepository;
import com.springboot.vaccineappointmentsystem.service.LoginAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private SysUserRepository sysUserRepository;

    @Autowired
    private LoginAttemptService loginAttemptService;

    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        // Check if username is currently frozen
        Map<String, Object> blockResult = loginAttemptService.checkBlocked(username);
        if (blockResult != null) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(blockResult);
        }

        // Pre-check: user exists and is active
        var sysUserOpt = sysUserRepository.findByUsername(username);
        if (sysUserOpt.isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "用户名不存在，请先注册");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
        SysUser sysUser = sysUserOpt.get();
        if (sysUser.getStatus() != 1) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "账户已被禁用，请联系管理员");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            loginAttemptService.recordSuccess(username);
            String jwt = jwtTokenProvider.generateToken(authentication);

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "success");
            Map<String, Object> data = new HashMap<>();
            data.put("id", sysUser.getId());
            data.put("username", sysUser.getUsername());
            data.put("nickname", sysUser.getNickname() != null ? sysUser.getNickname() : sysUser.getUsername());
            data.put("phone", sysUser.getPhone());
            data.put("role", sysUser.getRole());
            data.put("status", sysUser.getStatus());
            data.put("isVerified", sysUser.getIsVerified());
            data.put("realName", sysUser.getRealName());
            data.put("idCard", sysUser.getIdCard());
            data.put("avatarUrl", sysUser.getAvatarUrl());
            data.put("gender", sysUser.getGender());
            data.put("birthday", sysUser.getBirthday() != null ? sysUser.getBirthday().toString() : null);
            data.put("remark", sysUser.getRemark());
            data.put("lastUsernameChangeTime", sysUser.getLastUsernameChangeTime() != null ? sysUser.getLastUsernameChangeTime().toString() : null);
            data.put("token", jwt);
            response.put("data", data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = loginAttemptService.recordFailedAttempt(username);
            if (error.containsKey("attempts") && error.get("attempts") instanceof Integer attempts && attempts <= 1) {
                error.put("error", "密码错误，请检查后重试");
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verify() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            Map<String, Object> error = new HashMap<>();
            error.put("authenticated", false);
            error.put("error", "未认证");
            return ResponseEntity.status(401).body(error);
        }

        String username = auth.getName();
        var userOpt = sysUserRepository.findByUsername(username);

        Map<String, Object> response = new HashMap<>();
        response.put("authenticated", true);
        response.put("username", username);
        response.put("role", auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")) ? "ROLE_ADMIN" : "ROLE_USER");

        if (userOpt.isPresent()) {
            SysUser u = userOpt.get();
            response.put("id", u.getId());
            response.put("nickname", u.getNickname());
            response.put("phone", u.getPhone());
            response.put("status", u.getStatus());
            response.put("isVerified", u.getIsVerified());
            response.put("realName", u.getRealName());
            response.put("idCard", u.getIdCard());
            response.put("avatarUrl", u.getAvatarUrl());
            response.put("gender", u.getGender());
            response.put("birthday", u.getBirthday() != null ? u.getBirthday().toString() : null);
            response.put("remark", u.getRemark());
            response.put("lastUsernameChangeTime", u.getLastUsernameChangeTime() != null ? u.getLastUsernameChangeTime().toString() : null);
        }

        return ResponseEntity.ok(response);
    }
}
