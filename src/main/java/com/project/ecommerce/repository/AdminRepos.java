package com.project.ecommerce.repository;

import com.project.ecommerce.beans.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepos extends JpaRepository<Admin, Long > {
    Admin findByLogin(String login);
}
