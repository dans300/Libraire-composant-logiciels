package com.project.ecommerce.repository;

import com.project.ecommerce.beans.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepos extends JpaRepository<Author, Long > {

    Author findByFullName(String fullName);
}
