package org.example;

import org.example.entity.DataModel;
import org.example.proxy.Proxy;
import org.example.storage.*;

import java.util.Scanner;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    private static final Map<String, CRUD<DataModel>> dataAccessMap = new HashMap<>();

    static {
        // Initialize the data access objects for each file format
        dataAccessMap.put("CSV", new CSV<>("csv_file.csv", new DataModelCSVParser(), new DataModelCSVFormatter()));
        dataAccessMap.put("JSON", new JSON<>("json_file.json", DataModel.class));
        dataAccessMap.put("TXT", new TXT<>("txt_file.txt", new DataModelTextSerializer(), new DataModelTextDeserializer()));
        dataAccessMap.put("SERIALIZED", new SerializedObject<>("serialized_file.ser"));
        dataAccessMap.put("SQLITE", new SQLite<>("database.db", new DataModelSQLiteObjectMapper()));
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the CLI Data Access Framework");

        CRUD<DataModel> dataAccess = null;
        String format = null; // Declare 'format' outside the while loop

        while (dataAccess == null) {
            System.out.println("Select data format (CSV, JSON, TXT, SERIALIZED, SQLITE):");
            format = scanner.nextLine().toUpperCase();
            dataAccess = dataAccessMap.get(format);

            if (dataAccess == null) {
                System.out.println("Invalid data format selected. Please try again.");
            }
        }

        Proxy<DataModel> proxy = new Proxy<>(dataAccess);


        while (true) {
            System.out.println("Enter command (CREATE, READ, READALL, UPDATE, DELETE, EXIT):");
            String command = scanner.nextLine().toUpperCase();
            switch (command) {
                case "CREATE":
                    // Assume DataModel constructor takes parameters for each field
                    System.out.println("Enter data for the new record (format: id,data):");
                    String data = scanner.nextLine();
                    // Parse data and create new DataModel object
                    DataModel newData = parseDataModel(data);
                    proxy.create(newData);
                    System.out.println("Record created in " + format + " format.");
                    break;
                case "READ":
                    System.out.println("Enter the ID of the record to read:");
                    int readId = Integer.parseInt(scanner.nextLine());
                    DataModel readData = proxy.read(readId);
                    System.out.println(readData != null ? readData : "Record not found.");
                    break;
                case "READALL":
                    List<DataModel> allData = proxy.readAll();
                    allData.forEach(System.out::println);
                    break;
                case "UPDATE":
                    System.out.println("Enter the ID of the record to update:");
                    int updateId = Integer.parseInt(scanner.nextLine());
                    System.out.println("Enter the new data for the record (format: id,data):");
                    String updateData = scanner.nextLine();
                    DataModel updatedData = parseDataModel(updateData);
                    proxy.update(updateId, updatedData);
                    System.out.println("Record updated.");
                    break;
                case "DELETE":
                    System.out.println("Enter the ID of the record to delete:");
                    int deleteId = Integer.parseInt(scanner.nextLine());
                    proxy.delete(deleteId);
                    System.out.println("Record deleted.");
                    break;
                case "EXIT":
                    scanner.close();
                    System.out.println("Exiting the application.");
                    return;
                default:
                    System.out.println("Invalid command.");
            }
        }
    }

    // Helper method to parse input data into a DataModel object
    private static DataModel parseDataModel(String data) {
        String[] fields = data.split(",");
        // Check if the fields array has the required number of elements
        if (fields.length < 2) {
            // Handle error - the input data does not have enough information
            System.out.println("Invalid data format. Please enter data as 'id,data'."); // Or handle this case as appropriate
            return null;
        }
        try {
            Integer id = Integer.parseInt(fields[0].trim()); // Convert first part to Integer
            String name = fields[1].trim(); // Second part is the name
            return new DataModel(id, name);
        } catch (NumberFormatException e) {
            // Handle error - the id is not a valid integer
            System.out.println("Invalid ID format. ID should be an integer.");
            return null; // Or handle this case as appropriate
        }
    }


    // Placeholder implementations for parser, formatter, serializer, deserializer, and object mapper
    private static class DataModelCSVParser implements CSV.CSVParser<DataModel> {
        @Override
        public DataModel parse(String line) {
            String[] fields = line.split(",");
            Integer id = Integer.parseInt(fields[0].trim());
            String name = fields[1].trim();
            return new DataModel(id, name);
        }

        @Override
        public Integer getId(DataModel obj) {
            return obj.getId();
        }
    }


    private static class DataModelCSVFormatter implements CSV.CSVFormatter<DataModel> {
        @Override
        public String format(DataModel obj) {
            return obj.getId() + "," + obj.getName();
        }
    }


    private static class DataModelTextSerializer implements TXT.TextSerializer<DataModel> {
        @Override
        public String serialize(DataModel obj) {
            return obj.getId() + "," + obj.getName();
        }
    }


    private static class DataModelTextDeserializer implements TXT.TextDeserializer<DataModel> {
        @Override
        public DataModel deserialize(String str) {
            String[] fields = str.split(",");
            Integer id = Integer.parseInt(fields[0].trim());
            String name = fields[1].trim();
            return new DataModel(id, name);
        }

        @Override
        public Integer getId(DataModel obj) {
            return obj.getId();
        }
    }


    private static class DataModelSQLiteObjectMapper implements SQLite.SQLiteObjectMapper<DataModel> {
        @Override
        public String getTableCreationSQL() {
            return "CREATE TABLE IF NOT EXISTS DataModels (id INTEGER PRIMARY KEY, name TEXT);";
        }

        @Override
        public String getInsertSQL() {
            return "INSERT INTO DataModels (id, name) VALUES (?, ?);";
        }

        @Override
        public String getSelectSQL() {
            return "SELECT * FROM DataModels WHERE id = ?;";
        }

        @Override
        public String getSelectAllSQL() {
            return "SELECT * FROM DataModels;";
        }

        @Override
        public String getUpdateSQL() {
            return "UPDATE DataModels SET name = ? WHERE id = ?;";
        }

        @Override
        public int getUpdateIdParameterIndex() {
            return 2; // Since ID is the second parameter in the update SQL
        }

        @Override
        public String getDeleteSQL() {
            return "DELETE FROM DataModels WHERE id = ?;";
        }

        @Override
        public void mapObjectToPreparedStatement(java.sql.PreparedStatement pstmt, DataModel obj) throws java.sql.SQLException {
            pstmt.setInt(1, obj.getId());
            pstmt.setString(2, obj.getName());
        }

        @Override
        public DataModel mapResultSetToObject(java.sql.ResultSet rs) throws java.sql.SQLException {
            int id = rs.getInt("id");
            String name = rs.getString("name");
            return new DataModel(id, name);
        }
    }

}