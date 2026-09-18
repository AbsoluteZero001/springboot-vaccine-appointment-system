package com.springboot.vaccineappointmentsystem.service.impl;

import com.springboot.vaccineappointmentsystem.entity.FamilyMember;
import com.springboot.vaccineappointmentsystem.entity.SysUser;
import com.springboot.vaccineappointmentsystem.repository.FamilyMemberRepository;
import com.springboot.vaccineappointmentsystem.repository.SysUserRepository;
import com.springboot.vaccineappointmentsystem.service.FamilyMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FamilyMemberServiceImpl implements FamilyMemberService {

    @Autowired
    private FamilyMemberRepository familyMemberRepository;
    @Autowired
    private SysUserRepository sysUserRepository;

    @Override
    public FamilyMember addMember(Long userId, FamilyMember member) {
        SysUser user = sysUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        member.setUser(user);
        return familyMemberRepository.save(member);
    }

    @Override
    public FamilyMember updateMember(Long memberId, Long userId, FamilyMember details) {
        FamilyMember member = familyMemberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("家庭成员不存在"));
        if (!member.getUser().getId().equals(userId))
            throw new RuntimeException("无权操作");
        member.setName(details.getName());
        member.setIdCard(details.getIdCard());
        member.setPhone(details.getPhone());
        member.setRemark(details.getRemark());
        return familyMemberRepository.save(member);
    }

    @Override
    public void deleteMember(Long memberId, Long userId) {
        FamilyMember member = familyMemberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("家庭成员不存在"));
        if (!member.getUser().getId().equals(userId))
            throw new RuntimeException("无权操作");
        familyMemberRepository.delete(member);
    }

    @Override
    public List<FamilyMember> getMembersByUser(Long userId) {
        return familyMemberRepository.findByUserId(userId);
    }

    @Override
    public Optional<FamilyMember> getMemberById(Long memberId) {
        return familyMemberRepository.findById(memberId);
    }
}
