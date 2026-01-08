package com.hijabshop.web;

import com.hijabshop.entities.CustomerProfile;
import com.hijabshop.entities.InventoryException;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "RegisterServlet", urlPatterns = {"/RegisterServlet"})
public class RegisterServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Retrieve all parameters from the HTML form
        String name = request.getParameter("fullname");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        String pass = request.getParameter("password");
        String role = request.getParameter("role"); // Gets "Customer" or "Admin" from dropdown

        ShopDelegate delegate = new ShopDelegate();

        // 2. Create the entity with ALL fields
        CustomerProfile newUser = new CustomerProfile(name, email, phone, address, pass, role);

        try {
            // 3. Save to Database
            delegate.getFacade().registerCustomer(newUser);
            // 4. Redirect to login on success
            response.sendRedirect("login.html?msg=registered");
        } catch (InventoryException e) {
            response.getWriter().println("Error: " + e.getMessage());
        }
    }
}
