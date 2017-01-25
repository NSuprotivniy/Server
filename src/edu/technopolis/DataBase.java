package edu.technopolis;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSetMetaData;

/**
 * Created by nsuprotivniy on 25.01.17.
 */
public class DataBase {

    public static Connection connection;
    public static Statement statement;
    public static ResultSet resultSet;

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
        statement = connection.createStatement();

        StringBuilder query = new StringBuilder();


        query.append("CREATE TABLE if not exists ");
        query.append("'" + table.getString("table") + "' ");
        query.append("('id' INTEGER PRIMARY KEY AUTOINCREMENT");

        JsonObject fields = table.getJsonObject("fields");

        fields.forEach((field, type) -> {
            query.append(", '" + field + "' " + fields.getString(field));
        });

        query.append(");");

        System.out.println(query.toString());

        statement.execute(query.toString());

        //statement.execute("CREATE TABLE if not exists 'users' ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'name' text, 'phone' INT);");

        System.out.println("Table created!");
    }

    // Record saving
    public void save(JsonObject record) throws SQLException
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

        statement.execute(query.toString());


        //statement.execute("INSERT INTO 'users' ('name', 'phone') VALUES ('Petya', 125453); ");


        System.out.println("Record saved.");
    }

    // Records searching
    public JsonObject find(JsonObject clause) throws ClassNotFoundException, SQLException
    {
        StringBuilder query = new StringBuilder();

        query.append("SELECT * FROM " + clause.getString("table") + " ");
        query.append("WHERE ");

        clause = clause.getJsonObject("clause");

        clause.forEach((field, value) -> {
            query.append(field + "=" + value + " AND ");
        });
        query.delete(query.length() - 5, query.length() - 1);

        System.out.println(query.toString());

        resultSet = statement.executeQuery(query.toString());

        JsonObjectBuilder result = Json.createObjectBuilder();

        while(resultSet.next()) {
            JsonObjectBuilder record = Json.createObjectBuilder();
            ResultSetMetaData rsmd = resultSet.getMetaData();


            for (int i = 2; i <= rsmd.getColumnCount(); i++) {
                record.add(rsmd.getColumnName(i), resultSet.getString(i));
            }

            result.add(resultSet.getString(1), record.build());
        }

        System.out.println("Record found");

        return result.build();

    }

    // Close the connection
    public void close() throws ClassNotFoundException, SQLException
    {
        statement.close();
        connection.close();
        resultSet.close();

        System.out.println("Connection closed.");
    }

    public static void main(String[] args) {
        DataBase db = new DataBase();
        try {
            db.connect("Post.db");

            JsonObject fields = Json.createObjectBuilder()
                    .add("title", "VARCHAR(255)")
                    .add("author", "VARCHAR(255)")
                    .add("body", "TEXT")
                    .build();
            JsonObject table = Json.createObjectBuilder()
                    .add("table", "posts")
                    .add("fields", fields)
                    .build();
            db.create(table);

            JsonObject content = Json.createObjectBuilder()
                    .add("title", "Lorem Ipsum")
                    .add("author", "Noname")
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

            JsonObject result = db.find(query);

            result.forEach((id, row) -> {
                System.out.println(id);
                System.out.println(row);
            });

            db.close();

        } catch (Exception e) {
            System.out.println("DataBase error");
            e.printStackTrace();
        }

    }
}
