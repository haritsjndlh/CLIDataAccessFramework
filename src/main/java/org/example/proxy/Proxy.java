package org.example.proxy;

import org.example.service.IDatabase;
import org.example.storage.CRUD;

import java.util.List;

public class Proxy<T> implements IDatabase<T> {
    private CRUD<T> dataSource;

    public Proxy(CRUD<T> dataSource) {
        this.dataSource = dataSource;
    }

    public void create(T obj) {
        dataSource.create(obj);
    }

    public T read(Integer id) {
        return dataSource.read(id);
    }

    public List<T> readAll() {
        return dataSource.readAll();
    }

    public void update(Integer id, T obj) {
        dataSource.update(id, obj);
    }

    public void delete(Integer id) {
        dataSource.delete(id);
    }
}
