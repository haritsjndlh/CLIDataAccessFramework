package org.example.storage;

import java.util.List;

public interface CRUD<T> {
    void create(T obj);

    T read(Integer id);

    List<T> readAll();

    void update(Integer id, T obj);

    void delete(Integer id);
}
