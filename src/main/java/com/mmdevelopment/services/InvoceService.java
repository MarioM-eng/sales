package com.mmdevelopment.services;

import com.mmdevelopment.models.daos.InvoiceDao;
import com.mmdevelopment.models.entities.Invoice;

import java.util.List;

public class InvoceService extends BaseService<Invoice>{
    public InvoceService(InvoiceDao dao) {
        super(Invoice.class, dao);
    }

    public double getTotalByInvoice(Invoice invoice) {
        return invoice.getSalesDetails()
                .stream()
                .map(salesDetail -> salesDetail.getPrice().getValue())
                .mapToDouble(Double::doubleValue).sum();
    }

    public double getTotalByInvoices(List<Invoice> invoices) {
        return invoices.stream().map(invoice -> {
            return invoice.getSalesDetails()
                    .stream()
                    .map(salesDetail -> salesDetail.getPrice().getValue())
                    .mapToDouble(Double::doubleValue).sum();
        }).mapToDouble(Double::doubleValue).sum();
    }
}
