package com.hijabshop.web;

import com.hijabshop.entities.HijabProduct;
import com.hijabshop.entities.InventoryException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "HijabListServlet", urlPatterns = {"/HijabList"})
public class HijabListServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        // 1. Check Login Status
        HttpSession session = request.getSession(false);
        String currentUser = (session != null) ? (String) session.getAttribute("user") : null;
        boolean isLoggedIn = (currentUser != null);

        // Count cart
        int cartCount = 0;
        if (isLoggedIn) {
            List<HijabProduct> cart = (List<HijabProduct>) session.getAttribute("cart");
            if (cart != null) {
                cartCount = cart.size();
            }
        }

        ShopDelegate delegate = new ShopDelegate();

        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head><title>Hijab Shop Collection</title>");
            out.println("<style>");
            out.println("@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@600&family=Lato:wght@400;700&display=swap');");
            out.println("body { font-family: 'Lato', sans-serif; background-color: #fcf5f7; margin: 0; padding: 0; color: #555; }");

            // Navbar
            out.println(".navbar { background-color: #ffffff; box-shadow: 0 2px 10px rgba(0,0,0,0.05); padding: 15px 40px; display: flex; justify-content: space-between; align-items: center; position: sticky; top: 0; z-index: 1000; }");
            out.println(".navbar h1 { margin: 0; font-family: 'Playfair Display', serif; font-size: 28px; color: #d63384; letter-spacing: 1px; }");
            out.println(".nav-links a { color: #777; text-decoration: none; margin-left: 30px; font-weight: 700; text-transform: uppercase; font-size: 13px; letter-spacing: 1px; transition: color 0.3s; }");
            out.println(".nav-links a:hover { color: #d63384; }");
            out.println(".user-badge { color: #d63384; font-weight: bold; margin-left: 20px; border: 1px solid #d63384; padding: 5px 15px; border-radius: 20px; font-size: 13px; }");
            out.println(".cart-link { color: #d63384 !important; font-weight: 900 !important; }");

            // Header
            out.println(".header { text-align: center; padding: 60px 20px; background: linear-gradient(135deg, #ffeef2 0%, #fff0f3 100%); margin-bottom: 20px; border-bottom: 1px solid #ffe0e9; }");
            out.println(".header h2 { margin: 0; color: #d63384; font-family: 'Playfair Display', serif; font-size: 42px; }");

            // Section Headers
            out.println(".category-header { max-width: 1200px; margin: 40px auto 20px; padding: 0 20px; border-bottom: 2px solid #ff8da1; }");
            out.println(".category-header h3 { font-family: 'Playfair Display', serif; font-size: 32px; color: #333; margin-bottom: 10px; display: inline-block; background: #fcf5f7; padding-right: 20px; }");

            // Grid & Cards
            out.println(".container { max-width: 1200px; margin: 0 auto 60px; display: flex; flex-wrap: wrap; gap: 30px; justify-content: flex-start; padding: 0 20px; }");
            out.println(".card { background: white; width: 280px; border-radius: 12px; border: 1px solid #fceef2; box-shadow: 0 5px 15px rgba(214, 51, 132, 0.05); overflow: hidden; transition: all 0.3s ease; display: flex; flex-direction: column; }");
            out.println(".card:hover { transform: translateY(-7px); box-shadow: 0 15px 30px rgba(214, 51, 132, 0.15); }");
            out.println(".card-img { width: 100%; height: 320px; object-fit: cover; border-bottom: 1px solid #f8f9fa; }");
            out.println(".card-body { padding: 25px; text-align: center; }");
            out.println(".card-title { font-size: 16px; font-weight: 700; margin-bottom: 8px; color: #333; text-transform: uppercase; }");
            out.println(".card-price { color: #d63384; font-size: 20px; font-family: 'Playfair Display', serif; font-weight: bold; margin: 10px 0; }");
            out.println(".btn { background-color: #ff8da1; color: white; padding: 12px 20px; border: none; border-radius: 30px; cursor: pointer; width: 100%; font-size: 14px; font-weight: bold; text-transform: uppercase; margin-top: 15px; transition: background 0.3s; }");
            out.println(".btn:hover { background-color: #e06c85; }");
            out.println("</style></head>");
            out.println("<body>");

            // NAVBAR
            out.println("<div class='navbar'><h1>🌸 HijabShop</h1><div class='nav-links'>");
            out.println("<a href='index.jsp'>Home</a><a href='HijabList'>Collections</a>");
            if (isLoggedIn) {
                out.println("<a href='CartServlet' class='cart-link'>Cart (" + cartCount + ")</a>");
                out.println("<span class='user-badge'>Hi, " + currentUser + "</span><a href='LogoutServlet'>Logout</a>");
            } else {
                out.println("<a href='login.html'>Login</a>");
            }
            out.println("<a href='about.html'>About Us</a></div></div>");

            out.println("<div class='header'><h2>The Elegance Collection</h2><p>Modesty meets modern sophistication</p></div>");

            try {
                // 1. Fetch ALL Products
                HijabProduct[] allProducts = delegate.getFacade().getAllProducts();

                // 2. Separate into Lists
                List<HijabProduct> shawlList = new ArrayList<>();
                List<HijabProduct> bawalList = new ArrayList<>();
                List<HijabProduct> innerList = new ArrayList<>(); // Inner & Accessories

                for (HijabProduct h : allProducts) {
                    String cat = (h.getCategory() != null) ? h.getCategory().toLowerCase() : "";

                    if (cat.contains("shawl")) {
                        shawlList.add(h);
                    } else if (cat.contains("bawal")) {
                        bawalList.add(h);
                    } else {
                        // Assume everything else is Inner/Accessories
                        innerList.add(h);
                    }
                }

                // --- HELPER METHOD TO PRINT SECTION ---
                // We define a local helper class or just inline the loop logic to keep code simple.
                // Here we will inline the loops for each category.
                // === SECTION 1: SHAWLS ===
                if (!shawlList.isEmpty()) {
                    out.println("<div class='category-header'><h3>Shawl Collection</h3></div>");
                    out.println("<div class='container'>");
                    printProductCards(out, shawlList, isLoggedIn);
                    out.println("</div>");
                }

                // === SECTION 2: BAWAL ===
                if (!bawalList.isEmpty()) {
                    out.println("<div class='category-header'><h3>Bawal Series</h3></div>");
                    out.println("<div class='container'>");
                    printProductCards(out, bawalList, isLoggedIn);
                    out.println("</div>");
                }

                // === SECTION 3: INNER / ACCESSORIES ===
                if (!innerList.isEmpty()) {
                    out.println("<div class='category-header'><h3>Inners & Accessories</h3></div>");
                    out.println("<div class='container'>");
                    printProductCards(out, innerList, isLoggedIn);
                    out.println("</div>");
                }

                if (allProducts.length == 0) {
                    out.println("<h3 style='text-align:center'>No products found in the database.</h3>");
                }

            } catch (InventoryException ex) {
                out.println("<h3 style='text-align:center;color:red'>Error loading products.</h3>");
            }
            out.println("</body></html>");
        }
    }

    // --- Helper Method to Print Cards (Reduces Code Duplication) ---
    private void printProductCards(PrintWriter out, List<HijabProduct> products, boolean isLoggedIn) {
        for (HijabProduct h : products) {
            out.println("<div class='card'>");
            String img = (h.getImage() != null && !h.getImage().isEmpty()) ? h.getImage() : "https://placehold.co/280x320?text=No+Image";
            out.println("<img src='" + img + "' class='card-img'>");

            out.println("<div class='card-body'>");
            // out.println("<div style='color:#999;font-size:11px;'>" + h.getCategory() + "</div>");
            out.println("<div class='card-title'>" + h.getSku() + "</div>");
            out.println("<div class='card-price'>RM " + String.format("%.2f", h.getPrice()) + "</div>");

            if (isLoggedIn) {
                out.println("<button class='btn' onclick='location.href=\"CartServlet?action=add&sku=" + h.getSku() + "\"'>Add to Cart</button>");
            } else {
                out.println("<button class='btn' onclick='location.href=\"login.html\"'>Add to Cart</button>");
            }
            out.println("</div></div>");
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
