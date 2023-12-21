package com.project.ecommerce.modals;

import com.project.ecommerce.beans.Category;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class AdminDashboard {

    private long libraries;
    private long pendingRequest;

    private long clients;
    private long commands;

    private double circulation;
    private long orders;

    private List<Category> categories;

}
