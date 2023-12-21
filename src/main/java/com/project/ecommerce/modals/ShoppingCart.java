package com.project.ecommerce.modals;

import com.project.ecommerce.beans.Book;
import com.project.ecommerce.repository.BookRepos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ShoppingCart {

    @Autowired
    BookRepos bookRepos;

    private final List<Book> books=new ArrayList<>();
    private Double totalCost=0.0;

    public void clearCart(){
        books.clear();
        totalCost=0.0;
    }
    public boolean addBook(Long bookId){
        Book book=bookRepos.findById(bookId).orElse(null);
        if(book!=null){
            if(books.contains(book)) return false;
            books.add(book);
            totalCost+=book.getDeliveryCost()+book.getPrice();
            return true;
        }
        return false;
    }
    public boolean removeBook(Long bookId){
        Book book=bookRepos.findById(bookId).orElse(null);
        if(book!=null){
            for (Book bookInCart:books) {
                if (book.getId().equals(bookInCart.getId())){
                    System.out.println("the book exist we are going to remove it in sha lah");
                    books.remove(bookInCart);
                    totalCost=totalCost-bookInCart.getDeliveryCost()-bookInCart.getPrice();
                    return true;
                }
            }
        }
        return false;
    }
    public List<Book> getShoppingCart(){
        return books;
    }
    public Double getTotalCost(){
        return totalCost;
    }

}