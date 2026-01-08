package com.hijabshop.services;

import com.hijabshop.entities.CustomerOrder;
import com.hijabshop.entities.CustomerProfile;
import com.hijabshop.entities.HijabProduct;
import com.hijabshop.entities.InventoryException;
import com.hijabshop.entities.InventoryFacadeRemote;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless(name = "InventoryFacade")
public class InventoryFacade implements InventoryFacadeRemote {

    @PersistenceContext(unitName = "HijabShop-PU")
    private EntityManager em;

    // --- Existing Methods ---
    @Override
    public HijabProduct[] getAllProducts() throws InventoryException {
        Query query = em.createQuery("SELECT p FROM HijabProduct p");
        List<HijabProduct> list = query.getResultList();
        return list.toArray(new HijabProduct[0]);
    }

    @Override
    public HijabProduct getProduct(String sku) throws InventoryException {
        HijabProduct product = em.find(HijabProduct.class, sku);
        if (product == null) {
            throw new InventoryException("Product not found");
        }
        return product;
    }

    @Override
    public void addProduct(HijabProduct product) throws InventoryException {
        em.persist(product);
    }

    // --- NEW METHODS IMPLEMENTATION ---
    @Override
    public void updateProduct(HijabProduct product) throws InventoryException {
        // em.merge updates an existing entity
        em.merge(product);
    }

    @Override
    public void deleteProduct(String sku) throws InventoryException {
        HijabProduct product = em.find(HijabProduct.class, sku);
        if (product != null) {
            em.remove(product);
        } else {
            throw new InventoryException("Cannot delete: Product not found");
        }
    }

    // --- Existing Customer/Order Stubs ---
    @Override
    public void registerCustomer(CustomerProfile customer) throws InventoryException {
        em.persist(customer);
    }

    @Override
    public CustomerProfile loginCustomer(String email, String password) throws InventoryException {
        try {
            Query q = em.createQuery("SELECT c FROM CustomerProfile c WHERE c.email = :e AND c.password = :p");
            q.setParameter("e", email);
            q.setParameter("p", password);
            return (CustomerProfile) q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void createOrder(CustomerOrder order) throws InventoryException {
        em.persist(order);
    }

    @Override
    public List<CustomerOrder> getAllOrders() throws InventoryException {
        Query query = em.createQuery("SELECT o FROM CustomerOrder o");
        return query.getResultList();
    }

    @Override
    public double getTotalRevenue() throws InventoryException {
        try {
            Double sum = (Double) em.createQuery("SELECT SUM(o.totalAmount) FROM CustomerOrder o").getSingleResult();
            return sum != null ? sum : 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }
}
