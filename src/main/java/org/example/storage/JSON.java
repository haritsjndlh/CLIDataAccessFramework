package org.example.storage;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.example.entity.DataModel;

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
import java.io.File;
import java.lang.reflect.Type;

public class JSON<T> implements CRUD<T> {
    private Path path;
    private Gson gson;
    private Class<T> typeClass;

    public JSON(String filename, Class<T> typeClass) {
        this.path = Paths.get(filename);
        this.gson = new Gson();
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
            if (new File(path.toString()).length() == 0) {
                return new ArrayList<>();
            }
            Type listType = TypeToken.getParameterized(ArrayList.class, typeClass).getType();
            List<T> result = gson.fromJson(reader, listType);
            return result != null ? result : new ArrayList<>();
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
        return objects.stream()
                .filter(obj -> getIdFromObject(obj).equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void update(Integer id, T obj) {
        List<T> objects = readAll();
        List<T> updatedObjects = objects.stream()
                .map(existingObj -> getIdFromObject(existingObj).equals(id) ? obj : existingObj)
                .collect(Collectors.toList());
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
        // Implement this method based on how T's ID is retrieved.
        // For example, if T is always a DataModel, you can cast and call getId:
        if (obj instanceof DataModel) {
            return ((DataModel) obj).getId();
        }
        return null;
    }
}