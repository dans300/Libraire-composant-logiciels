package com.project.ecommerce.modals;

import com.project.ecommerce.beans.Book;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BooksSellsRating {
    private Book book;
    private int sells;
    private Double rating;
}
