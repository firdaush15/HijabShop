package com.hijabshop.web;

import com.hijabshop.entities.HijabProduct;
import com.hijabshop.entities.InventoryFacadeRemote;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@WebServlet(name = "EditProductServlet", urlPatterns = {"/EditProductServlet"})
@MultipartConfig
public class EditProductServlet extends HttpServlet {

    @EJB
    private InventoryFacadeRemote inventoryFacade;

    // 1. GET Request: Show the Edit Form pre-filled
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String sku = request.getParameter("sku");
        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            HijabProduct p = inventoryFacade.getProduct(sku);

            out.println("<!DOCTYPE html>");
            out.println("<html><head><title>Edit Product</title>");
            out.println("<style>");
            out.println("body { font-family: 'Lato', sans-serif; background: #fcf5f7; color: #555; padding: 40px; }");
            out.println(".card { background: white; padding: 30px; border-radius: 10px; max-width: 500px; margin: auto; box-shadow: 0 5px 15px rgba(0,0,0,0.1); }");
            out.println("h2 { color: #d63384; font-family: 'Playfair Display', serif; text-align: center; }");
            out.println("label { font-weight: bold; margin-top: 10px; display: block; }");
            out.println("input, select { width: 100%; padding: 10px; margin-top: 5px; border: 1px solid #ddd; border-radius: 5px; box-sizing: border-box; }");
            out.println(".btn { background: #ff8da1; color: white; padding: 12px; border: none; border-radius: 5px; width: 100%; font-size: 16px; cursor: pointer; margin-top: 20px; transition: 0.3s; }");
            out.println(".btn:hover { background: #d63384; }");
            out.println(".cancel { display: block; text-align: center; margin-top: 15px; text-decoration: none; color: #777; }");
            out.println("</style>");
            out.println("</head><body>");

            if (p != null) {
                out.println("<div class='card'>");
                out.println("<h2>Edit Product</h2>");
                out.println("<form action='EditProductServlet' method='POST' enctype='multipart/form-data'>");

                // Read-only SKU (Primary Key)
                out.println("<label>Product SKU (Cannot be changed):</label>");
                out.println("<input type='text' value='" + p.getSku() + "' disabled>");
                out.println("<input type='hidden' name='sku' value='" + p.getSku() + "'>");

                out.println("<label>Category:</label>");
                out.println("<select name='category'>");
                out.println("<option " + (p.getCategory().equalsIgnoreCase("Shawl") ? "selected" : "") + ">Shawl</option>");
                out.println("<option " + (p.getCategory().equalsIgnoreCase("Bawal") ? "selected" : "") + ">Bawal</option>");
                out.println("<option " + (p.getCategory().equalsIgnoreCase("Accessories") ? "selected" : "") + ">Accessories</option>");
                out.println("</select>");

                out.println("<label>Price (RM):</label>");
                out.println("<input type='number' step='0.01' name='price' value='" + p.getPrice() + "' required>");

                out.println("<label>Update Image (Optional):</label>");
                out.println("<input type='file' name='imageFile' accept='image/*'>");
                if (p.getImage() != null) {
                    out.println("<p style='font-size:12px; color:#888;'>Current: " + p.getImage() + "</p>");
                }

                out.println("<button type='submit' class='btn'>Update Product</button>");
                out.println("<a href='Dashboard' class='cancel'>Cancel</a>");
                out.println("</form></div>");
            } else {
                out.println("<h3 style='text-align:center'>Product not found!</h3><div style='text-align:center'><a href='Dashboard'>Go Back</a></div>");
            }
            out.println("</body></html>");
        } catch (Exception e) {
            response.sendRedirect("Dashboard?msg=Error Loading Product");
        }
    }

    // 2. POST Request: Handle the update logic
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String sku = request.getParameter("sku");
            String category = request.getParameter("category");
            double price = Double.parseDouble(request.getParameter("price"));
            Part filePart = request.getPart("imageFile");

            // Fetch existing to get current image path
            HijabProduct existing = inventoryFacade.getProduct(sku);
            String imagePath = existing.getImage();

            // Check if a new file was uploaded
            if (filePart.getSize() > 0) {
                String originalFileName = getFileName(filePart);
                String uniqueFileName = System.currentTimeMillis() + "_" + originalFileName;
                String uploadPath = getServletContext().getRealPath("/images");

                File dir = new File(uploadPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                File file = new File(dir, uniqueFileName);

                try (InputStream input = filePart.getInputStream(); FileOutputStream output = new FileOutputStream(file)) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = input.read(buffer)) > 0) {
                        output.write(buffer, 0, length);
                    }
                }
                // Update image path only if upload succeeded
                imagePath = "images/" + uniqueFileName;
            }

            // Create updated object
            HijabProduct updatedProduct = new HijabProduct(sku, price, category, imagePath);
            inventoryFacade.updateProduct(updatedProduct);

            response.sendRedirect("Dashboard?msg=Product Updated Successfully");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("Dashboard?msg=Error Updating Product");
        }
    }

    // Helper to clean up file path
    private String getFileName(Part part) {
        for (String content : part.getHeader("content-disposition").split(";")) {
            if (content.trim().startsWith("filename")) {
                String path = content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
                return path.substring(path.lastIndexOf('/') + 1).substring(path.lastIndexOf('\\') + 1);
            }
        }
        return "unknown_" + System.currentTimeMillis() + ".jpg";
    }
}
