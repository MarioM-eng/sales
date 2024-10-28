package com.mmdevelopment.services;

import com.mmdevelopment.Validations;
import com.mmdevelopment.models.daos.ProductDao;
import com.mmdevelopment.models.entities.Product;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

import static com.mmdevelopment.utils.ServiceTransactionManager.executeInTransaction;

@Slf4j
public class ProductService extends BaseService<Product> {

    private ProductDao dao;

    public ProductService(ProductDao dao) {
        super(Product.class, dao);
        this.dao = dao;
    }

    public List<Product> getProductByMatch(String name) {
        return this.dao.searchRecordsByMatch(name);
    }

    public List<Product> getProductByName(String name) {
        return this.dao.getByName(name);
    }

    public void validate(Product product) throws IllegalArgumentException {
        Validations.getInstance().validate(product, true);
        if (!isCodeAvailableFor(product)) {
            throw new IllegalArgumentException("El código ingresado ya pertenece a otro producto. Por favor ingrese uno nuevo");
        }

    }

    public boolean isCodeAvailableFor(Product product) {
        Optional<Product> productByCode = this.dao.getProductByCode(product.getCode());

        if (productByCode.isEmpty()) {
            return true;
        }

        if (!this.dao.isNew(product)) {
            return productByCode.get().getId() == product.getId();
        }

        return  false;
    }

    public boolean exists(String code) {
        Optional<Product> productOptional = getByCode(code);
        if (!productOptional.isEmpty()) {
            return productOptional.get().isEnabled();
        }

        return false;
    }

    private Optional<Product> getByCode(String code) {
        return ((ProductDao) this.getDao()).getByCode(code);
    }

    public void setEnabled(Product... products) {

        executeInTransaction(
                entityManager -> {
                    for (Product product: products) {
                        if (!exists(product.getCode())) {
                            throw new IllegalArgumentException(
                                    "El producto con código "+ product.getCode() +" no existe");
                        }
                        if (product.isEnabled()) {
                            product.setEnabled(false);
                        } else {
                            product.setEnabled(true);
                        }
                        this.getDao().save(product);
                    }
                    return null;
                }
        );
    }

    public List<Product> getEnabled() {
        return ((ProductDao) this.getDao()).getEnabled();
    }

}
