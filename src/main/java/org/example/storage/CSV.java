package org.example.storage;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public class CSV<T> implements CRUD<T> {
    private Path path;
    private CSVParser<T> parser;
    private CSVFormatter<T> formatter;

    // Constructor to set the file path and CSV utilities
    public CSV(String filename, CSVParser<T> parser, CSVFormatter<T> formatter) {
        this.path = Paths.get(filename);
        this.parser = parser;
        this.formatter = formatter;
    }

    @Override
    public void create(T obj) {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.APPEND)) {
            writer.write(formatter.format(obj));
            writer.newLine();
        } catch (IOException e) {
            // Exception handling here
        }
    }

    @Override
    public T read(Integer id) {
        try (Stream<String> lines = Files.lines(path)) {
            return lines.map(parser::parse)
                    .filter(Objects::nonNull)
                    .filter(obj -> parser.getId(obj).equals(id))
                    .findFirst()
                    .orElse(null);
        } catch (IOException e) {
            // Exception handling here
            return null;
        }
    }

    @Override
    public List<T> readAll() {
        try (Stream<String> lines = Files.lines(path)) {
            return lines.map(parser::parse)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            // Exception handling here
            return Collections.emptyList();
        }
    }

    @Override
    public void update(Integer id, T obj) {
        List<T> objects = readAll();
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            objects.stream()
                    .map(existingObj -> parser.getId(existingObj).equals(id) ? obj : existingObj)
                    .map(formatter::format)
                    .forEach(line -> {
                        try {
                            writer.write(line);
                            writer.newLine();
                        } catch (IOException e) {
                            // Exception handling here
                        }
                    });
        } catch (IOException e) {
            // Exception handling here
        }
    }

    @Override
    public void delete(Integer id) {
        List<T> objects = readAll();
        objects.removeIf(obj -> parser.getId(obj).equals(id));
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            objects.stream()
                    .map(formatter::format)
                    .forEach(line -> {
                        try {
                            writer.write(line);
                            writer.newLine();
                        } catch (IOException e) {
                            // Exception handling here
                        }
                    });
        } catch (IOException e) {
            // Exception handling here
        }
    }

    // Interfaces for CSV parsing and formatting
    public interface CSVParser<U> {
        U parse(String line);

        Integer getId(U obj);
    }

    public interface CSVFormatter<U> {
        String format(U obj);
    }
}

