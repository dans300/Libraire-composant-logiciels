package com.project.ecommerce.beans;


import com.project.ecommerce.enums.BookState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String editor;
    private String description;
    private int pages;
    private Double price;
    private Double deliveryCost;
    private BookState state;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "book")
    private List<OrderedBook> orderedBooks=new ArrayList<>();

    @ManyToMany
    private List<Category> categories;

    @ManyToMany
    private List<Author> authors;

    @ManyToOne
    private Library library;

    @Transient
    private boolean inCart;

    @Transient
    private Double rating;

    @Transient
    private int votes;

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", editor='" + editor + '\'' +
                ", pages=" + pages +
                ", price=" + price +
                ", deliveryCost=" + deliveryCost +
                '}';
    }
}
