package com.mmdevelopment.services;

import com.mmdevelopment.Validations;
import com.mmdevelopment.models.daos.ColorDao;
import com.mmdevelopment.models.entities.Color;
import com.mmdevelopment.utils.ServiceTransactionManager;
import com.mmdevelopment.utils.exceptions.NonexistentEntityException;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

import static com.mmdevelopment.utils.ServiceTransactionManager.executeInTransaction;

@Slf4j
public class ColorService extends BaseService<Color>{
    public ColorService(ColorDao dao) {
        super(Color.class, dao);
    }
    public void create(List<Color> colors) {
        ServiceTransactionManager.executeInTransaction(
                new ServiceOperation<Color>() {
                    @Override
                    public Color execute(EntityManager em) throws NonexistentEntityException {
                        getDao().create(colors);
                        return null;
                    }
                }
        );
    }

    public void validate(Color color) throws IllegalArgumentException {
        Validations.getInstance().validate(color, true);
        if (colorExists(color.getName())) {
            throw new IllegalArgumentException(
                    "El color " + color.getName() + " ya existe. Por favor ingrese uno nuevo");
        }
    }

    public Optional<Color> getColorByName(String name) {
        return ((ColorDao) this.getDao()).getColorByName(name);
    }

    public boolean colorExists(String name) {
        Optional<Color> colorOptional = getColorByName(name);
        if (colorOptional.isPresent()) {
            return colorOptional.get().isEnabled();
        }

        return false;
    }

    public List<Color> getColorByMatch(String name) {
        return ((ColorDao) this.getDao()).searchRecordsByMatch(name, true);
    }

    public List<Color> getEnabled() {
        return ((ColorDao) this.getDao()).getEnabledColors();
    }

    @Override
    public Color save(Color color) {
        validate(color);
        Color colorToSave;
        Optional<Color> colorOptional = getColorByName(color.getName());
        if (colorOptional.isPresent()){
            colorToSave = colorOptional.get();
            colorToSave.setEnabled(true);
        } else {
            colorToSave = color;
        }
        return executeInTransaction(entityManager -> this.getDao().save(colorToSave));
    }

    public void setEnabled(Color color) {
        if (!colorExists(color.getName())) {
            throw new IllegalArgumentException(
                    "El color enviado no existe");
        }
        color.setEnabled(!color.isEnabled());
        executeInTransaction(entityManager -> this.getDao().save(color));
    }

}
