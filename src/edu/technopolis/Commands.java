package edu.technopolis;

import javax.json.Json;
import javax.json.JsonObject;

/**
 * Created by nsuprotivniy on 28.05.17.
 */

/**
 * Class handles commands from client side.
 */
public class Commands {

    private PostsHandler posts;
    private FeedsHandler feeds;
    private SubscribersHandler subscribers;
    private UsersHandler users;


    Commands() {
        posts = new PostsHandler();
        feeds = new FeedsHandler();
        subscribers = new SubscribersHandler();
        users = new UsersHandler();
    }
    
    public JsonObject handle(JsonObject command) {

        String command_name = command.getString("cmd");
        JsonObject content = command.getJsonObject("content");

        switch (command_name) {


            case "subscribers_save":
                return subscribers_save(content);
            case "subscribers_find":
                return subscribers_find(content);
            case "subscribers_all":
                return subscribers_all(content);


            case "feeds_save":
                return feeds_save(content);
            case "feeds_find":
                return feeds_find(content);
            case "feeds_all":
                return feeds_all(content);

            case "users_save":
                return users_save(content);
            case "users_find":
                return users_find(content);
            case "users_all":
                return users_all(content);


            case "posts_find":
                return posts_find(content);
            case "posts_all":
                return posts_all(content);
            case "posts_save":
                return posts_save(content);


            case "exit":
                return null;

            default:
                return JSONHandler.generateAnswer(command_name, Json.createObjectBuilder().build(), false);
        }
    }



    private JsonObject users_all(JsonObject content) {
        return users.all();
    }

    private JsonObject users_find(JsonObject content) {
        return users.save(content);
    }

    private JsonObject users_save(JsonObject content) {
        return users.save(content);
    }

    private JsonObject feeds_all(JsonObject content) {
        return feeds.all();
    }

    private JsonObject feeds_find(JsonObject content) {
        return feeds.find(content);
    }

    private JsonObject feeds_save(JsonObject content) {
        return feeds.save(content);
    }

    private JsonObject posts_save(JsonObject content) {
        JsonObject post = posts.save(content);
        if (post.getString("status").equals("successful"))
            subscribers.broadcast(post.getJsonArray("content"));
        return post;
    }

    private JsonObject posts_all(JsonObject content) {
        return posts.all();
    }

    private JsonObject posts_find(JsonObject content) {
        return posts.find(content);
    }

    private JsonObject subscribers_save(JsonObject content) {
        return subscribers.save(content);
    }

    private JsonObject subscribers_all(JsonObject content) {
        return subscribers.all();
    }

    private JsonObject subscribers_find(JsonObject content) {
        return subscribers.find(content);
    }
}

 
