package com.hijabshop.web;

import com.hijabshop.entities.CustomerProfile;
import com.hijabshop.entities.InventoryFacadeRemote;
import java.io.IOException;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {

    @EJB
    private InventoryFacadeRemote inventoryFacade;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Basic null check to avoid crashes
        if (email != null) email = email.trim();
        if (password != null) password = password.trim();

        // 1. ADMIN CHECK (Hardcoded for security simplicity)
        if ("admin@hijabshop.com".equalsIgnoreCase(email) && "admin123".equals(password)) {
            HttpSession session = request.getSession();
            session.setAttribute("user", "Admin");
            session.setAttribute("role", "ADMIN");
            
            // FIX: Redirect to the Servlet URL, not an HTML file
            response.sendRedirect("Dashboard"); 
            return;
        } 

        // 2. CUSTOMER CHECK (Database)
        try {
            CustomerProfile customer = inventoryFacade.loginCustomer(email, password);

            if (customer != null) {
                HttpSession session = request.getSession();
                session.setAttribute("user", customer.getFullName());
                session.setAttribute("role", "CUSTOMER");
                response.sendRedirect("index.jsp");
            } else {
                response.sendRedirect("login.html?error=invalid");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("login.html?error=exception");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}