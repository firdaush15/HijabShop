package com.hijabshop.web;

import com.hijabshop.entities.HijabProduct;
import com.hijabshop.entities.InventoryFacadeRemote; // Import Facade
import com.hijabshop.services.SystemStatusBeanLocal;
import java.io.IOException;
import java.io.PrintWriter;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "DashboardServlet", urlPatterns = {"/Dashboard"})
public class DashboardServlet extends HttpServlet {

    @EJB
    private SystemStatusBeanLocal statusBean;

    @EJB
    private InventoryFacadeRemote inventoryFacade; // Inject Inventory Facade

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Security Check
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
            out.println("<head><title>HijabShop Admin</title>");
            out.println("<style>");
            out.println("body { font-family: 'Lato', sans-serif; background: #fcf5f7; color: #555; padding: 20px; }");
            out.println(".card { background: white; padding: 30px; border-radius: 10px; margin-bottom: 20px; box-shadow: 0 5px 15px rgba(0,0,0,0.05); max-width: 800px; margin: 20px auto; }");
            out.println("input, select { width: 100%; padding: 10px; margin: 10px 0; border: 1px solid #ddd; border-radius: 5px; box-sizing: border-box; }");
            out.println(".btn { background: #ff8da1; color: white; padding: 10px 20px; border: none; border-radius: 5px; cursor: pointer; width: 100%; margin-top:10px; }");

            // Table Styles
            out.println("table { width: 100%; border-collapse: collapse; margin-top: 15px; }");
            out.println("th, td { padding: 12px; text-align: left; border-bottom: 1px solid #eee; }");
            out.println("th { background-color: #ff8da1; color: white; border-radius: 4px; }");
            out.println(".action-link { text-decoration: none; padding: 5px 10px; border-radius: 5px; color: white; font-size: 12px; margin-right: 5px; }");
            out.println(".edit { background-color: #f0ad4e; }");
            out.println(".delete { background-color: #d9534f; }");
            out.println(".msg { text-align: center; padding: 10px; background: #d1e7dd; color: #0f5132; border-radius: 5px; margin-bottom: 20px; }");

            out.println("</style></head>");
            out.println("<body>");

            out.println("<h1 style='text-align:center; color:#d63384;'>Admin Dashboard</h1>");

            // Show Feedback Messages
            String msg = request.getParameter("msg");
            if (msg != null) {
                out.println("<div style='max-width:800px; margin:0 auto;'><div class='msg'>" + msg + "</div></div>");
            }

            // --- SYSTEM HEALTH ---
            out.println("<div class='card'><h3>System Status</h3>");
            out.println("<p>" + statusBean.getSystemHealth() + "</p></div>");

            // --- PRODUCT INVENTORY MANAGEMENT (NEW) ---
            out.println("<div class='card'><h3>Manage Inventory</h3>");
            out.println("<table>");
            out.println("<thead><tr><th>SKU</th><th>Category</th><th>Price</th><th>Actions</th></tr></thead>");
            out.println("<tbody>");

            try {
                HijabProduct[] products = inventoryFacade.getAllProducts();
                if (products.length == 0) {
                    out.println("<tr><td colspan='4' style='text-align:center'>No products found.</td></tr>");
                }
                for (HijabProduct p : products) {
                    out.println("<tr>");
                    out.println("<td>" + p.getSku() + "</td>");
                    out.println("<td>" + p.getCategory() + "</td>");
                    out.println("<td>RM " + String.format("%.2f", p.getPrice()) + "</td>");
                    out.println("<td>");
                    out.println("<a href='EditProductServlet?sku=" + p.getSku() + "' class='action-link edit'>Edit</a>");
                    out.println("<a href='DeleteProductServlet?sku=" + p.getSku() + "' class='action-link delete' onclick=\"return confirm('Are you sure you want to delete " + p.getSku() + "?');\">Delete</a>");
                    out.println("</td>");
                    out.println("</tr>");
                }
            } catch (Exception e) {
                out.println("<tr><td colspan='4' style='color:red'>Error retrieving products.</td></tr>");
            }
            out.println("</tbody></table>");
            out.println("</div>");

            // --- ADD PRODUCT FORM ---
            out.println("<div class='card'><h3>Add New Product</h3>");

            // Add Product Status Message
            String status = request.getParameter("status");
            if ("success".equals(status)) {
                out.println("<p style='color:green'>Product Added!</p>");
            }
            if ("error".equals(status)) {
                out.println("<p style='color:red'>Upload Failed!</p>");
            }

            out.println("<form action='AddProductServlet' method='POST' enctype='multipart/form-data'>");
            out.println("<label>SKU:</label><input type='text' name='sku' required>");
            out.println("<label>Category:</label>");
            out.println("<select name='category'><option>Shawl</option><option>Bawal</option><option>Accessories</option></select>");
            out.println("<label>Price (RM):</label><input type='number' step='0.01' name='price' required>");
            out.println("<label>Image:</label><input type='file' name='imageFile' accept='image/*' required>");
            out.println("<button type='submit' class='btn'>Save Product</button>");
            out.println("</form>");

            out.println("</div>");

            // --- FOOTER LINKS ---
            out.println("<div style='text-align:center;'><a href='ManageOrders'>View Orders</a> | <a href='LogoutServlet'>Logout</a></div>");
            out.println("</body></html>");
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
