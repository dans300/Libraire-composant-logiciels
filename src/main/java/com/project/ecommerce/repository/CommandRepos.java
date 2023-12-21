package com.project.ecommerce.repository;

import com.project.ecommerce.beans.Client;
import com.project.ecommerce.beans.Command;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommandRepos extends JpaRepository<Command, Long > {

}
