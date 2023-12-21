package com.project.ecommerce.repository;

import com.project.ecommerce.beans.Command;
import com.project.ecommerce.enums.OrderState;
import com.project.ecommerce.beans.OrderedBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderedBookRepos extends JpaRepository<com.project.ecommerce.beans.OrderedBook, Long > {

    @Query(value = "SELECT o FROM OrderedBook o WHERE o.book.library.id=:libraryId")
    List<OrderedBook> findAllByLibrary(@Param("libraryId") Long libraryId);

    List<OrderedBook> findAllByState(OrderState state);
    long countOrderedBookByStateAndCommand(OrderState state, Command command);
}
