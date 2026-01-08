package com.hijabshop.web;

// Import required entity and exception classes
import com.hijabshop.entities.HijabProduct;
import com.hijabshop.entities.InventoryException;

// Import Java utilities and IO classes
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

// Import Servlet and HTTP-related classes
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

// Servlet mapping: handles all cart-related actions
@WebServlet(name = "CartServlet", urlPatterns = {"/CartServlet"})
public class CartServlet extends HttpServlet {

    // Central method used by both GET and POST requests
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Set response content type
        response.setContentType("text/html;charset=UTF-8");

        // =====================================
        // 1. Get current user session
        // =====================================
        // Session acts as temporary storage for the user's cart
        HttpSession session = request.getSession();

        // =====================================
        // 2. Retrieve or create shopping cart
        // =====================================
        // Get cart from session
        List<HijabProduct> cart = (List<HijabProduct>) session.getAttribute("cart");

        // If cart does not exist, create a new one
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }

        // =====================================
        // 3. Determine user action
        // =====================================
        // action = add / remove / view
        String action = request.getParameter("action");

        // SKU of the product to add or remove
        String skuToAdd = request.getParameter("sku");

        // =====================================
        // ACTION: ADD ITEM TO CART
        // =====================================
        if ("add".equals(action) && skuToAdd != null) {
            try {
                // Use delegate to access business logic / EJB
                ShopDelegate delegate = new ShopDelegate();

                // Retrieve all products from database
                HijabProduct[] allProducts = delegate.getFacade().getAllProducts();

                // Find matching product by SKU
                for (HijabProduct h : allProducts) {
                    if (h.getSku().equals(skuToAdd)) {
                        cart.add(h); // Add product to cart
                        break;
                    }
                }

                // Redirect back to product listing page
                response.sendRedirect("HijabList");
                return;

            } catch (InventoryException ex) {
                // Exception handling can be improved later
            }
        }

        // =====================================
        // ACTION: REMOVE ITEM FROM CART
        // =====================================
        if ("remove".equals(action) && skuToAdd != null) {
            for (int i = 0; i < cart.size(); i++) {
                if (cart.get(i).getSku().equals(skuToAdd)) {
                    cart.remove(i); // Remove selected item
                    break;
                }
            }
        }

        // =====================================
        // ACTION: VIEW CART (DEFAULT)
        // =====================================
        // Calculate total price of all items in cart
        double totalPrice = 0.0;
        for (HijabProduct item : cart) {
            totalPrice += item.getPrice();
        }

        // =====================================
        // Generate HTML response
        // =====================================
        try (PrintWriter out = response.getWriter()) {

            // HTML structure
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Your Shopping Cart</title>");

            // =====================
            // Embedded CSS Styling
            // =====================
            out.println("<style>");
            out.println("@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@600&family=Lato:wght@400;700&display=swap');");
            out.println("body { font-family: 'Lato', sans-serif; background-color: #fcf5f7; color: #555; text-align: center; }");

            // Navigation bar styling
            out.println(".navbar { background-color: #ffffff; padding: 15px 40px; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 2px 10px rgba(0,0,0,0.05); }");
            out.println(".navbar h1 { margin: 0; font-family: 'Playfair Display', serif; font-size: 28px; color: #d63384; }");
            out.println(".nav-links a { color: #777; text-decoration: none; margin-left: 30px; font-weight: bold; font-size: 13px; text-transform: uppercase; }");

            // Cart table styling
            out.println(".container { max-width: 800px; margin: 50px auto; background: white; padding: 40px; border-radius: 15px; box-shadow: 0 5px 25px rgba(0,0,0,0.05); }");
            out.println("table { width: 100%; border-collapse: collapse; margin-top: 20px; }");
            out.println("th { background-color: #ffeef2; color: #d63384; padding: 15px; font-family: 'Playfair Display', serif; }");
            out.println("td { padding: 15px; border-bottom: 1px solid #f0f0f0; }");
            out.println(".total { font-size: 24px; font-weight: bold; color: #d63384; margin-top: 30px; text-align: right; }");

            // Button styling
            out.println(".btn { padding: 10px 20px; border-radius: 30px; text-decoration: none; font-weight: bold; font-size: 14px; margin: 5px; display: inline-block; }");
            out.println(".btn-shop { background-color: #eee; color: #333; }");
            out.println(".btn-checkout { background-color: #ff8da1; color: white; }");
            out.println(".btn-remove { color: red; font-size: 12px; text-decoration: underline; cursor: pointer; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");

            // =====================
            // Navigation Bar
            // =====================
            out.println("<div class='navbar'>");
            out.println("<h1>🌸 HijabShop</h1>");
            out.println("<div class='nav-links'>");
            out.println("<a href='index.html'>Home</a>");
            out.println("<a href='HijabList'>Collections</a>");
            out.println("<a href='CartServlet'>Cart (" + cart.size() + ")</a>");
            out.println("</div>");
            out.println("</div>");

            // =====================
            // Cart Content
            // =====================
            out.println("<div class='container'>");
            out.println("<h2 style='font-family: Playfair Display; color: #d63384;'>Your Shopping Bag</h2>");

            // If cart is empty
            if (cart.isEmpty()) {
                out.println("<p>Your bag is currently empty.</p>");
                out.println("<br><a href='HijabList' class='btn btn-checkout'>Start Shopping</a>");
            } else {
                // Display cart items
                out.println("<table>");
                out.println("<tr><th>Item</th><th>Price</th><th>Action</th></tr>");

                for (HijabProduct item : cart) {
                    out.println("<tr>");
                    out.println("<td>" + item.getSku().replace("-", " ") + "</td>");
                    out.println("<td>RM " + String.format("%.2f", item.getPrice()) + "</td>");
                    out.println("<td><a href='CartServlet?action=remove&sku=" + item.getSku() + "' class='btn-remove'>Remove</a></td>");
                    out.println("</tr>");
                }
                out.println("</table>");

                // Display total price
                out.println("<div class='total'>Total: RM " + String.format("%.2f", totalPrice) + "</div>");

                // Navigation buttons
                out.println("<br><br>");
                out.println("<a href='HijabList' class='btn btn-shop'>Continue Shopping</a>");
                out.println("<a href='CheckoutServlet' class='btn btn-checkout'>Proceed to Checkout</a>");
            }

            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // Handles GET requests (e.g. viewing cart)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    // Handles POST requests (e.g. add/remove item)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
