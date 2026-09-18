package com.springboot.vaccineappointmentsystem.controller;

import com.springboot.vaccineappointmentsystem.entity.FamilyMember;
import com.springboot.vaccineappointmentsystem.service.FamilyMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/family-members")
public class FamilyMemberController {

    @Autowired
    private FamilyMemberService familyMemberService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FamilyMember>> getMembers(@PathVariable Long userId) {
        return ResponseEntity.ok(familyMemberService.getMembersByUser(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMember(@PathVariable Long id) {
        return familyMemberService.getMemberById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> {
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "家庭成员不存在");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
                });
    }

    @PostMapping
    public ResponseEntity<?> addMember(@RequestBody Map<String, Object> payload) {
        try {
            Long userId = Long.valueOf(payload.get("userId").toString());
            FamilyMember member = new FamilyMember();
            member.setName(payload.get("name").toString());
            member.setIdCard(payload.get("idCard") != null ? payload.get("idCard").toString() : null);
            member.setPhone(payload.get("phone") != null ? payload.get("phone").toString() : null);
            member.setRemark(payload.get("remark") != null ? payload.get("remark").toString() : null);
            FamilyMember saved = familyMemberService.addMember(userId, member);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMember(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        try {
            Long userId = Long.valueOf(payload.get("userId").toString());
            FamilyMember details = new FamilyMember();
            details.setName(payload.get("name").toString());
            details.setIdCard(payload.get("idCard") != null ? payload.get("idCard").toString() : null);
            details.setPhone(payload.get("phone") != null ? payload.get("phone").toString() : null);
            details.setRemark(payload.get("remark") != null ? payload.get("remark").toString() : null);
            return ResponseEntity.ok(familyMemberService.updateMember(id, userId, details));
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMember(@PathVariable Long id, @RequestParam Long userId) {
        try {
            familyMemberService.deleteMember(id, userId);
            Map<String, String> msg = new HashMap<>();
            msg.put("message", "已删除");
            return ResponseEntity.ok(msg);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
