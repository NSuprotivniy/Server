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

public class PostsHandler {

    // Class is Singleton. It makes access to subscribers in one place
    // and one connection to data base with serialized mode.
    private static final PostsHandler INSTANCE = new PostsHandler();
    public static PostsHandler getInstance() { return INSTANCE; }

    // Database with posts table.
    private DataBase db;

    // List of clients who subscribed for new posts.
    private List<PostsSubscriber> subscribers = new ArrayList<>();


    PostsHandler() {

        String db_path = "DataBase/Post.db";

        db = new DataBase(db_path);

//        Now we delegate all manipulations with table from Server to administrator.
//        So that's why we reject this.
//        JsonObject fields = Json.createObjectBuilder()
//                .add("title", "VARCHAR(255)")
//                .add("author", "VARCHAR(255)")
//                .add("body", "TEXT")
//                .build();
//        JsonObject table = Json.createObjectBuilder()
//                .add("table", "posts")
//                .add("fields", fields)
//                .build();
//
//        db = new DataBase(db_path, table);
    }

    public void close() {
        try {
            db.close();
        } catch (Exception e) {
            System.out.println("Can't close database connection");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    // Searching posts.
    public JsonObject find(JsonObject clause) {
        try {

            JsonObject query = Json.createObjectBuilder()
                    .add("table", "posts")
                    .add("clause", clause)
                    .build();

            JsonArray content = db.find(query);

            return JSONHandler.generateAnswer("find_posts", content, true);

        } catch (Exception e) {
            System.out.println("Can't find posts");
            System.out.println(e.getMessage());
            e.printStackTrace();
            return JSONHandler.generateAnswer("find_posts", clause, true);
        }
    }

    // Getting all posts.
    public JsonObject getAll() {
        try {
            JsonObject query = Json.createObjectBuilder()
                    .add("table", "posts")
                    .add("clause", Json.createObjectBuilder().build())
                    .build();

            JsonArray content = db.find(query);

            return JSONHandler.generateAnswer("get_all_posts", content, true);

        } catch (Exception e) {
            System.out.println("Can't find posts");
            System.out.println(e.getMessage());
            e.printStackTrace();
            return JSONHandler.generateAnswer("get_all_posts", Json.createArrayBuilder().build(), false);
        }
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

    // Saving new post.
    public JsonObject save(JsonObject content) {
        try {
            JsonObject query = Json.createObjectBuilder()
                    .add("table", "posts")
                    .add("content", content)
                    .build();

            JsonArray record = db.save(query);

            eventBroadcast(content);

            return JSONHandler.generateAnswer("save_post", record, true);

        } catch (Exception e) {
            System.out.println("Can't save the post");
            System.out.println(e.getMessage());
            e.printStackTrace();
            return JSONHandler.generateAnswer("save_post", content, false);
        }

    }


    // Sending signal about new post to all subscribers.
    private void eventBroadcast(JsonObject post) {

        for (PostsSubscriber subscriber : subscribers) {
            subscriber.handle_event(post);
        }
    }

    public void addSubscriber(PostsSubscriber subscriber) {
        subscribers.add(subscriber);
    }

}
