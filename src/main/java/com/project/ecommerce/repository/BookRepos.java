package com.project.ecommerce.repository;

import com.project.ecommerce.beans.Book;
import com.project.ecommerce.enums.BookState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepos extends JpaRepository<Book, Long > {
    List<Book> findAllByState(BookState state);
}
