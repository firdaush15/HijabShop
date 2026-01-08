package com.hijabshop.web;

// Import entity class for cart items
import com.hijabshop.entities.HijabProduct;

// Import Java IO and utility classes
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

// Import Servlet and HTTP-related classes
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

// Servlet mapping for checkout process
@WebServlet(name = "CheckoutServlet", urlPatterns = {"/CheckoutServlet"})
public class CheckoutServlet extends HttpServlet {

    // Central method used by both GET and POST requests
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Set response type
        response.setContentType("text/html;charset=UTF-8");

        // =====================================
        // 1. Retrieve session and cart data
        // =====================================
        // Get current session
        HttpSession session = request.getSession();

        // Retrieve cart items stored in session
        List<HijabProduct> cart = (List<HijabProduct>) session.getAttribute("cart");

        // =====================================
        // 2. Calculate total price
        // =====================================
        // Recalculate total for display consistency
        double total = 0.0;
        if (cart != null) {
            for (HijabProduct p : cart) {
                total += p.getPrice();
            }
        }

        // =====================================
        // 3. Generate checkout page HTML
        // =====================================
        try (PrintWriter out = response.getWriter()) {

            // Basic HTML structure
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Secure Checkout</title>");

            // =====================
            // Embedded CSS Styling
            // =====================
            out.println("<style>");
            out.println("@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@600&family=Lato:wght@400;700&display=swap');");
            out.println("body { font-family: 'Lato', sans-serif; background-color: #fcf5f7; color: #555; }");

            // Main container styling
            out.println(".container { max-width: 600px; margin: 40px auto; background: white; padding: 40px; border-radius: 15px; box-shadow: 0 5px 25px rgba(0,0,0,0.05); }");
            out.println("h2 { color: #d63384; font-family: 'Playfair Display', serif; text-align: center; margin-bottom: 30px; }");

            // Checkout form styling
            out.println(".form-group { margin-bottom: 20px; }");
            out.println("label { display: block; font-weight: bold; margin-bottom: 8px; color: #333; }");
            out.println("input[type='text'], select { width: 100%; padding: 12px; border: 1px solid #ddd; border-radius: 8px; box-sizing: border-box; font-size: 14px; }");
            out.println(".row { display: flex; gap: 15px; }");
            out.println(".col { flex: 1; }");

            // Order summary box styling
            out.println(".summary { background-color: #ffeef2; padding: 15px; border-radius: 8px; margin-bottom: 25px; text-align: center; border: 1px dashed #ff8da1; }");
            out.println(".total-price { font-size: 20px; font-weight: bold; color: #d63384; }");

            // Payment button styling
            out.println(".btn-pay { background-color: #ff8da1; color: white; width: 100%; padding: 15px; border: none; border-radius: 30px; font-size: 16px; font-weight: bold; cursor: pointer; transition: background 0.3s; margin-top: 10px; }");
            out.println(".btn-pay:hover { background-color: #e06c85; }");
            out.println(".back-link { display: block; text-align: center; margin-top: 15px; text-decoration: none; color: #777; font-size: 13px; }");

            out.println("</style>");
            out.println("</head>");
            out.println("<body>");

            // =====================
            // Checkout Page Content
            // =====================
            out.println("<div class='container'>");
            out.println("<h2>Secure Checkout</h2>");

            // Order summary section
            out.println("<div class='summary'>");
            out.println("<p>Total Amount to Pay</p>");
            out.println("<div class='total-price'>RM " + String.format("%.2f", total) + "</div>");
            out.println("</div>");

            // =====================
            // Checkout Form
            // =====================
            // Form submits to OrderConfirmationServlet
            out.println("<form action='OrderConfirmationServlet' method='POST'>");

            // Customer information fields
            out.println("<div class='form-group'><label>Full Name</label><input type='text' name='name' required placeholder='e.g. Dini Amalia'></div>");
            out.println("<div class='form-group'><label>Shipping Address</label><input type='text' name='address' required placeholder='Street address, City, State'></div>");

            // Divider line
            out.println("<hr style='border: 0; border-top: 1px solid #eee; margin: 25px 0;'>");

            // Payment section
            out.println("<h3 style='font-family: Playfair Display; color: #333;'>Payment Details</h3>");

            // Payment input fields (UI only, no real payment processing)
            out.println("<div class='form-group'><label>Card Number</label><input type='text' placeholder='0000 0000 0000 0000' required></div>");

            out.println("<div class='row'>");
            out.println("<div class='col'><label>Expiry Date</label><input type='text' placeholder='MM/YY' required></div>");
            out.println("<div class='col'><label>CVV</label><input type='text' placeholder='123' required></div>");
            out.println("</div>");

            // Submit payment button
            out.println("<button type='submit' class='btn-pay'>Pay RM " + String.format("%.2f", total) + "</button>");
            out.println("</form>");

            // Link to return to cart page
            out.println("<a href='CartServlet' class='back-link'>Cancel and return to Cart</a>");
            out.println("</div>");

            out.println("</body>");
            out.println("</html>");
        }
    }

    // Handles GET requests (display checkout page)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    // Handles POST requests (if needed)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
