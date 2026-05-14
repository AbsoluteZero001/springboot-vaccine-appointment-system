package com.springboot.vaccineappointmentsystem.controller;

import com.springboot.vaccineappointmentsystem.config.JwtTokenProvider;
import com.springboot.vaccineappointmentsystem.entity.Admin;
import com.springboot.vaccineappointmentsystem.repository.SysUserRepository;
import com.springboot.vaccineappointmentsystem.service.AdminService;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admins")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private SysUserRepository sysUserRepository;

    @Autowired
    private LoginAttemptService loginAttemptService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        // Check if username is currently frozen
        Map<String, Object> blockResult = loginAttemptService.checkBlocked(username);
        if (blockResult != null) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(blockResult);
        }

        // Pre-check: admin exists in sys_user
        var sysUserOpt = sysUserRepository.findByUsername(username);
        if (sysUserOpt.isEmpty() || sysUserOpt.get().getType() != 1) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "管理员不存在");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            loginAttemptService.recordSuccess(username);
            String jwt = jwtTokenProvider.generateToken(authentication);
            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", jwt);
            response.put("tokenType", "Bearer");
            response.put("admin", authentication.getPrincipal());
            response.put("message", "管理员登录成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = loginAttemptService.recordFailedAttempt(username);
            if (error.containsKey("attempts") && error.get("attempts") instanceof Integer attempts && attempts <= 1) {
                error.put("error", "密码错误，请检查后重试");
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    @GetMapping
    public List<Admin> getAllAdmins() {
        return adminService.getAllAdmins();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAdminById(@PathVariable Long id) {
        Optional<Admin> adminOpt = adminService.getAdminById(id);
        if (adminOpt.isPresent()) {
            return ResponseEntity.ok(adminOpt.get());
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "管理员未找到");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @PostMapping
    public ResponseEntity<?> createAdmin(@RequestBody Admin admin) {
        try {
            Admin created = adminService.createAdmin(admin);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAdmin(@PathVariable Long id, @RequestBody Admin adminDetails) {
        try {
            Admin updated = adminService.updateAdmin(id, adminDetails);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAdmin(@PathVariable Long id) {
        try {
            adminService.deleteAdmin(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "管理员删除成功");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}