package edu.technopolis;

import javax.json.Json;
import javax.json.JsonObject;

/**
 * Created by nsuprotivniy on 26.01.17.
 */
public class PostsHandler {

    DataBase db;

    PostsHandler() {
        String db_path = "DataBase/Post.db";

        JsonObject fields = Json.createObjectBuilder()
                .add("title", "VARCHAR(255)")
                .add("author", "VARCHAR(255)")
                .add("body", "TEXT")
                .build();
        JsonObject table = Json.createObjectBuilder()
                .add("table", "posts")
                .add("fields", fields)
                .build();

        db = new DataBase(db_path, table);
    }

    public JsonObject find(JsonObject clause) {
        try {

            JsonObject query = Json.createObjectBuilder()
                    .add("table", "posts")
                    .add("clause", clause)
                    .build();

            JsonObject content = db.find(query);

            JsonObject result = Json.createObjectBuilder()
                    .add("result", "successful")
                    .add("content", content)
                    .build();
            return result;

        } catch (Exception e) {
            System.out.println("Can't find the post");
            e.printStackTrace();
            return Json.createObjectBuilder().add("result", "unsuccessful").build();
        }
    }

    public JsonObject get_all() {
        try {
            JsonObject query = Json.createObjectBuilder()
                    .add("table", "posts")
                    .add("clause", Json.createObjectBuilder().build())
                    .build();

            JsonObject content = db.find(query);
            JsonObject result = Json.createObjectBuilder()
                    .add("result", "successful")
                    .add("content", content)
                    .build();
            return result;

        } catch (Exception e) {
            System.out.println("Can't find posts");
            e.printStackTrace();
            return Json.createObjectBuilder().add("result", "unsuccessful").build();
        }
    }

    public JsonObject save(JsonObject content) {
        try {
            JsonObject query = Json.createObjectBuilder()
                    .add("table", "posts")
                    .add("content", content)
                    .build();

            return Json.createObjectBuilder().add("result", "successful").build();

        } catch (Exception e) {
            System.out.println("Can't save the post");
            e.printStackTrace();
            return Json.createObjectBuilder().add("result", "unsuccessful").build();
        }

    }




}
