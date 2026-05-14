package com.springboot.vaccineappointmentsystem.controller;

import com.springboot.vaccineappointmentsystem.config.JwtTokenProvider;
import com.springboot.vaccineappointmentsystem.entity.SysUser;
import com.springboot.vaccineappointmentsystem.entity.User;
import com.springboot.vaccineappointmentsystem.repository.SysUserRepository;
import com.springboot.vaccineappointmentsystem.service.LoginAttemptService;
import com.springboot.vaccineappointmentsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private SysUserRepository sysUserRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, Object> body) {
        try {
            String username = (String) body.get("username");
            String password = (String) body.get("password");

            if (username == null || username.isBlank()) {
                return badRequest("用户名不能为空");
            }
            if (password == null || password.isBlank()) {
                return badRequest("密码不能为空");
            }
            if (body.get("phone") == null || ((String) body.get("phone")).isBlank()) {
                return badRequest("手机号不能为空");
            }

            if (sysUserRepository.existsByUsername(username)) {
                return badRequest("用户名已存在");
            }
            if (sysUserRepository.existsByPhone((String) body.get("phone"))) {
                return badRequest("手机号已注册");
            }

            SysUser user = new SysUser();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setPhone((String) body.get("phone"));
            user.setType(0); // NORMAL user

            // Optional fields
            if (body.get("gender") != null) {
                user.setGender(((Number) body.get("gender")).intValue());
            }
            if (body.get("birthday") != null) {
                String bd = (String) body.get("birthday");
                user.setBirthday(java.time.LocalDate.parse(bd));
            }
            if (body.get("remark") != null) {
                user.setRemark((String) body.get("remark"));
            }

            SysUser saved = sysUserRepository.save(user);

            // Return without password
            Map<String, Object> response = new HashMap<>();
            response.put("id", saved.getId());
            response.put("username", saved.getUsername());
            response.put("message", "注册成功");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return badRequest(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        // Check if username is currently frozen
        Map<String, Object> blockResult = loginAttemptService.checkBlocked(username);
        if (blockResult != null) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(blockResult);
        }

        // Pre-check: user exists
        Optional<SysUser> sysUserOpt = sysUserRepository.findByUsername(username);
        if (sysUserOpt.isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "用户名不存在，请先注册");
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
            response.put("user", authentication.getPrincipal());
            response.put("message", "登录成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = loginAttemptService.recordFailedAttempt(username);
            // Override first-attempt generic message with password-specific hint
            if (error.containsKey("attempts") && error.get("attempts") instanceof Integer attempts && attempts <= 1) {
                error.put("error", "密码错误，请检查后重试");
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<User> userOpt = userService.getUserById(id);
        if (userOpt.isPresent()) {
            return ResponseEntity.ok(userOpt.get());
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "用户未找到");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        Optional<User> userOpt = userService.getUserByUsername(username);
        if (userOpt.isPresent()) {
            return ResponseEntity.ok(userOpt.get());
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "用户未找到");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        try {
            User updated = userService.updateUser(id, userDetails);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return badRequest(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "用户删除成功");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return badRequest(e.getMessage());
        }
    }

    private ResponseEntity<?> badRequest(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return ResponseEntity.badRequest().body(error);
    }
}
