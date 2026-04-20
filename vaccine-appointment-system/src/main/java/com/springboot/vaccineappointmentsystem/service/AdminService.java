package com.springboot.vaccineappointmentsystem.service;

import com.springboot.vaccineappointmentsystem.entity.Admin;

import java.util.List;
import java.util.Optional;

public interface AdminService {
    Optional<Admin> login(String username, String password);
    Optional<Admin> getAdminById(Long id);
    List<Admin> getAllAdmins();
    Admin createAdmin(Admin admin);
    Admin updateAdmin(Long id, Admin adminDetails);
    void deleteAdmin(Long id);
}