package com.mmdevelopment.services;

import com.mmdevelopment.Validations;
import com.mmdevelopment.models.daos.SizeDao;
import com.mmdevelopment.models.entities.Size;
import com.mmdevelopment.utils.ServiceTransactionManager;
import com.mmdevelopment.utils.exceptions.NonexistentEntityException;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

import static com.mmdevelopment.utils.ServiceTransactionManager.executeInTransaction;

public class SizeService extends BaseService<Size>{
    public SizeService(SizeDao dao) {
        super(Size.class, dao);
    }

    public void create(List<Size> sizes) {
        ServiceTransactionManager.executeInTransaction(
                new ServiceOperation<Size>() {
                    @Override
                    public Size execute(EntityManager em) throws NonexistentEntityException {
                        getDao().create(sizes);
                        return null;
                    }
                }
        );
    }

    public void validate(Size size) throws IllegalArgumentException {
        Validations.getInstance().validate(size, true);
        if (exists(size.getName())) {
            throw new IllegalArgumentException(
                    "El tamaño " + size.getName() + " ya existe. Por favor ingrese uno nuevo");
        }
    }

    public Optional<Size> getSizeByName(String name) {
        return ((SizeDao) this.getDao()).getByName(name);
    }

    public boolean exists(String name) {
        Optional<Size> sizeOptional = getSizeByName(name);
        if (!sizeOptional.isEmpty()) {
            return sizeOptional.get().isEnabled();
        }

        return false;
    }

    public List<Size> getSizeByMatch(String name) {
        return ((SizeDao) this.getDao()).searchRecordsByMatch(name, true);
    }

    public List<Size> getEnabled() {
        return ((SizeDao) this.getDao()).getEnabled();
    }

    @Override
    public Size save(Size size) {
        validate(size);
        Size sizeToSave;
        Optional<Size> sizeOptional = getSizeByName(size.getName());
        if (!sizeOptional.isEmpty()){
            sizeToSave = sizeOptional.get();
            sizeToSave.setEnabled(true);
        } else {
            sizeToSave = size;
        }
        return executeInTransaction(entityManager -> this.getDao().save(sizeToSave));
    }

    public void setEnabled(Size size) {
        if (!exists(size.getName())) {
            throw new IllegalArgumentException(
                    "El color enviado no existe");
        }
        if (size.isEnabled()) {
            size.setEnabled(false);
        } else {
            size.setEnabled(true);
        }
        executeInTransaction(entityManager -> this.getDao().save(size));
    }

}
