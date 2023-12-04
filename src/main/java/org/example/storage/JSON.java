package org.example.storage;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.stream.Collectors;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JSON<T> implements CRUD<T> {
    private Path path;
    private Gson gson;
    private TypeToken<List<T>> typeToken;
    private Class<T> typeClass;

    public JSON(String filename, Class<T> typeClass) {
        this.path = Paths.get(filename);
        this.gson = new Gson();
        this.typeToken = new TypeToken<List<T>>() {
        };
        this.typeClass = typeClass;
        createFileIfNotExists();
    }

    private void createFileIfNotExists() {
        try {
            if (Files.notExists(path)) {
                Files.createFile(path);
                // Initialize with an empty JSON array
                writeToFile(Collections.emptyList());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public List<T> readAll() {
        try (Reader reader = Files.newBufferedReader(path)) {
            List<T> result = gson.fromJson(reader, typeToken.getType());
            return result != null ? new ArrayList<>(result) : new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public void create(T obj) {
        List<T> objects = readAll();
        objects.add(obj);
        writeToFile(objects);
    }

    @Override
    public T read(Integer id) {
        List<T> objects = readAll();
        return objects.stream().filter(obj -> getIdFromObject(obj).equals(id)).findFirst().orElse(null);
    }

    @Override
    public void update(Integer id, T obj) {
        List<T> objects = readAll();
        List<T> updatedObjects = objects.stream().map(existingObj -> getIdFromObject(existingObj).equals(id) ? obj : existingObj).collect(Collectors.toList());
        writeToFile(updatedObjects);
    }

    @Override
    public void delete(Integer id) {
        List<T> objects = readAll();
        objects.removeIf(obj -> getIdFromObject(obj).equals(id));
        writeToFile(objects);
    }

    private void writeToFile(List<T> objects) {
        try (Writer writer = Files.newBufferedWriter(path)) {
            gson.toJson(objects, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // You need to implement this method based on how your object's ID is stored and retrieved
    private Integer getIdFromObject(T obj) {
        // Assuming the T class has a getId method or some way to retrieve its identifier
        // For example, if you have a getId() method in your object, you can do:
        // return obj.getId();
        // But this needs to be replaced with whatever logic your objects use to store their ID.
        return null;
    }
}