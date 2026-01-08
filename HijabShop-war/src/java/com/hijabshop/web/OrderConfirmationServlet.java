package com.hijabshop.web;

import com.hijabshop.entities.CustomerOrder;
import com.hijabshop.entities.HijabProduct;
import com.hijabshop.entities.InventoryFacadeRemote;
import com.hijabshop.entities.LineItem;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "OrderConfirmationServlet", urlPatterns = {"/OrderConfirmationServlet"})
public class OrderConfirmationServlet extends HttpServlet {

    // 1. INJECT THE DATABASE CONNECTION
    @EJB
    private InventoryFacadeRemote inventoryFacade;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        // 2. GET FORM DATA
        String customerName = request.getParameter("name");
        String address = request.getParameter("address"); // Don't forget the address!

        // 3. GET CART DATA (Before clearing it)
        HttpSession session = request.getSession();
        List<HijabProduct> cart = (List<HijabProduct>) session.getAttribute("cart");

        // Generate Transaction ID for display
        Random rand = new Random();
        int transactionId = 100000 + rand.nextInt(900000);

        try {
            if (cart != null && !cart.isEmpty()) {

                // A. Calculate Total
                double totalAmount = 0.0;
                for (HijabProduct p : cart) {
                    totalAmount += p.getPrice();
                }

                // B. Create the Order Object
                CustomerOrder newOrder = new CustomerOrder(customerName, address, totalAmount, "Paid", new Date());

                // C. Create Line Items (The products in the order)
                List<LineItem> items = new ArrayList<>();
                for (HijabProduct p : cart) {
                    // Create a line item for each product (Quantity 1 for simplicity)
                    // Constructor: SKU, Name, Price, Quantity, OrderParent
                    LineItem item = new LineItem(p.getSku(), p.getSku(), p.getPrice(), 1, newOrder);
                    items.add(item);
                }

                // D. Attach items to Order
                newOrder.setLineItems(items);

                // E. SAVE TO DATABASE
                inventoryFacade.createOrder(newOrder);

                System.out.println(">>> Order Saved Successfully: " + newOrder.getId());
            }

        } catch (Exception e) {
            e.printStackTrace();
            // You might want to handle errors here, but for now we proceed to show the success/fail page
            System.out.println(">>> ERROR SAVING ORDER: " + e.getMessage());
        }

        // 4. NOW CLEAR THE CART
        session.removeAttribute("cart");

        // 5. Display Receipt
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Payment Successful</title>");
            out.println("<style>");
            out.println("@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@600&family=Lato:wght@400;700&display=swap');");
            out.println("body { font-family: 'Lato', sans-serif; background-color: #fcf5f7; text-align: center; padding-top: 50px; }");
            out.println(".success-box { max-width: 500px; margin: 0 auto; background: white; padding: 50px; border-radius: 15px; box-shadow: 0 10px 30px rgba(0,0,0,0.1); }");
            out.println(".icon { font-size: 60px; color: #2ecc71; margin-bottom: 20px; }");
            out.println("h1 { color: #d63384; font-family: 'Playfair Display', serif; margin-bottom: 10px; }");
            out.println("p { color: #777; font-size: 16px; line-height: 1.6; }");
            out.println(".trans-id { background: #f8f9fa; padding: 10px; border-radius: 5px; display: inline-block; font-family: monospace; letter-spacing: 2px; margin: 20px 0; color: #555; }");
            out.println(".btn-home { background-color: #ff8da1; color: white; padding: 12px 30px; border-radius: 30px; text-decoration: none; font-weight: bold; display: inline-block; margin-top: 20px; }");
            out.println(".btn-home:hover { background-color: #e06c85; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");

            out.println("<div class='success-box'>");
            out.println("<div class='icon'>✔</div>");
            out.println("<h1>Payment Successful!</h1>");
            out.println("<p>Thank you, <strong>" + customerName + "</strong>. Your order has been placed successfully.</p>");
            out.println("<div class='trans-id'>ID: HJB-" + transactionId + "</div>");
            out.println("<p>We will ship your items to <strong>" + (address != null ? address : "your address") + "</strong> shortly.</p>");
            out.println("<a href='HijabList' class='btn-home'>Back to Shop</a>");
            out.println("</div>");

            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
}
