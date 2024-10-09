package com.mmdevelopment.models.daos;

import com.mmdevelopment.utils.exceptions.NonexistentEntityException;

import java.util.List;

public interface DAO<T> {

    public T create(T object);
    public void update(T object) throws NonexistentEntityException;
    public T findById(int id);
    public void delete(T entity) throws NonexistentEntityException;
    public List<T> getAll();
    public List<T> getAll(int maxResults, int firstResult);
    public List<T> getAll(boolean all, int maxResults, int firstResult);

}
