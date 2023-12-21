package com.project.ecommerce.repository;

import com.project.ecommerce.beans.Category;
import com.project.ecommerce.beans.OrderedBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepos extends JpaRepository<Category, Long > {

    @Query("SELECT COUNT(DISTINCT c) FROM Book b JOIN b.categories c WHERE b.library.id = :libraryId")
    long countDistinctCategoriesByLibraryId(@Param("libraryId") Long libraryId);
}
