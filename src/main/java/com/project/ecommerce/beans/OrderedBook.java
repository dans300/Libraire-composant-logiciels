package com.project.ecommerce.beans;

import com.project.ecommerce.enums.OrderState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class OrderedBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    private Command command;

    private Double rating;

    private OrderState state=OrderState.PENDING;

    private String reason;

    @Override
    public String toString() {
        return "OrderedBook{" +
                "id=" + id +
                '}';
    }
}
