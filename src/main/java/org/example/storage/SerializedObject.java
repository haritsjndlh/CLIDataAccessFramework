package org.example.storage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SerializedObject<T extends Serializable & SerializedObject.Identifiable> implements CRUD<T> {
    private String filePath;

    public SerializedObject(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void create(T obj) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath))) {
            out.writeObject(obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public T read(Integer id) {
        // Deserialization does not inherently support querying by ID.
        // You would need a specific implementation based on your object's design.
        return null;
    }

    @Override
    public List<T> readAll() {
        List<T> objects = new ArrayList<>();
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath))) {
            while (true) {
                objects.add((T) in.readObject());
            }
        } catch (EOFException ignored) {
            // End of file reached
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return objects;
    }

    @Override
    public void update(Integer id, T obj) {
        List<T> objects = readAll();
        for (int i = 0; i < objects.size(); i++) {
            if (objects.get(i).getId().equals(id)) {
                objects.set(i, obj);
                writeAll(objects);
                return;
            }
        }
    }

    @Override
    public void delete(Integer id) {
        List<T> objects = readAll();
        objects.removeIf(obj -> obj.getId().equals(id));
        writeAll(objects);
    }

    private void writeAll(List<T> objects) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath))) {
            for (T obj : objects) {
                out.writeObject(obj);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface Identifiable {
        Integer getId();
    }
}
