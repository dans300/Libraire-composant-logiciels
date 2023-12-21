package com.project.ecommerce.services;

import com.project.ecommerce.beans.*;
import com.project.ecommerce.enums.OrderState;
import com.project.ecommerce.modals.AdminDashboard;
import com.project.ecommerce.modals.SellersSellsRating;
import com.project.ecommerce.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminService {

    @Autowired
    AdminRepos adminRepos;

    @Autowired
    LibraryRepos libraryRepos;

    @Autowired
    OrderedBookRepos orderedBookRepos;

    @Autowired
    CategoryRepos categoryRepos;

    @Autowired
    ClientRepos clientRepos;

    @Autowired
    CommandRepos commandRepos;

    @Autowired
    LibraryService libraryService;

    @Autowired
    EmailService emailService;

    public List<OrderedBook> getOrders(Long libraryId){
        return orderedBookRepos.findAllByLibrary(libraryId);
    }

    public boolean saveCategory(String newCategory)
    {
        try {
            Category category=new Category();
            category.setDescription(newCategory);
            categoryRepos.save(category);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    public void saveAdmin(Admin admin){
        adminRepos.save(admin);
    }

    public boolean acceptLibrary(Long libraryId){
        Library library=libraryRepos.findById(libraryId).orElse(null);
        if(library!=null){
            library.setAccepted(true);
            libraryRepos.save(library);
            emailService.sendReturn(library.getEmail(),true);
            return true;
        }
        return false;
    }

    public boolean declineLibrary(Long libraryId){
        try {
            Library library=libraryRepos.findById(libraryId).orElse(null);
            if(library!=null){
             emailService.sendReturn(library.getEmail(),false);
             libraryRepos.delete(library);
             return true;
            }
            return false;
        }catch (Exception e){
            return false;
        }
    }

    public List<Category> getCategories() {
        return categoryRepos.findAll();
    }

    public List<OrderedBook> getOrders() {
        return orderedBookRepos.findAllByState(OrderState.DELIVERED);
    }

    public List<Client> getClients() {
        return clientRepos.findAll();
    }

    public List<Client> purchasesPerClient(){
        List<Client> clients=getClients();
        int clientIndex=0;
        for (Client client:clients) {
            int purchases=0;
            List<Command> commands=client.getCommands();
            for (Command command:commands) {
                purchases+=orderedBookRepos.countOrderedBookByStateAndCommand(OrderState.DELIVERED,command);
            }
            client.setPurchases(purchases);
            clients.set(clientIndex, client);
            clientIndex+=1;
        }
        return clients;
    }

    public List<SellersSellsRating> sellsPerLibrary(){
        List<SellersSellsRating> sellsRatingPerLibrary=new ArrayList<>();
        List<Library> libraries=libraryRepos.findAll();
        for (Library library:libraries) {
            int sells=0;
            double rating=0;
            int votes=0;
            if(library.isAccepted()){
                List<OrderedBook> orders=getOrders(library.getId());
                for (OrderedBook orderedBook: orders) {
                    if(orderedBook.getState().equals(OrderState.DELIVERED)){
                        sells+=1;
                        if(orderedBook.getRating()!=null){
                            votes+=1;
                            rating+=orderedBook.getRating();
                        }
                    }
                }
            }
            SellersSellsRating sellsRating=new SellersSellsRating();
            if(votes!=0) rating=rating/votes;

            sellsRating.setLibrary(library);
            sellsRating.setSells(sells);
            sellsRating.setRating(rating);

            sellsRatingPerLibrary.add(sellsRating);
        }

        return sellsRatingPerLibrary;
    }

    public AdminDashboard getDashboardStatistics(){
        AdminDashboard statistics=new AdminDashboard();

        long libraries=libraryRepos.count();
        long pendingRequest=libraryRepos.countLibrariesByAccepted(false);
        long clients=clientRepos.count();
        long commands=commandRepos.count();
        List<OrderedBook> orderedBooks=orderedBookRepos.findAllByState(OrderState.DELIVERED);
        double circulation=0;
        for (OrderedBook order:orderedBooks) {
            circulation+=order.getBook().getPrice()+order.getBook().getDeliveryCost();
        }
        long orders=orderedBookRepos.count();

        List<Category> categories=this.getCategories();

        statistics.setLibraries(libraries);
        statistics.setPendingRequest(pendingRequest);
        statistics.setClients(clients);
        statistics.setCommands(commands);
        statistics.setCirculation(circulation);
        statistics.setOrders(orders);
        statistics.setCategories(categories);

        return statistics;
    }
}
