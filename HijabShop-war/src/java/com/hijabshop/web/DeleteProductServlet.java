package com.hijabshop.web;

import com.hijabshop.entities.InventoryFacadeRemote;
import java.io.IOException;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "DeleteProductServlet", urlPatterns = {"/DeleteProductServlet"})
public class DeleteProductServlet extends HttpServlet {

    @EJB
    private InventoryFacadeRemote inventoryFacade;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String sku = request.getParameter("sku");
        
        if (sku != null && !sku.trim().isEmpty()) {
            try {
                inventoryFacade.deleteProduct(sku);
                response.sendRedirect("Dashboard?msg=Product Deleted Successfully");
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        response.sendRedirect("Dashboard?msg=Error Deleting Product");
    }
}
