package org.example.storage;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public class TXT<T> implements CRUD<T> {
    private  Path path;
    private  TextSerializer<T> serializer;
    private  TextDeserializer<T> deserializer;

    public TXT(String filename, TextSerializer<T> serializer, TextDeserializer<T> deserializer) {
        this.path = Paths.get(filename);
        if (Files.notExists(path)) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        this.serializer = serializer;
        this.deserializer = deserializer;
    }

    @Override
    public void create(T obj) {
        try {
            if (Files.notExists(path)) {
                Files.createFile(path);
            }
            try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.APPEND)) {
                writer.write(serializer.serialize(obj));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public T read(Integer id) {
        try (Stream<String> lines = Files.lines(path)) {
            return lines.map(deserializer::deserialize)
                    .filter(Objects::nonNull)
                    .filter(obj -> deserializer.getId(obj).equals(id))
                    .findFirst()
                    .orElse(null);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<T> readAll() {
        try (Stream<String> lines = Files.lines(path)) {
            return lines.map(deserializer::deserialize)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public void update(Integer id, T obj) {
        List<T> objects = readAll();
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (T currentObj : objects) {
                if (deserializer.getId(currentObj).equals(id)) {
                    writer.write(serializer.serialize(obj));
                } else {
                    writer.write(serializer.serialize(currentObj));
                }
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Integer id) {
        List<T> objects = readAll();
        objects.removeIf(obj -> deserializer.getId(obj).equals(id));
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (T obj : objects) {
                writer.write(serializer.serialize(obj));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Interfaces for object serialization and deserialization
    public interface TextSerializer<U> {
        String serialize(U obj);
    }

    public interface TextDeserializer<U> {
        U deserialize(String str);
        Integer getId(U obj);
    }
}