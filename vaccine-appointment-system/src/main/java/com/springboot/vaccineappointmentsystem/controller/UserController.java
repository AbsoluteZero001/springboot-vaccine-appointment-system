package com.springboot.vaccineappointmentsystem.controller;

import com.springboot.vaccineappointmentsystem.entity.SysUser;
import com.springboot.vaccineappointmentsystem.repository.SysUserRepository;
import com.springboot.vaccineappointmentsystem.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private SysUserRepository sysUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FileStorageService fileStorageService;

    private static final Pattern ID_CARD_PATTERN = Pattern.compile("^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[\\dXx]$");

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
            user.setNickname(username); // 默认昵称等于用户名

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
            response.put("nickname", saved.getNickname());
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
        if (body.get("remark") != null) {
            user.setRemark((String) body.get("remark"));
        }
        sysUserRepository.save(user);
        return ResponseEntity.ok(user);
    }

    // ── Profile management ──────────────────────────────────────

    @PutMapping("/{id}/profile")
    public ResponseEntity<?> updateProfile(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        var opt = sysUserRepository.findById(id);
        if (opt.isEmpty()) return badRequest("用户未找到");
        SysUser user = opt.get();

        if (body.get("nickname") != null) {
            String nickname = ((String) body.get("nickname")).trim();
            if (nickname.isBlank()) return badRequest("昵称不能为空");
            if (nickname.length() > 50) return badRequest("昵称最长50个字符");
            user.setNickname(nickname);
        }
        if (body.get("gender") != null) {
            user.setGender(((Number) body.get("gender")).intValue());
        }
        if (body.get("birthday") != null) {
            user.setBirthday(java.time.LocalDate.parse((String) body.get("birthday")));
        }
        if (body.get("phone") != null) {
            String phone = (String) body.get("phone");
            if (!phone.equals(user.getPhone()) && sysUserRepository.existsByPhone(phone)) {
                return badRequest("手机号已注册");
            }
            user.setPhone(phone);
        }
        if (body.get("remark") != null) {
            user.setRemark((String) body.get("remark"));
        }
        sysUserRepository.save(user);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/{id}/avatar")
    public ResponseEntity<?> uploadAvatar(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) return badRequest("文件为空");
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return badRequest("只允许上传图片文件");
            }
            var opt = sysUserRepository.findById(id);
            if (opt.isEmpty()) return badRequest("用户未找到");
            SysUser user = opt.get();
            String avatarUrl = fileStorageService.storeFile(file);
            user.setAvatarUrl(avatarUrl);
            sysUserRepository.save(user);
            Map<String, Object> resp = new HashMap<>();
            resp.put("avatarUrl", avatarUrl);
            resp.put("message", "头像上传成功");
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            return badRequest("头像上传失败: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/verify")
    public ResponseEntity<?> submitRealNameVerify(@PathVariable Long id, @RequestBody Map<String, String> body) {
        var opt = sysUserRepository.findById(id);
        if (opt.isEmpty()) return badRequest("用户未找到");
        SysUser user = opt.get();

        String realName = body.get("realName");
        String idCard = body.get("idCard");

        if (realName == null || realName.trim().isBlank()) {
            return badRequest("真实姓名不能为空");
        }
        if (idCard == null || idCard.trim().isBlank()) {
            return badRequest("身份证号不能为空");
        }
        realName = realName.trim();
        idCard = idCard.trim().toUpperCase();

        if (realName.length() < 2 || realName.length() > 50) {
            return badRequest("姓名长度不正确");
        }
        if (!ID_CARD_PATTERN.matcher(idCard).matches()) {
            return badRequest("身份证号格式不正确，请输入18位有效身份证号码");
        }

        // 从身份证号第17位（倒数第2位）自动推导性别：奇数为男，偶数为女
        char genderDigitChar = idCard.charAt(16);
        if (Character.isDigit(genderDigitChar)) {
            int genderDigit = genderDigitChar - '0';
            int derivedGender = (genderDigit % 2 == 1) ? 1 : 2; // 1=男 2=女
            user.setGender(derivedGender);
        }

        user.setRealName(realName);
        user.setIdCard(idCard);
        user.setIsVerified(1);
        sysUserRepository.save(user);

        Map<String, Object> resp = new HashMap<>();
        resp.put("message", "实名认证成功");
        resp.put("isVerified", 1);
        resp.put("gender", user.getGender());
        return ResponseEntity.ok(resp);
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
