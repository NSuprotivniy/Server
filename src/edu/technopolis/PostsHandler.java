package edu.technopolis;

import javax.json.Json;
import javax.json.JsonObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nsuprotivniy on 26.01.17.
 */
public class PostsHandler {

    private static final PostsHandler INSTANCE = new PostsHandler();

    DataBase db;
    List<PostSubscriber> subscribers;


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

         subscribers = new ArrayList<>();
    }

    public static PostsHandler getInstance() {
        return INSTANCE;
    }

    public JsonObject find(JsonObject clause) {
        try {

            JsonObject query = Json.createObjectBuilder()
                    .add("table", "posts")
                    .add("clause", clause)
                    .build();

            JsonObject content = db.find(query);

            return JSONHandler.generateAnswer("find_post", content, true);

        } catch (Exception e) {
            System.out.println("Can't find the post");
            e.printStackTrace();
            return JSONHandler.generateAnswer("find_post", clause, true);
        }
    }

    public JsonObject get_all() {
        try {
            JsonObject query = Json.createObjectBuilder()
                    .add("table", "posts")
                    .add("clause", Json.createObjectBuilder().build())
                    .build();

            JsonObject content = db.find(query);

            return JSONHandler.generateAnswer("get_all_posts", content, true);

        } catch (Exception e) {
            System.out.println("Can't find posts");
            e.printStackTrace();
            return JSONHandler.generateAnswer("get_all_posts", null, false);
        }
    }

    public JsonObject get_last_posts(JsonObject period) {
        try {

            String clause = "created_at >= DATE('now', '-" +
                    period.getString("amount") + " " +
                    period.getString("period") + "')";

            JsonObject query = Json.createObjectBuilder()
                    .add("table", "posts")
                    .add("clause", clause)
                    .build();

            JsonObject result = db.where(query);

            return JSONHandler.generateAnswer("get_last_posts", result, true);

        } catch (Exception e) {
            System.out.println("Can't find posts");
            e.printStackTrace();
            return JSONHandler.generateAnswer("get_last_posts", period, false);
        }
    }

    public JsonObject save(JsonObject content) {
        try {
            JsonObject query = Json.createObjectBuilder()
                    .add("table", "posts")
                    .add("content", content)
                    .build();

            db.save(query);

            eventBroadcast(content);

            return JSONHandler.generateAnswer("save_post", content, true);

        } catch (Exception e) {
            System.out.println("Can't save the post");
            e.printStackTrace();
            return JSONHandler.generateAnswer("save_post", content, false);
        }

    }

    private void eventBroadcast(JsonObject post) {

        for (PostSubscriber subscriber : subscribers) {
            subscriber.handle_event(post);
        }
    }

    public void addSubscriber(PostSubscriber subscriber) {
        subscribers.add(subscriber);
    }

}
