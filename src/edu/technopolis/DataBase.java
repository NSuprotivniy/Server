package edu.technopolis;

import javax.json.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSetMetaData;

import java.util.concurrent.Semaphore;

/**
 * Created by nsuprotivniy on 25.01.17.
 *
 */

/*
    Database processor.

    It connects to database using path to db location in params.
    It implements simple ORM for working with database using sql queries. It can create new table,
    searching records in the table and save new.

    It uses JSON objects getting via params to form query to database.

    Note: there are no special functionality for sql injection protection.
 */

public class DataBase {

    private Connection connection;
    private Semaphore mutex = new Semaphore(1);

    DataBase(String path, JsonObject table) {
        try {
            connect(path);
            create(table);
        } catch (Exception e) {
            System.out.println("Connection error");
            e.printStackTrace();
        }
    }

    DataBase(String path) {
        try {
            connect(path);
        } catch (Exception e) {
            System.out.println("Connection error");
            e.printStackTrace();
        }
    }

    // DataBase connection
    public void connect(String path) throws ClassNotFoundException, SQLException
    {
        connection = null;
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        System.out.println("Base connected!");
    }

    // Table creation
    public void create(JsonObject table) throws ClassNotFoundException, SQLException
    {
        Statement statement = connection.createStatement();

        StringBuilder query = new StringBuilder();


        query.append("CREATE TABLE if not exists ");
        query.append("'" + table.getString("table") + "' ");
        query.append("('id' INTEGER PRIMARY KEY AUTOINCREMENT");

        JsonObject fields = table.getJsonObject("fields");

        fields.forEach((field, type) -> {
            query.append(", '" + field + "' " + fields.getString(field));
        });

        query.append(", created_at DATE DEFAULT (datetime('now','localtime'))");
        query.append(");");

        System.out.println(query.toString());

        statement.execute(query.toString());

        statement.close();

        //statement.execute("CREATE TABLE if not exists 'users' ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'name' text, 'phone' INT);");

        System.out.println("Table created!");
    }

    // Record saving
    public JsonArray save(JsonObject record) throws SQLException, InterruptedException
    {
        StringBuilder query = new StringBuilder();
        StringBuilder fields = new StringBuilder();
        StringBuilder values = new StringBuilder();

        query.append("INSERT INTO '" + record.getString("table") + "'");
        JsonObject content = record.getJsonObject("content");

        fields.append("(");
        values.append("VALUES (");
        int i = 0, size = content.size();
        content.forEach((field, value) -> {
            fields.append("'" + field + "', ");
            values.append("" + value + ", ");

        });

        fields.delete(fields.length() - 2, fields.length() - 1);
        values.delete(values.length() - 2, values.length() - 1);
        fields.append(")");
        values.append(")");

        query.append(" " + fields);
        query.append(" " + values);
        query.append(";");

        System.out.println(query.toString());

        for(int j = 0; mutex.tryAcquire(); j++) {
            if (j > 10) {
                throw new InterruptedException("Can't acquire mutex.");
            }
            Thread.yield();
        }

        Statement statement = connection.createStatement();
        statement.execute(query.toString());

        //statement.execute("INSERT INTO 'users' ('name', 'phone') VALUES ('Petya', 125453); ");

        ResultSet resultSet = statement.executeQuery("SELECT * FROM " + record.getString("table") + " where id=last_insert_rowid();");

        mutex.release();

        System.out.println("Record saved.");

        JsonArray result = ResultSetToJsonArray(resultSet);

        resultSet.close();
        statement.close();


        return result;
    }

    // Records searching
    public JsonArray where(JsonObject search_params) throws ClassNotFoundException, SQLException
    {
        return where(search_params.getString("table"), search_params.getString("clause"));
    }

    // Records searching
    private JsonArray where(String table, String clause) throws ClassNotFoundException, SQLException
    {
        StringBuilder query = new StringBuilder();

        query.append("SELECT * FROM " + table + " ");

        if (!clause.isEmpty()) {
            query.append("WHERE ");
            query.append(clause);
        }

        System.out.println(query.toString());

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query.toString());

        JsonArray result = ResultSetToJsonArray(resultSet);

        System.out.println("Record found");

        resultSet.close();
        statement.close();
        //statement.execute("SELECT * FROM posts WHERE author='Noname'");

        return result;
    }

    // Records searching
    public JsonArray find(JsonObject search_params) throws ClassNotFoundException, SQLException
    {
        String table = search_params.getString("table");
        StringBuilder clause = new StringBuilder();
        search_params = search_params.getJsonObject("clause");

        if (!search_params.isEmpty()) {
            search_params.forEach((field, value) -> {
                clause.append(field + "=" + value + " AND ");
            });
            clause.delete(clause.length() - 5, clause.length() - 1);
        }

        return where(table, clause.toString());
    }

    // Remove all records
    public void clear(String table) throws ClassNotFoundException, SQLException
    {
        Statement statement = connection.createStatement();
        statement.execute("DELETE from " + table);
    }


    // Close the connection
    public void close() throws ClassNotFoundException, SQLException
    {
        connection.close();

        System.out.println("Connection closed.");
    }

    private JsonArray ResultSetToJsonArray(ResultSet resultSet) throws SQLException
    {
        JsonArrayBuilder records = Json.createArrayBuilder();

        while(resultSet.next()) {
            JsonObjectBuilder record = Json.createObjectBuilder();
            ResultSetMetaData rsmd = resultSet.getMetaData();


            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                record.add(rsmd.getColumnName(i), resultSet.getString(i));
            }

            records.add(record.build());
        }

        return records.build();
    }

    public static void main(String[] args) {

        try {

            JsonObject fields = Json.createObjectBuilder()
                    .add("title", "VARCHAR(255)")
                    .add("author", "VARCHAR(255)")
                    .add("body", "TEXT")
                    .build();
            JsonObject table = Json.createObjectBuilder()
                    .add("table", "posts")
                    .add("fields", fields)
                    .build();

            DataBase db = new DataBase("DataBase/Post.db", table);

            JsonObject content = Json.createObjectBuilder()
                    .add("title", "Lorem Ipsum")
                    .add("author", "Marcus Tullius Cicero")
                    .add("body", "Lorem ipsum dolor sit amet, consectetuer adipiscing elit.")
                    .build();
            JsonObject record = Json.createObjectBuilder()
                    .add("table", "posts")
                    .add("content", content)
                    .build();
            db.save(record);


            JsonObject clause = Json.createObjectBuilder()
                    .add("author", "Noname")
                    .build();
            JsonObject query = Json.createObjectBuilder()
                    .add("table", "posts")
                    .add("clause", clause)
                    .build();

            JsonArray result = db.find(query);

            for (JsonValue row : result) {
                System.out.println(row);
            }


            db.close();

        } catch (Exception e) {
            System.out.println("DataBase error");
            e.printStackTrace();
        }

    }
}
