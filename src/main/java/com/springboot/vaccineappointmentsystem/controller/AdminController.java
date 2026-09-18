package com.springboot.vaccineappointmentsystem.controller;

import com.springboot.vaccineappointmentsystem.entity.SysUser;
import com.springboot.vaccineappointmentsystem.repository.SysUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admins")
public class AdminController {

    @Autowired
    private SysUserRepository sysUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public List<SysUser> getAllAdmins() {
        return sysUserRepository.findByRole("ROLE_ADMIN");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAdminById(@PathVariable Long id) {
        return sysUserRepository.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> {
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "管理员未找到");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
                });
    }

    @PostMapping
    public ResponseEntity<?> createAdmin(@RequestBody SysUser admin) {
        // Only one admin allowed
        if (sysUserRepository.countByRole("ROLE_ADMIN") >= 1) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "系统仅允许存在一个管理员账户");
            return ResponseEntity.badRequest().body(error);
        }
        if (sysUserRepository.existsByUsername(admin.getUsername())) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "用户名已存在");
            return ResponseEntity.badRequest().body(error);
        }
        admin.setRole("ROLE_ADMIN");
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        admin.setStatus(1);
        SysUser created = sysUserRepository.save(admin);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAdmin(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        var opt = sysUserRepository.findById(id);
        if (opt.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "管理员未找到");
            return ResponseEntity.badRequest().body(error);
        }
        SysUser admin = opt.get();
        if (body.get("status") != null) {
            admin.setStatus(((Number) body.get("status")).intValue());
        }
        if (body.get("phone") != null) {
            admin.setPhone((String) body.get("phone"));
        }
        if (body.get("password") != null && !((String) body.get("password")).isBlank()) {
            admin.setPassword(passwordEncoder.encode((String) body.get("password")));
        }
        sysUserRepository.save(admin);
        return ResponseEntity.ok(admin);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAdmin(@PathVariable Long id) {
        if (!sysUserRepository.existsById(id)) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "管理员未找到");
            return ResponseEntity.badRequest().body(error);
        }
        sysUserRepository.deleteById(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "管理员删除成功");
        return ResponseEntity.ok(response);
    }
}
