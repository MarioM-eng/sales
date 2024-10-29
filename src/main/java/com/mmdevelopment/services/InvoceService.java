package com.mmdevelopment.services;

import com.mmdevelopment.models.daos.InvoiceDao;
import com.mmdevelopment.models.entities.Invoice;

public class InvoceService extends BaseService<Invoice>{
    public InvoceService(InvoiceDao dao) {
        super(Invoice.class, dao);
    }
}
