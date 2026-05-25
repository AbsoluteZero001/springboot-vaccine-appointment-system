package com.springboot.vaccineappointmentsystem.service;

import com.springboot.vaccineappointmentsystem.entity.FamilyMember;

import java.util.List;
import java.util.Optional;

public interface FamilyMemberService {
    FamilyMember addMember(Long userId, FamilyMember member);

    FamilyMember updateMember(Long memberId, Long userId, FamilyMember details);

    void deleteMember(Long memberId, Long userId);

    List<FamilyMember> getMembersByUser(Long userId);

    Optional<FamilyMember> getMemberById(Long memberId);
}
