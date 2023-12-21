package com.project.ecommerce.modals;

import com.project.ecommerce.beans.Library;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SellersSellsRating {
    private Library library;
    private int sells;
    private Double rating;
}
