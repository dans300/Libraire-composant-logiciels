package com.project.ecommerce.controllers;

import com.project.ecommerce.beans.*;
import com.project.ecommerce.modals.ShoppingCart;
import com.project.ecommerce.repository.BookRepos;
import com.project.ecommerce.security.JwtUtils;
import com.project.ecommerce.security.RequiresRole;
import com.project.ecommerce.security.Roles;
import com.project.ecommerce.services.ClientService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class ClientController {


    @Autowired
    ClientService clientService;

    @Autowired
    BookRepos bookRepos;

    @Autowired
    private ShoppingCart shoppingCart;

    @GetMapping({"", "/"})
    public String home(Model model, HttpServletRequest request, @RequestParam(name = "key-word", required = false) String keyWord) {
        boolean authenticated=clientService.isAuthenticated(request);
        model.addAttribute("authenticated", authenticated);

        List<Book> books;
        if(keyWord!=null)
        {
            books=clientService.searchEngine(keyWord);
        }
        else{
            books=clientService.getBooks();
        }

        model.addAttribute("books", books);
        return "index";
    }

    @GetMapping("/add-book")
    public ResponseEntity<String> addBook(@RequestParam("bookId") Long bookId) {
        // Your logic to add the book to the cart
        boolean bookAdded = shoppingCart.addBook(bookId);

        // Return a JSON response
        if (bookAdded) {
            return ResponseEntity.ok("Book added to cart successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to add book to cart");
        }
    }

    @GetMapping("/remove-book")
    public ResponseEntity<String> removeBook(@RequestParam("bookId") Long bookId) {
        // Your logic to add the book to the cart
        boolean bookRemoved = shoppingCart.removeBook(bookId);

        // Return a JSON response
        if (bookRemoved) {
            return ResponseEntity.ok("Book removed from cart successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to remove the book from cart");
        }
    }


    @GetMapping({"/login", "/login/"})
    public String redirectLogin(Model model, HttpServletRequest request,@RequestParam(name = "redirect-to", required = false) String redirectUri){

        model.addAttribute("redirect","");
        if(Objects.equals(redirectUri, "/panel")){
            model.addAttribute("redirect","?redirect-to=/panel");
            return "login";
        }
        request.getSession().invalidate();
        return "login";
    }

    @PostMapping("/login")
    public String Login( HttpServletRequest request,  @RequestParam("login") String login,  @RequestParam("password") String password, @RequestParam(name = "redirect-to", required = false)  String redirectUri){
        AppUser user=clientService.login(login,password);
        if(user!=null){
            String role="";
            String redirect="";

            if(user.getClass()==Admin.class){
                role=Roles.ROLE_ADMIN.toString();
                redirect="redirect:/admin";
            }
            else{
                if(user.getClass()==Library.class){
                    role=Roles.ROLE_LIBRARY.toString();
                    redirect="redirect:/library";
                }
                else{
                    if(user.getClass()== Client.class){
                        role=Roles.ROLE_CLIENT.toString();
                        redirect="redirect:/";
                        if (Objects.equals(redirectUri, "/panel")){
                            redirect="redirect:/panel";
                        }
                    }
                }
            }

            String token=JwtUtils.generateToken(role,user.getId());
            request.getSession().setAttribute("token", token);
            return redirect;
        }

        if (Objects.equals(redirectUri, "/panel")){
            return "redirect:/login?redirect-to=/panel&authentication=false";
        }

        return "redirect:/login?authentication=false";
    }

    @GetMapping({"/register", "/register/"})
    public String redirectRegister(Model model,@RequestParam(name = "redirect-to", required = false) String redirectUri){
        model.addAttribute("redirect","");
        if(Objects.equals(redirectUri, "/panel")){
            model.addAttribute("redirect","?redirect-to=/panel");
            return "register";
        }
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam("name") String name, @RequestParam("email") String email, @RequestParam("address") String address, @RequestParam("login") String login, @RequestParam("password") String password, @RequestParam(name = "redirect-to", required = false)  String redirectUri){

        Client client=new Client();
        client.setFullName(name);
        client.setEmail(email);
        client.setAddress(address);
        client.setLogin(login);
        client.setPassword(JwtUtils.hashPassword(password));

        if(clientService.register(client)){
            if(Objects.equals(redirectUri, "/panel")){
                return "redirect:/login?redirect-to=/panel&accountCreated=true";
            }
            return "redirect:/login?accountCreated=true";
        }
        else{
            if(Objects.equals(redirectUri, "/panel")){
                return "redirect:/register?redirect-to=/panel&accountCreated=false";
            }
            return "redirect:/register?accountCreated=false";
        }
    }

    @GetMapping({"/panel","/panel/"})
    public String getPanel(Model model, HttpServletRequest request){
        boolean authenticated=clientService.isAuthenticated(request);
        model.addAttribute("authenticated", authenticated);

        List<Book> books=shoppingCart.getShoppingCart();
        model.addAttribute("books", books);

        Double totalCost=shoppingCart.getTotalCost();
        model.addAttribute("totalCost", totalCost);

        return "panel";
    }

    @RequiresRole(Roles.ROLE_CLIENT)
    @PostMapping("/client/add-command")
    public String addCommand(Model model, HttpServletRequest request){
        Command command=clientService.addCommand(request);
        if(command==null)
        {
            return "redirect:/panel?commandSaved=false";
        }
        else
        {
            boolean authenticated=clientService.isAuthenticated(request);
            model.addAttribute("authenticated", authenticated);
            model.addAttribute("command",command);
            return "panel";
        }
    }

    @RequiresRole(Roles.ROLE_CLIENT)
    @GetMapping("/client/add-command")
    public String redirectToPanel(){
        return "redirect:/panel";
    }

    @RequiresRole(Roles.ROLE_CLIENT)
    @GetMapping({"/client/commands","/client/commands/"})
    public String getCommands(Model model, HttpServletRequest request){
        boolean authenticated=clientService.isAuthenticated(request);
        model.addAttribute("authenticated", authenticated);

        List<Command> commands=clientService.getCommands(request);
        model.addAttribute("commands", commands);
        return "my-commands";
    }

    @RequiresRole(Roles.ROLE_CLIENT)
    @PostMapping("/client/submit-review")
    public String submitReview(HttpServletRequest request, @RequestParam("order-id") Long orderId, @RequestParam("rating") int rating){
        boolean rated=clientService.submitReview(request,orderId,rating);
        return "redirect:/client/commands?commandRated="+rated;
    }

}
