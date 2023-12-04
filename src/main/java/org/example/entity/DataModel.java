package org.example.entity;

import org.example.storage.SerializedObject;

import java.io.Serializable;

public class DataModel implements Serializable, SerializedObject.Identifiable {
    private Integer id;
    private String name;

    // Constructor
    public DataModel(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Optionally, override toString method for better readability
    @Override
    public String toString() {
        return "DataModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}


