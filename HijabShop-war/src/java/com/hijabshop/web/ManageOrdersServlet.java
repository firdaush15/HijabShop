package com.hijabshop.web;

import com.hijabshop.entities.CustomerOrder;
import com.hijabshop.entities.InventoryFacadeRemote;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "ManageOrdersServlet", urlPatterns = {"/ManageOrders"})
public class ManageOrdersServlet extends HttpServlet {

    @EJB
    private InventoryFacadeRemote inventoryFacade;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. SECURITY CHECK (Admin Only)
        HttpSession session = request.getSession(false);
        String role = (session != null) ? (String) session.getAttribute("role") : null;

        if (session == null || !"ADMIN".equals(role)) {
            response.sendRedirect("login.html");
            return;
        }

        response.setContentType("text/html;charset=UTF-8");
        
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Manage Orders</title>");
            out.println("<style>");
            out.println("@import url('https://fonts.googleapis.com/css2?family=Lato:wght@400;700&display=swap');");
            out.println("body { font-family: 'Lato', sans-serif; background-color: #fcf5f7; padding: 20px; color: #555; }");
            out.println(".navbar { display:flex; justify-content:space-between; align-items:center; background:white; padding:15px 30px; border-radius:10px; box-shadow:0 2px 10px rgba(0,0,0,0.05); margin-bottom:20px; }");
            out.println("h1 { color: #d63384; margin: 0; font-size: 24px; }");
            out.println(".btn { text-decoration: none; background: #ff8da1; color: white; padding: 10px 20px; border-radius: 20px; font-weight: bold; transition:0.3s; }");
            out.println(".btn:hover { background: #d63384; }");
            
            out.println("table { width: 100%; border-collapse: collapse; background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 5px 15px rgba(0,0,0,0.05); }");
            out.println("th { background: #ffeef2; color: #d63384; padding: 15px; text-align: left; }");
            out.println("td { padding: 15px; border-bottom: 1px solid #f0f0f0; }");
            out.println("tr:last-child td { border-bottom: none; }");
            out.println("tr:hover { background-color: #fff9fb; }");
            out.println(".badge { padding: 5px 10px; border-radius: 15px; font-size: 12px; font-weight: bold; }");
            out.println(".badge-paid { background: #e8f5e9; color: #2e7d32; }");
            out.println(".badge-pending { background: #fff3e0; color: #ef6c00; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");

            // Navbar
            out.println("<div class='navbar'>");
            out.println("<h1>📦 Order Management</h1>");
            out.println("<a href='Dashboard' class='btn'>Back to Dashboard</a>");
            out.println("</div>");

            out.println("<table>");
            out.println("<thead><tr><th>ID</th><th>Date</th><th>Customer</th><th>Address</th><th>Total (RM)</th><th>Status</th></tr></thead>");
            out.println("<tbody>");

            try {
                // FETCH ORDERS FROM DATABASE
                List<CustomerOrder> orders = inventoryFacade.getAllOrders();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                for (CustomerOrder order : orders) {
                    out.println("<tr>");
                    out.println("<td>#" + order.getId() + "</td>");
                    out.println("<td>" + (order.getOrderDate() != null ? sdf.format(order.getOrderDate()) : "N/A") + "</td>");
                    out.println("<td>" + order.getCustomerName() + "</td>");
                    out.println("<td>" + order.getShippingAddress() + "</td>");
                    out.println("<td>RM " + String.format("%.2f", order.getTotalAmount()) + "</td>");
                    
                    String statusClass = "Paid".equalsIgnoreCase(order.getStatus()) ? "badge-paid" : "badge-pending";
                    out.println("<td><span class='badge " + statusClass + "'>" + order.getStatus() + "</span></td>");
                    out.println("</tr>");
                }
            } catch (Exception e) {
                out.println("<tr><td colspan='6' style='text-align:center; color:red;'>Error loading orders: " + e.getMessage() + "</td></tr>");
            }

            out.println("</tbody>");
            out.println("</table>");

            out.println("</body>");
            out.println("</html>");
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