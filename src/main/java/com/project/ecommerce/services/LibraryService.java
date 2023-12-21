package com.project.ecommerce.services;

import com.project.ecommerce.beans.*;
import com.project.ecommerce.enums.BookState;
import com.project.ecommerce.enums.OrderState;
import com.project.ecommerce.modals.BooksSellsRating;
import com.project.ecommerce.modals.LibraryDashboard;
import com.project.ecommerce.repository.*;
import com.project.ecommerce.security.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class LibraryService {

    @Autowired
    LibraryRepos libraryRepos;

    @Autowired
    AuthorRepos authorRepos;

    @Autowired
    CategoryRepos categoryRepos;

    @Autowired
    BookRepos bookRepos;

    @Autowired
    OrderedBookRepos orderedBookRepos;

    public boolean addBook( String title, String editor, String description, int pages, Double price, Double deliveryCost, String state, String authorsString, List<Long> categoriesIds, HttpServletRequest request){
        try {
            String token = request.getSession().getAttribute("token").toString();
            Long libraryId= Long.valueOf(JwtUtils.extractUserId(token));

            Library library=libraryRepos.findById(libraryId).orElse(null);
            if(library==null) return false;

            String[] authorsNames=authorsString.split(",");
            List<Author> authors=new ArrayList<>();
            for (String authorName:authorsNames) {
                Author author=authorRepos.findByFullName(authorName);
                if(author==null){
                    author=new Author();
                    author.setFullName(authorName);
                    authorRepos.save(author);
                }
                authors.add(author);
            }

            List<Category> categories=new ArrayList<>();
            if(categoriesIds!=null){
                for (Long categoryId:categoriesIds) {
                    categoryRepos.findById(categoryId).ifPresent(categories::add);
                }
            }


            Book book=new Book();
            book.setTitle(title);
            book.setEditor(editor);
            book.setDescription(description);
            book.setPages(pages);
            book.setPrice(price);
            book.setDeliveryCost(deliveryCost);
            book.setState(BookState.valueOf(state));
            book.setCategories(categories);
            book.setAuthors(authors);
            book.setLibrary(library);

            bookRepos.save(book);
            List<Book> books=library.getBooks();
            books.add(book);
            library.setBooks(books);
            libraryRepos.save(library);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    public boolean register(Library library){
        try {
            library.setAccepted(false);
            libraryRepos.save(library);
            return true;
        }
        catch (Exception e){
            return false;
        }

    }

    public List<OrderedBook> getOrders(HttpServletRequest request){
        Library library=extractLibrary(request);
        if(library!=null){
            return orderedBookRepos.findAllByLibrary(library.getId());
        }
        return new ArrayList<>();
    }

    public boolean shippedOrder(Long orderedBookId, HttpServletRequest request){
        Library library=extractLibrary(request);
        if(library==null) return false;

        OrderedBook orderedBook=orderedBookRepos.findById(orderedBookId).orElse(null);
        if(orderedBook!=null){
            if(Objects.equals(orderedBook.getBook().getLibrary().getId(), library.getId())){
                orderedBook.setState(OrderState.DELIVERED);
                orderedBookRepos.save(orderedBook);
                return true;
            }
        }
        return false;
    }

    public boolean cancelOrder(Long orderedBookId, String reason, HttpServletRequest request){
        Library library=extractLibrary(request);
        if(library==null) return false;

        OrderedBook orderedBook=orderedBookRepos.findById(orderedBookId).orElse(null);
        if(orderedBook!=null){
            if(Objects.equals(orderedBook.getBook().getLibrary().getId(), library.getId())){
                orderedBook.setState(OrderState.CANCELED);
                orderedBook.setReason(reason);
                orderedBookRepos.save(orderedBook);
                return true;
            }
        }
        return false;
    }

    public List<BooksSellsRating> getBooksSellsRating(HttpServletRequest request){
        try {

            Library library = extractLibrary(request);
            if (library == null) return new ArrayList<>();

            List<BooksSellsRating> booksSellsRatings=new ArrayList<>();

            List<Book> books=library.getBooks();
            for (Book book:books) {
                int votes=0;
                int sells=0;
                Double rating=0.0;
                List<OrderedBook> orderedBooks=book.getOrderedBooks();
                for (OrderedBook orderedBook:orderedBooks) {
                    if(orderedBook.getState()!=null && orderedBook.getState().equals(OrderState.DELIVERED)){
                        sells+=1;
                        if(orderedBook.getRating()!=null){
                            votes+=1;
                            rating+=orderedBook.getRating();
                        }
                    }
                }

                BooksSellsRating bookSellsRating=new BooksSellsRating();
                bookSellsRating.setBook(book);
                bookSellsRating.setSells(sells);
                if(rating>0) rating=rating/votes;
                bookSellsRating.setRating(rating);

                booksSellsRatings.add(bookSellsRating);
            }
            return booksSellsRatings;
        }
        catch (Exception e){
            return new ArrayList<>();
        }
    }

    public LibraryDashboard getDashboardStatistics(HttpServletRequest request){
        LibraryDashboard statistics=new LibraryDashboard();
        try {

            Library library=extractLibrary(request);
            if(library==null) return null;

            long categories=categoryRepos.countDistinctCategoriesByLibraryId(library.getId());

            int sells=0;
            int pending=0;
            double revenue=0.0;
            int votes=0;
            Double rating=0.0;
            int orders=0;

            List<Book> books=library.getBooks();
            for (Book book:books) {
                List<OrderedBook> orderedBooks=book.getOrderedBooks();
                for (OrderedBook orderedBook:orderedBooks) {
                    orders+=1;
                    if(orderedBook.getState()==OrderState.PENDING){
                        pending+=1;
                    }
                    else{
                        if(orderedBook.getState().equals(OrderState.DELIVERED)){
                            sells+=1;
                            revenue+=(orderedBook.getBook().getPrice()+orderedBook.getBook().getDeliveryCost());
                            if(orderedBook.getRating()!=null){
                                votes+=1;
                                rating+=orderedBook.getRating();
                            }
                        }
                    }
                }
            }

            List<Library> libraries=libraryRepos.findAll();
            double shippingPosition= 0;
            double revenuePosition= 0;
            for (Library lib:libraries) {

                LibraryDashboard libStatistics=getStatistics(lib);

                if (libStatistics.getRevenue()<=revenue){
                    revenuePosition+=1;
                }

                if(libStatistics.getSells()<=sells){
                    shippingPosition+=1;
                }

            }

            shippingPosition= (100*shippingPosition)/libraries.size();
            revenuePosition=(100*revenuePosition)/libraries.size();

            if(votes!=0) rating=rating/votes;

            statistics.setBooks(library.getBooks().size());
            statistics.setCategories(categories);
            statistics.setOrderedBooks(orders);
            statistics.setPendingOrders(pending);
            statistics.setSells(sells);
            statistics.setRevenue(revenue);
            statistics.setRating(rating);
            statistics.setVotes(votes);
            statistics.setShippingPosition(shippingPosition);
            statistics.setRevenuePosition(revenuePosition);

            return statistics;
        }
        catch (Exception e){
            return null;
        }
    }

    private LibraryDashboard getStatistics(Library library){
        LibraryDashboard statistics=new LibraryDashboard();
        int sells=0;
        double revenue=0.0;

        List<Book> books=library.getBooks();
        for (Book book:books) {
            List<OrderedBook> orderedBooks=book.getOrderedBooks();
            for (OrderedBook orderedBook:orderedBooks) {
                if(orderedBook.getState().equals(OrderState.DELIVERED)){
                    sells+=1;
                    revenue+=(orderedBook.getBook().getPrice()+orderedBook.getBook().getDeliveryCost());
                }
            }
        }
        statistics.setSells(sells);
        statistics.setRevenue(revenue);

        return statistics;
    }
    private Library extractLibrary(HttpServletRequest request){
        try {
            String token = request.getSession().getAttribute("token").toString();
            Long libraryId = Long.valueOf(JwtUtils.extractUserId(token));

            return libraryRepos.findById(libraryId).orElse(null);
        }
        catch (Exception e){
            return null;
        }
    }
}
