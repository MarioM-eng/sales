package com.mmdevelopment.services;

import com.mmdevelopment.Validations;
import com.mmdevelopment.models.daos.StockDao;
import com.mmdevelopment.models.entities.Price;
import com.mmdevelopment.models.entities.Stock;
import static com.mmdevelopment.utils.ServiceTransactionManager.executeInTransaction;

import java.util.Optional;

public class StockService extends BaseService<Stock>{
    public StockService(StockDao dao) {
        super(Stock.class, dao);
    }

    public void validate(Stock stock) throws IllegalArgumentException {
        Validations.getInstance().validate(stock, true);
        if (!isValidatedColorAndSizeCombination(stock)) {
            throw new IllegalArgumentException(
                    "La combinación entre tamaño y color ya exite para el producto " +
                            stock.getProduct().getName() + ". Por favor ingrese uno nuevo");
        }
    }

    public boolean isValidatedColorAndSizeCombination(Stock stock) {
        Optional<Stock> stockOptional = stock.getProduct().getStocks()
                .stream()
                .filter(
                    stk -> (stk.getColor().equals(stock.getColor()) && stk.getSize().equals(stock.getSize()))
                )
                .findFirst();
        if (!stockOptional.isEmpty()) {
            if (stockOptional.get().getId() != stock.getId()) {
                return false;
            }
        }
        return true;
    }

    public boolean areValidatedThePrices(Stock stock) {
        double costPrice = 0.0;
        double retailPrice = 0.0;

         for (Price price: stock.getPrices()) {
             if (price.getValue() == null) {
                 throw new IllegalArgumentException(
                         "El precio de " + price.getPriceType().getName() +
                                 " debe ingresarse.");
             }
             if (price.getPriceType().getPrefix().equals("costPrice")) {
                 costPrice = price.getValue();
             }
             if (price.getPriceType().getPrefix().equals("retailPrice")) {
                 retailPrice = price.getValue();
             }
         }

         if (costPrice != 0 && costPrice > retailPrice) {
             throw new IllegalArgumentException("El precio de costo no debe ser mayor al precio de venta al público");
         }

         return true;
    }

    public void setEnabled(Stock... stocks) {
        executeInTransaction(
                em -> {
                    ((StockDao) this.getDao()).setEnabled(stocks);
                    return null;
                }
        );
    }
}
