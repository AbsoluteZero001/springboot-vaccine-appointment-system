package com.springboot.vaccineappointmentsystem.controller;

import com.springboot.vaccineappointmentsystem.entity.SysUser;
import com.springboot.vaccineappointmentsystem.repository.SysUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private SysUserRepository sysUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, Object> body) {
        try {
            String username = (String) body.get("username");
            String password = (String) body.get("password");
            String phone = (String) body.get("phone");

            if (username == null || username.isBlank()) {
                return badRequest("用户名不能为空");
            }
            if (password == null || password.isBlank()) {
                return badRequest("密码不能为空");
            }
            if (phone == null || phone.isBlank()) {
                return badRequest("手机号不能为空");
            }
            if (sysUserRepository.existsByUsername(username)) {
                return badRequest("用户名已存在");
            }
            if (sysUserRepository.existsByPhone(phone)) {
                return badRequest("手机号已注册");
            }

            SysUser user = new SysUser();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setPhone(phone);
            user.setRole("ROLE_USER");
            user.setStatus(1);

            // Optional fields
            if (body.get("gender") != null) {
                user.setGender(((Number) body.get("gender")).intValue());
            }
            if (body.get("birthday") != null) {
                user.setBirthday(java.time.LocalDate.parse((String) body.get("birthday")));
            }
            if (body.get("remark") != null) {
                user.setRemark((String) body.get("remark"));
            }

            SysUser saved = sysUserRepository.save(user);

            Map<String, Object> response = new HashMap<>();
            response.put("id", saved.getId());
            response.put("username", saved.getUsername());
            response.put("message", "注册成功");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return badRequest(e.getMessage());
        }
    }

    @GetMapping
    public List<SysUser> getAllUsers() {
        return sysUserRepository.findByRole("ROLE_USER");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return sysUserRepository.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        return sysUserRepository.findByUsername(username)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        var opt = sysUserRepository.findById(id);
        if (opt.isEmpty()) {
            return badRequest("用户未找到");
        }
        SysUser user = opt.get();
        if (body.get("status") != null) {
            user.setStatus(((Number) body.get("status")).intValue());
        }
        if (body.get("phone") != null) {
            String phone = (String) body.get("phone");
            if (!phone.equals(user.getPhone()) && sysUserRepository.existsByPhone(phone)) {
                return badRequest("手机号已注册");
            }
            user.setPhone(phone);
        }
        sysUserRepository.save(user);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (!sysUserRepository.existsById(id)) {
            return badRequest("用户未找到");
        }
        sysUserRepository.deleteById(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "用户删除成功");
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<?> badRequest(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return ResponseEntity.badRequest().body(error);
    }
}
