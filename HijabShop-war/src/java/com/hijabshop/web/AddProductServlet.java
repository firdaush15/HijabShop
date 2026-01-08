package com.hijabshop.web;

// Import entity and EJB interface
import com.hijabshop.entities.HijabProduct;
import com.hijabshop.entities.InventoryFacadeRemote;

// Import required Java IO classes for file handling
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

// Import EJB and Servlet-related classes
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

// Servlet mapping: this servlet handles requests sent to /AddProductServlet
@WebServlet(name = "AddProductServlet", urlPatterns = {"/AddProductServlet"})

// Enables file upload functionality (multipart/form-data)
@MultipartConfig
public class AddProductServlet extends HttpServlet {

    // Inject InventoryFacadeRemote EJB to interact with the database
    @EJB
    private InventoryFacadeRemote inventoryFacade;

    // Handles HTTP POST requests (form submission)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // ================================
            // 1. Capture form data from request
            // ================================

            // Retrieve SKU value from the form
            String sku = request.getParameter("sku");

            // Retrieve product category from the form
            String category = request.getParameter("category");

            // Retrieve price and convert from String to double
            double price = Double.parseDouble(request.getParameter("price"));

            // Retrieve uploaded image file from the form
            Part filePart = request.getPart("imageFile");

            // ================================
            // 2. Process uploaded image file
            // ================================
            // Get the original filename from the uploaded file
            String originalFileName = getFileName(filePart);

            // Generate a unique filename using current timestamp
            // This prevents file overwrite when multiple images have the same name
            String uniqueFileName = System.currentTimeMillis() + "_" + originalFileName;

            // Get the absolute path of the "images" folder in the web application
            String uploadPath = getServletContext().getRealPath("/images");

            // Debug message to show where the image is saved
            System.out.println(">>> UPLOADING TO: " + uploadPath + File.separator + uniqueFileName);

            // Create the images directory if it does not exist
            File dir = new File(uploadPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // Create a file object for the uploaded image
            File file = new File(dir, uniqueFileName);

            // Write uploaded file data into the server directory
            try (InputStream input = filePart.getInputStream(); FileOutputStream output = new FileOutputStream(file)) {

                byte[] buffer = new byte[1024];
                int length;

                // Read file data in chunks and write to output file
                while ((length = input.read(buffer)) > 0) {
                    output.write(buffer, 0, length);
                }
            }

            // ================================
            // 3. Save product data to database
            // ================================
            // Store relative image path for database usage
            String dbPath = "images/" + uniqueFileName;

            // Create HijabProduct object with form data
            HijabProduct product = new HijabProduct(sku, price, category, dbPath);

            // Call EJB method to save product into database
            inventoryFacade.addProduct(product);

            // Redirect to dashboard with success status
            response.sendRedirect("Dashboard?status=success");

        } catch (Exception e) {
            // Print error details for debugging
            e.printStackTrace();

            // Redirect to dashboard with error status if something fails
            response.sendRedirect("Dashboard?status=error");
        }
    }

    // =========================================
    // Helper method to extract filename safely
    // =========================================
    private String getFileName(Part part) {

        // Extract filename from content-disposition header
        for (String content : part.getHeader("content-disposition").split(";")) {
            if (content.trim().startsWith("filename")) {

                // Remove quotes and fake browser paths (e.g., C:\fakepath\image.jpg)
                String path = content.substring(content.indexOf('=') + 1)
                        .trim()
                        .replace("\"", "");

                // Return only the actual filename
                return path.substring(path.lastIndexOf('/') + 1)
                        .substring(path.lastIndexOf('\\') + 1);
            }
        }

        // Fallback filename if extraction fails
        return "unknown_" + System.currentTimeMillis() + ".jpg";
    }
}
