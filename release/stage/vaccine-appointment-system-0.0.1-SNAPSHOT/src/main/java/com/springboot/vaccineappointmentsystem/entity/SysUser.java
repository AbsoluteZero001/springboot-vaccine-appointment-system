package com.springboot.vaccineappointmentsystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "sys_user")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SysUser implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false, length = 100)
    private String password;

    @Column(unique = true, nullable = false, length = 20)
    private String phone;

    @Column(nullable = false, length = 20)
    private String role = "ROLE_USER"; // ROLE_USER or ROLE_ADMIN

    @Column(nullable = false)
    private Integer status = 1; // 0: disabled, 1: active

    @Column(length = 50)
    private String nickname;

    @Column(length = 50)
    private String realName;

    @Column(length = 18)
    private String idCard;

    @Column(length = 255)
    private String avatarUrl;

    @Column(nullable = false)
    private Integer isVerified = 0; // 0=未实名认证 1=已实名认证

    @Column
    private Integer gender; // 0=未知 1=男 2=女

    @Column
    private LocalDate birthday;

    @Column(length = 500)
    private String remark;

    private LocalDateTime lastUsernameChangeTime;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updateTime;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String r = (role != null && !role.isEmpty()) ? role : "ROLE_USER";
        return Collections.singletonList(new SimpleGrantedAuthority(r));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status == null || status == 1;
    }
}
