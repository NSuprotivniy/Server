package edu.technopolis;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nsuprotivniy on 26.01.17.
 */

/*
    Class provides access to posts database table.
    It uses DataBase processor forming JSON queries with clause
    getting via params.

 */

public class PostsHandler extends ModelHandler {

    // Class is Singleton. It makes access to subscribers in one place
    // and one connection to data base with serialized mode.
    private static final PostsHandler INSTANCE = new PostsHandler();
    public static PostsHandler getInstance() { return INSTANCE; }


    PostsHandler() {

        DB_PATH = "DataBase/Post.db";
        TABLE_NAME = "posts";
        db = new DataBase(DB_PATH);
        FIND_CMD = "posts_find";
        SAVE_CMD = "posts_save";
        GET_CMD = "posts_get";
        ALL_CMD = "posts_all";
    }


    // Getting last post for some period.
    public JsonObject getLastPosts(JsonObject period) {
        try {

            String clause = "created_at >= DATE('now', '-" +
                    period.getString("amount") + " " +
                    period.getString("period") + "')";

            JsonObject query = Json.createObjectBuilder()
                    .add("table", "posts")
                    .add("clause", clause)
                    .build();

            JsonArray result = db.where(query);

            return JSONHandler.generateAnswer("get_last_posts", result, true);

        } catch (Exception e) {
            System.out.println("Can't find posts");
            System.out.println(e.getMessage());
            e.printStackTrace();
            return JSONHandler.generateAnswer("get_last_posts", period, false);
        }
    }
}
