package com.project.ecommerce;

import com.project.ecommerce.beans.Admin;
import com.project.ecommerce.beans.Category;
import com.project.ecommerce.beans.Library;
import com.project.ecommerce.repository.CategoryRepos;
import com.project.ecommerce.repository.LibraryRepos;
import com.project.ecommerce.security.JwtUtils;
import com.project.ecommerce.services.AdminService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ECommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ECommerceApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(
            AdminService adminService
    ) {
        return args -> {
            // Code to run on application startup
            Admin admin = new Admin();
            admin.setLogin("admin");
            admin.setFullName("insa");
            admin.setPassword(JwtUtils.hashPassword("admin"));
            // Set data on the entity
            adminService.saveAdmin(admin);
        };
    }

}
