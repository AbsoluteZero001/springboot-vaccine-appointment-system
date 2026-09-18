package com.springboot.vaccineappointmentsystem.repository;

import com.springboot.vaccineappointmentsystem.entity.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SysUserRepository extends JpaRepository<SysUser, Long> {
    Optional<SysUser> findByUsername(String username);

    Optional<SysUser> findByPhone(String phone);

    List<SysUser> findByRole(String role);

    long countByRole(String role);

    boolean existsByUsername(String username);

    boolean existsByPhone(String phone);
}
