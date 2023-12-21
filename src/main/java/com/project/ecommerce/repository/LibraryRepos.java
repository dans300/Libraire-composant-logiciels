package com.project.ecommerce.repository;

import com.project.ecommerce.beans.Admin;
import com.project.ecommerce.beans.Library;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibraryRepos extends JpaRepository<Library, Long > {

    Library findByLogin(String login);
    long countLibrariesByAccepted(boolean accepted);
}
