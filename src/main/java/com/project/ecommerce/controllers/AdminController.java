package com.project.ecommerce.controllers;

import com.project.ecommerce.beans.*;
import com.project.ecommerce.modals.AdminDashboard;
import com.project.ecommerce.modals.SellersSellsRating;
import com.project.ecommerce.repository.BookRepos;
import com.project.ecommerce.repository.ClientRepos;
import com.project.ecommerce.repository.CommandRepos;
import com.project.ecommerce.repository.LibraryRepos;
import com.project.ecommerce.security.RequiresRole;
import com.project.ecommerce.security.Roles;
import com.project.ecommerce.services.AdminService;
import com.project.ecommerce.services.LibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    AdminService adminService;

    @Autowired
    BookRepos bookRepos;

    @Autowired
    LibraryRepos libraryRepos;

    @Autowired
    ClientRepos clientRepos;

    @Autowired
    CommandRepos commandRepos;

    @Autowired
    LibraryService libraryService;


    @RequiresRole(Roles.ROLE_ADMIN)
    @GetMapping({"","/"})
    public String dashboard(Model model)
    {
        AdminDashboard statistics=adminService.getDashboardStatistics();
        model.addAttribute("statistics", statistics);
        return "admin-dashboard";
    }

    @RequiresRole(Roles.ROLE_ADMIN)
    @GetMapping({"/libraries","/libraries/"})
    public String libraries(Model model){
        List<SellersSellsRating> sellsRating=adminService.sellsPerLibrary();
        model.addAttribute("sellersSellsRating", sellsRating);
        return "libraries";
    }

    @RequiresRole(Roles.ROLE_ADMIN)
    @GetMapping({"/clients", "/clients/"})
    public String clients(Model model){

        List<Client> clients=adminService.purchasesPerClient();
        model.addAttribute("clients", clients);

        return "clients";
    }

    @RequiresRole(Roles.ROLE_ADMIN)
    @PostMapping("/add-category")
    public String addCategory(@RequestParam("category") String category){
        return "redirect:/admin?categorySaved="+adminService.saveCategory(category);
    }

    @RequiresRole(Roles.ROLE_ADMIN)
    @GetMapping("/accept-library")
    public String acceptLibrary(@RequestParam("library-id") Long libraryId){
       return  "redirect:/admin/libraries?libraryAccepted="+adminService.acceptLibrary(libraryId);
    }

    @RequiresRole(Roles.ROLE_ADMIN)
    @GetMapping("/decline-library")
    public String declineLibrary(@RequestParam("library-id") Long libraryId){
        return  "redirect:/admin/libraries?libraryDeclined="+adminService.declineLibrary(libraryId);
    }
}
