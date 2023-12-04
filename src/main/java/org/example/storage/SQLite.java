package org.example.storage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLite<T> implements CRUD<T> {
    private String url;
    private SQLiteObjectMapper<T> objectMapper;

    public SQLite(String dbName, SQLiteObjectMapper<T> objectMapper) {
        this.url = "jdbc:sqlite:" + dbName;
        this.objectMapper = objectMapper;
        initializeDatabase();
    }

    private void initializeDatabase() {
        // Initialization logic to create tables, etc., using objectMapper.getTableCreationSQL()
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            // Execute table creation SQL command
            stmt.execute(objectMapper.getTableCreationSQL());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void create(T obj) {
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(objectMapper.getInsertSQL())) {
            objectMapper.mapObjectToPreparedStatement(pstmt, obj);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public T read(Integer id) {
        T obj = null;
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(objectMapper.getSelectSQL())) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                obj = objectMapper.mapResultSetToObject(rs);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return obj;
    }

    @Override
    public List<T> readAll() {
        List<T> list = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(objectMapper.getSelectAllSQL())) {
            while (rs.next()) {
                T obj = objectMapper.mapResultSetToObject(rs);
                list.add(obj);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    @Override
    public void update(Integer id, T obj) {
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(objectMapper.getUpdateSQL())) {
            objectMapper.mapObjectToPreparedStatement(pstmt, obj);
            pstmt.setInt(objectMapper.getUpdateIdParameterIndex(), id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void delete(Integer id) {
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(objectMapper.getDeleteSQL())) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // The SQLiteObjectMapper interface should be implemented for each specific type
    public interface SQLiteObjectMapper<U> {
        String getTableCreationSQL();

        String getInsertSQL();

        String getSelectSQL();

        String getSelectAllSQL();

        String getUpdateSQL();

        int getUpdateIdParameterIndex();

        String getDeleteSQL();

        void mapObjectToPreparedStatement(PreparedStatement pstmt, U obj) throws SQLException;

        U mapResultSetToObject(ResultSet rs) throws SQLException;
    }
}
