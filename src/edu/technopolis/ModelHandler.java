package edu.technopolis;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;

/**
 * Created by nsuprotivniy on 28.05.17.
 */


/**
 * Defines the common functionality for all database handlers.
 * Each function get json object as a param and prepare request to DataBase class.
 * Return value is json too, it includes status of operation and result content.
 * Implements REST API.
 */

public abstract class ModelHandler {


    protected DataBase db;
    protected String TABLE_NAME;
    protected String DB_PATH;

    protected String FIND_CMD;
    protected String SAVE_CMD;
    protected String GET_CMD;
    protected String ALL_CMD;




    public JsonObject find(JsonObject clause) {
        try {

            JsonObject query = Json.createObjectBuilder()
                    .add("table", TABLE_NAME)
                    .add("clause", clause)
                    .build();

            JsonArray content = db.find(query);

            return JSONHandler.generateAnswer(FIND_CMD, content, true);

        } catch (Exception e) {
            System.out.println("Can't find");
            System.out.println(e.getMessage());
            e.printStackTrace();
            return JSONHandler.generateAnswer(FIND_CMD, clause, true);
        }
    }


    public JsonObject all() {
        try {
            JsonObject query = Json.createObjectBuilder()
                    .add("table", TABLE_NAME)
                    .add("clause", Json.createObjectBuilder().build())
                    .build();

            JsonArray content = db.find(query);

            return JSONHandler.generateAnswer(ALL_CMD, content, true);

        } catch (Exception e) {
            System.out.println("Can't get all");
            System.out.println(e.getMessage());
            e.printStackTrace();
            return JSONHandler.generateAnswer(ALL_CMD, Json.createArrayBuilder().build(), false);
        }
    }


    public JsonObject save(JsonObject content) {
        try {
            JsonObject query = Json.createObjectBuilder()
                    .add("table", TABLE_NAME)
                    .add("content", content)
                    .build();

            JsonArray record = db.save(query);

            return JSONHandler.generateAnswer(SAVE_CMD, record, true);

        } catch (Exception e) {
            System.out.println("Can't save");
            System.out.println(e.getMessage());
            e.printStackTrace();
            return JSONHandler.generateAnswer(SAVE_CMD, content, false);
        }
    }

    public JsonObject edit(JsonObject content) {
        return null;
    }

    public  JsonObject remove(JsonObject object) {
        return null;
    }

    public JsonObject get(JsonObject content) {
        return null;
    }
}
