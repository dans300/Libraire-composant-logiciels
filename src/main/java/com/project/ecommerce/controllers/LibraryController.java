package com.project.ecommerce.controllers;

import com.project.ecommerce.beans.*;
import com.project.ecommerce.modals.BooksSellsRating;
import com.project.ecommerce.modals.LibraryDashboard;
import com.project.ecommerce.repository.CategoryRepos;
import com.project.ecommerce.security.JwtUtils;
import com.project.ecommerce.security.RequiresRole;
import com.project.ecommerce.security.Roles;
import com.project.ecommerce.services.LibraryService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/library")
public class LibraryController {

    @Autowired
    LibraryService libraryService;

    @Autowired
    CategoryRepos categoryRepos;

    @RequiresRole(Roles.ROLE_LIBRARY)
    @GetMapping({"","/"})
    public String dashboard(Model model, HttpServletRequest request){
        LibraryDashboard statistics=libraryService.getDashboardStatistics(request);
        model.addAttribute("statistics", statistics);
        return "library-dashboard";
    }

    @RequiresRole(Roles.ROLE_LIBRARY)
    @GetMapping({"/commands","/commands/"})
    public String commands(Model model, HttpServletRequest request){
        List<OrderedBook> orders=libraryService.getOrders(request);
        model.addAttribute("orders", orders);
        return "library-commands";
    }

    @RequiresRole(Roles.ROLE_LIBRARY)
    @GetMapping({"/books","/books/"})
    public String books(Model model, HttpServletRequest request){

        List<BooksSellsRating> books=libraryService.getBooksSellsRating(request);
        model.addAttribute("booksSellsRating", books);

        List<Category> categories=categoryRepos.findAll();
        model.addAttribute("categories", categories);

        return "library-books";
    }

    @RequiresRole(Roles.ROLE_LIBRARY)
    @GetMapping("/accept-order")
    public String acceptOrder(HttpServletRequest request, @RequestParam("orderId") Long orderId){
        boolean success=libraryService.shippedOrder(orderId, request);
        return "redirect:/library/commands?acceptOrder="+success;
    }

    @RequiresRole(Roles.ROLE_LIBRARY)
    @PostMapping("/decline-order")
    public String declineOrder(HttpServletRequest request, @RequestParam("orderId") Long orderId, @RequestParam("reason") String reason){
        boolean success=libraryService.cancelOrder(orderId, reason,request);
        return "redirect:/library/commands?declineOrder="+success;
    }

    @RequiresRole(Roles.ROLE_LIBRARY)
    @PostMapping("/add-book")
    public String addBook(HttpServletRequest request, @RequestParam("title") String title, @RequestParam("editor") String editor, @RequestParam("description") String description, @RequestParam("pages") int pages, @RequestParam("price") Double price, @RequestParam("deliveryCost") Double deliveryCost, @RequestParam(value = "state",required = false) String state, @RequestParam("authors") String authors, @RequestParam(value = "categories",required = false) List<Long> categories){
        boolean success=libraryService.addBook(title,editor,description,pages,price, deliveryCost,state,authors,categories,request);
        return "redirect:/library/books?bookSaved="+success;
    }

    @GetMapping({"/register","/register/"})
    public String register(){
        return "register-library";
    }

    @PostMapping("/register")
    public String register(@RequestParam("name") String name, @RequestParam("email") String email, @RequestParam("address") String address, @RequestParam("login") String login, @RequestParam("password") String password){
        Library library=new Library();
        library.setName(name);
        library.setEmail(email);
        library.setAddress(address);
        library.setLogin(login);
        library.setPassword(JwtUtils.hashPassword(password));

        if(libraryService.register(library)){
            return "redirect:/login?libraryAccountCreated=true";
        }
        else{
            return "redirect:/library/register?accountCreated=false";
        }
    }
}
