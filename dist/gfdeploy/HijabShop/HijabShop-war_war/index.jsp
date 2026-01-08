<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.hijabshop.web.ShopDelegate"%>
<%@page import="com.hijabshop.entities.HijabProduct"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Arrays"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Hijab Shop - Home</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <style>
            @import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@600&family=Lato:wght@400;700&display=swap');

            body {
                font-family: 'Lato', sans-serif;
                margin: 0;
                padding: 0;
                background-color: #fcf5f7;
                color: #555;
            }

            /* --- NAVBAR --- */
            .navbar {
                background-color: #ffffff;
                box-shadow: 0 2px 10px rgba(0,0,0,0.05);
                padding: 15px 40px;
                display: flex;
                justify-content: space-between;
                align-items: center;
                position: sticky;
                top: 0;
                z-index: 1000;
            }
            .navbar h1 {
                margin: 0;
                font-family: 'Playfair Display', serif;
                font-size: 28px;
                color: #d63384;
                letter-spacing: 1px;
            }
            .nav-links a {
                color: #777;
                text-decoration: none;
                margin-left: 30px;
                font-weight: 700;
                text-transform: uppercase;
                font-size: 13px;
                letter-spacing: 1px;
                transition: color 0.3s;
            }
            .nav-links a:hover {
                color: #d63384;
            }
            .user-badge {
                color: #d63384;
                font-weight: bold;
                margin-left: 20px;
                border: 1px solid #d63384;
                padding: 5px 15px;
                border-radius: 20px;
                font-size: 13px;
            }
            /* New Cart Style */
            .cart-link {
                color: #d63384 !important;
                font-weight: 900 !important;
            }

            /* --- HERO --- */
            .hero {
                background: linear-gradient(135deg, #fff0f5 0%, #ffe6eb 100%);
                height: 60vh;
                display: flex;
                flex-direction: column;
                justify-content: center;
                align-items: center;
                text-align: center;
                margin-bottom: 50px;
                border-bottom: 1px solid #ffeef2;
            }
            .hero h1 {
                font-family: 'Playfair Display', serif;
                font-size: 72px;
                color: #d63384;
                margin-bottom: 40px;
                letter-spacing: 1px;
                text-shadow: 2px 2px 0px white;
            }
            .btn-shop {
                background-color: #ff8da1;
                color: white;
                padding: 18px 50px;
                text-decoration: none;
                font-size: 18px;
                border-radius: 50px;
                font-weight: bold;
                text-transform: uppercase;
                letter-spacing: 2px;
                transition: all 0.3s;
                box-shadow: 0 5px 20px rgba(214, 51, 132, 0.3);
            }
            .btn-shop:hover {
                background-color: #d63384;
                transform: translateY(-3px);
            }

            /* --- SECTIONS --- */
            .section {
                max-width: 1300px;
                margin: 0 auto 80px auto;
                padding: 0 20px;
                text-align: center;
            }
            .section-title {
                font-family: 'Playfair Display', serif;
                font-size: 42px;
                color: #d63384;
                margin-bottom: 50px;
                position: relative;
                display: inline-block;
            }
            .section-title::after {
                content: '';
                display: block;
                width: 80px;
                height: 3px;
                background-color: #ff8da1;
                margin: 15px auto 0;
            }

            /* --- CATEGORIES GRID --- */
            .categories-grid {
                display: flex;
                justify-content: center;
                gap: 40px;
                flex-wrap: wrap;
            }
            .cat-card {
                width: 380px;
                height: 450px;
                border-radius: 20px;
                position: relative;
                overflow: hidden;
                box-shadow: 0 15px 35px rgba(0,0,0,0.1);
                background-color: #fff;
                transition: transform 0.3s ease;
            }
            .cat-card:hover {
                transform: translateY(-10px);
            }

            /* Carousel */
            .carousel-images {
                width: 100%;
                height: 100%;
                position: relative;
            }
            .carousel-images img {
                width: 100%;
                height: 100%;
                object-fit: cover;
                position: absolute;
                top: 0;
                left: 0;
                opacity: 0;
                transition: opacity 0.5s ease-in-out;
            }
            .carousel-images img.active {
                opacity: 1;
            }

            .carousel-btn {
                position: absolute;
                top: 50%;
                transform: translateY(-50%);
                background: rgba(255, 255, 255, 0.8);
                border: none;
                color: #d63384;
                font-size: 24px;
                padding: 15px;
                cursor: pointer;
                border-radius: 50%;
                z-index: 2;
                transition: background 0.3s, transform 0.2s;
                box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            }
            .carousel-btn:hover {
                background: rgba(255, 255, 255, 1);
                transform: translateY(-50%) scale(1.1);
            }
            .carousel-btn.prev {
                left: 15px;
            }
            .carousel-btn.next {
                right: 15px;
            }

            .cat-overlay {
                background: linear-gradient(to top, rgba(255, 255, 255, 1) 0%, rgba(255, 255, 255, 0.8) 100%);
                position: absolute;
                bottom: 0;
                width: 100%;
                padding: 20px 0;
                text-align: center;
                font-weight: bold;
                color: #d63384;
                font-family: 'Playfair Display', serif;
                font-size: 28px;
                z-index: 1;
                letter-spacing: 1px;
            }

            /* Features & Footer */
            .features-grid {
                display: flex;
                justify-content: space-around;
                flex-wrap: wrap;
                gap: 40px;
                background-color: white;
                padding: 60px;
                border-radius: 20px;
                box-shadow: 0 5px 25px rgba(0,0,0,0.03);
                max-width: 1200px;
                margin: 0 auto;
            }
            .feature-item {
                max-width: 300px;
            }
            .feature-icon {
                font-size: 50px;
                color: #ff8da1;
                margin-bottom: 20px;
            }
            .footer {
                background-color: #ffffff;
                color: #d63384;
                text-align: center;
                padding: 50px 20px;
                border-top: 1px solid #ffe0e9;
                margin-top: auto;
            }
        </style>
    </head>
    <body>

        <%-- 1. SETUP DATA --%>
        <%
            ShopDelegate delegate = new ShopDelegate();
            List<HijabProduct> allProducts = new ArrayList<HijabProduct>();
            try {
                HijabProduct[] fetched = delegate.getFacade().getAllProducts();
                if (fetched != null) {
                    allProducts = Arrays.asList(fetched);
                }
            } catch (Exception e) {
            }

            String[][] categoriesToShow = {
                {"Shawl", "Shawl"},
                {"Bawal", "Bawal"},
                {"Accessories", "Accessories"}
            };

            // CHECK LOGIN & CART STATUS
            String currentUser = (String) session.getAttribute("user");
            boolean isLoggedIn = (currentUser != null);

            // --- CART COUNT LOGIC ---
            List<HijabProduct> cart = (List<HijabProduct>) session.getAttribute("cart");
            int cartCount = (cart != null) ? cart.size() : 0;
        %>

        <div class="navbar">
            <h1>🌸 HijabShop</h1>
            <div class="nav-links">
                <a href="index.jsp">Home</a>
                <a href="HijabList">Collections</a>

                <% if (isLoggedIn) {%>
                <a href="CartServlet" class="cart-link">Cart (<%= cartCount%>)</a>
                <span class="user-badge">Hi, <%= currentUser%></span>
                <a href="LogoutServlet">Logout</a>
                <% } else { %>
                <a href="login.html">Login</a> 
                <% } %>

                <a href="about.html">About Us</a>
            </div>
        </div>

        <div class="hero">
            <h1>Elevate Your Elegance</h1>
            <a href="HijabList" class="btn-shop">Shop The Collection</a>
        </div>

        <div class="section">
            <h2 class="section-title">Shop by Category</h2>
            <div class="categories-grid">

                <%
                    for (String[] cat : categoriesToShow) {
                        String displayTitle = cat[0];
                        String dbValue = cat[1];

                        List<HijabProduct> catProducts = new ArrayList<HijabProduct>();
                        for (HijabProduct p : allProducts) {
                            if (dbValue.equalsIgnoreCase(p.getCategory())) {
                                catProducts.add(p);
                            }
                        }
                %>

                <div class="cat-card">
                    <% if (catProducts.isEmpty()) {%>
                    <div class="carousel-images">
                        <img src="https://placehold.co/380x450/fff0f5/d63384?text=Coming+Soon" class="active" alt="Coming Soon">
                    </div>
                    <div class="cat-overlay"><%= displayTitle%></div>

                    <% } else { %>
                    <div class="carousel-images">
                        <%
                            int limit = Math.min(3, catProducts.size());
                            for (int i = 0; i < limit; i++) {
                                String activeClass = (i == 0) ? "active" : "";
                                String imgPath = catProducts.get(i).getImage();
                                if (imgPath == null || imgPath.isEmpty())
                                    imgPath = "https://placehold.co/380x450?text=No+Image";
                        %>
                        <img src="<%= imgPath%>" class="<%= activeClass%>" alt="<%= displayTitle%>">
                        <% } %>
                    </div>

                    <% if (catProducts.size() > 1) { %>
                    <button class="carousel-btn prev">&#10094;</button>
                    <button class="carousel-btn next">&#10095;</button>
                    <% }%>
                    <div class="cat-overlay"><%= displayTitle%></div>

                    <div style="position: absolute; bottom: 60px; width: 100%; text-align: center; z-index: 5;">
                        <button class="btn-shop" style="padding: 8px 20px; font-size: 12px;" onclick="addToCart('<%= catProducts.get(0).getSku()%>')">Shop Now</button>
                    </div>

                    <% } %>
                </div>

                <% }%>

            </div>
        </div>

        <div class="section">
            <div class="features-grid">
                <div class="feature-item"><div class="feature-icon">✨</div><h3>Premium Quality</h3><p>Hand-picked fabrics that ensure breathable comfort all day long.</p></div>
                <div class="feature-item"><div class="feature-icon">🚚</div><h3>Fast Delivery</h3><p>Get your favorite styles delivered to your doorstep within 3 days.</p></div>
                <div class="feature-item"><div class="feature-icon">💎</div><h3>Exclusive Designs</h3><p>Unique patterns and cuts you won't find anywhere else.</p></div>
            </div>
        </div>

        <div class="footer">
            <h3>Stay Connected</h3>
            <p>Follow us on social media for the latest updates and sales.</p>
            <br>
            &copy; 2024 HijabShop Enterprise. All Rights Reserved.
        </div>

        <script>
            function addToCart(sku) {
                var loggedIn = <%= isLoggedIn%>;
                if (loggedIn) {
                    // Redirect to CartServlet to Add
                    window.location.href = 'CartServlet?action=add&sku=' + sku;
                } else {
                    window.location.href = 'login.html';
                }
            }

            document.querySelectorAll('.cat-card').forEach(card => {
                const images = card.querySelectorAll('.carousel-images img');
                if (images.length > 1) {
                    const prevBtn = card.querySelector('.prev');
                    const nextBtn = card.querySelector('.next');
                    let currentIndex = 0;
                    function showImage(index) {
                        images.forEach((img, i) => {
                            img.classList.toggle('active', i === index);
                        });
                    }
                    prevBtn.addEventListener('click', (e) => {
                        e.stopPropagation();
                        currentIndex = (currentIndex > 0) ? currentIndex - 1 : images.length - 1;
                        showImage(currentIndex);
                    });
                    nextBtn.addEventListener('click', (e) => {
                        e.stopPropagation();
                        currentIndex = (currentIndex < images.length - 1) ? currentIndex + 1 : 0;
                        showImage(currentIndex);
                    });
                }
            });
        </script>

    </body>
</html>