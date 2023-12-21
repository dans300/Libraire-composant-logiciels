package com.project.ecommerce.modals;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LibraryDashboard {

    private int books;
    private long categories;

    private int orderedBooks;
    private int pendingOrders;

    private int sells;
    private double revenue;

    private Double rating;
    private int votes;

    private Double revenuePosition;
    private Double shippingPosition;
}
