package com.project.ecommerce.services;

import com.project.ecommerce.beans.*;
import com.project.ecommerce.enums.BookState;
import com.project.ecommerce.enums.OrderState;
import com.project.ecommerce.modals.ShoppingCart;
import com.project.ecommerce.repository.*;
import com.project.ecommerce.security.JwtUtils;
import com.project.ecommerce.security.Roles;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ClientService {

    @Autowired
    AdminRepos adminRepos;

    @Autowired
    LibraryRepos libraryRepos;

    @Autowired
    ClientRepos clientRepo;

    @Autowired
    BookRepos bookRepos;

    @Autowired
    CommandRepos commandRepos;

    @Autowired
    OrderedBookRepos orderedBookRepos;

    @Autowired
    private ShoppingCart shoppingCart;


    public boolean isAuthenticated(HttpServletRequest request){
        try{
            String token = request.getSession().getAttribute("token").toString();
            return JwtUtils.validateToken(token, String.valueOf(Roles.ROLE_CLIENT));
        }
        catch (Exception e){
            return false;
        }
    }

    public AppUser login(String login, String password){
        password= JwtUtils.hashPassword(password);
        Admin admin=adminRepos.findByLogin(login);
        if(admin!=null && admin.getPassword().equals(password)){
            return admin;
        }
        else{
            Library library=libraryRepos.findByLogin(login);

            if(library!=null && library.isAccepted() && library.getPassword().equals(password)){
                return library;
            }
            else{
                Client client=clientRepo.findByLogin(login);
                if(client!=null && client.getPassword().equals(password)){
                    return client;
                }
            }
        }
        return null;
    }

    public boolean register(Client client){
        try {
            clientRepo.save(client);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    private List<Book> getBooksInfo(List<Book> books){
        List<Book> cart=shoppingCart.getShoppingCart();
        for (int i=0;i<books.size();i++) {
            List<OrderedBook> orderedBooks=books.get(i).getOrderedBooks();
            Double rating=0.0;
            int votes=0;
            for (OrderedBook orderedBook:orderedBooks) {
                if(orderedBook.getRating()!=null){
                    votes+=1;
                    rating+=orderedBook.getRating();
                }
            }
            if(votes!=0) rating=rating/votes;

            books.get(i).setRating(rating);
            books.get(i).setVotes(votes);

            books.get(i).setInCart(false);
            for (Book cartBook:cart) {
                if(books.get(i).getId().equals(cartBook.getId())){
                    books.get(i).setInCart(true);
                    break;
                }
            }
        }
        return books;
    }

    public List<Book> getBooks(){
        List<Book> books=bookRepos.findAll();
        return getBooksInfo(books);
    }

    private Client extractClient(HttpServletRequest request){
        try {
            String token = request.getSession().getAttribute("token").toString();
            Long clientId = Long.valueOf(JwtUtils.extractUserId(token));

            return clientRepo.findById(clientId).orElse(null);
        }
        catch (Exception e){
            return null;
        }
    }

    public List<Command> getCommands(HttpServletRequest request){
        Client client=extractClient(request);
        if(client!=null){
            return client.getCommands();
        }
        return new ArrayList<>();
    }

    public Command addCommand(HttpServletRequest request){
        try {
            List<Book> books=shoppingCart.getShoppingCart();
            if(books.isEmpty()) return null;

            Client client=extractClient(request);
            if (client == null) return null;

            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy");
            String date=dateFormat.format(new Date());

            Command command=new Command();
            command.setClient(client);
            command.setDate(date);
            commandRepos.save(command);

            List<Command> commands=client.getCommands();
            commands.add(command);
            client.setCommands(commands);
            clientRepo.save(client);

            List<OrderedBook> orders=new ArrayList<>();
            for (Book book: books) {
                OrderedBook orderedBook=new OrderedBook();
                orderedBook.setBook(book);
                orderedBook.setCommand(command);
                orderedBookRepos.save(orderedBook);
                orders.add(orderedBook);
                List<OrderedBook> orderedBooks=book.getOrderedBooks();
                orderedBooks.add(orderedBook);
                book.setOrderedBooks(orderedBooks);
                bookRepos.save(book);
            }

            command.setOrderedBooks(orders);
            commandRepos.save(command);
            System.out.println("done hamdo li lah");

            shoppingCart.clearCart();
            return command;
        }
        catch (Exception e){
            return null;
        }
    }

    public boolean submitReview(HttpServletRequest request, Long orderId, int rating){
        try {
            Client client=extractClient(request);
            OrderedBook orderedBook=orderedBookRepos.findById(orderId).orElse(null);
            if(orderedBook!=null && client!=null){
                if(
                        orderedBook.getCommand().getClient().getId().equals(client.getId())
                                && orderedBook.getRating()==null
                                && orderedBook.getState().equals(OrderState.DELIVERED)
                ){
                    orderedBook.setRating((double) rating);
                    orderedBookRepos.save(orderedBook);
                    return true;
                }
            }
            return false;
        }
        catch (Exception e){
            return false;
        }

    }

    public List<Book> searchEngine(String keyWord){
        List<Book> allBooks=bookRepos.findAll();
        List<Book> books=new ArrayList<>();

        for (Book book:allBooks) {
            boolean goNext=false;
            // search based on title
            if(book.getTitle().contains(keyWord)){
                books.add(book);
                continue;
            }

            // search based on editors
            if(book.getEditor().contains(keyWord)){
                books.add(book);
                continue;
            }

            // search based on library name
            if(book.getLibrary().getName().contains(keyWord)){
                books.add(book);
                continue;
            }


            // search based on authors
            List<Author> authors=book.getAuthors();
            for (Author author:authors) {
                if(author.getFullName().contains(keyWord)){
                    books.add(book);
                    goNext=true;
                    break;
                }
            }

            if(!goNext){
                // continue searching based on categories
                List<Category> categories=book.getCategories();
                for (Category category:categories) {
                    if(category.getDescription().contains(keyWord)){
                        books.add(book);
                        break;
                    }
                }
            }
        }

        // continue searching based on states
        try {
            BookState state= BookState.valueOf(keyWord);
            List<Book> booksByState=bookRepos.findAllByState(state);
            for (Book book:booksByState) {
                if(!books.contains(book)){
                    books.add(book);
                }
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }

        return getBooksInfo(books);
    }

}
