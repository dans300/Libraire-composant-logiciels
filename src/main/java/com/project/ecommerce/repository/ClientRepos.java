package com.project.ecommerce.repository;

import com.project.ecommerce.beans.Admin;
import com.project.ecommerce.beans.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepos extends JpaRepository<Client, Long > {

    Client findByLogin(String login);
}
