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

            return db.find(query);

        } catch (Exception e) {
            System.out.println("Can't find the post");
            e.printStackTrace();
        }
        return null;
    }


}
