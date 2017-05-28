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
    private boolean Aquired;

    Commands() {
        posts = new PostsHandler();
        feeds = new FeedsHandler();
        subscribers = new SubscribersHandler();
        users = new UsersHandler();
        Aquired = false;
    }

    public synchronized boolean aquire() {
        if(Aquired == false) {
            Aquired = true;
            return true;
        }
        return false;
    }

    public synchronized void release() {
        Aquired = false;
    }
    
    public JsonObject handle(JsonObject command) {

        String command_name = command.getString("cmd");
        JsonObject content = command.getJsonObject("content");
        JsonObject result;

        switch (command_name) {


            case "subscribers_save": return serverCommandSend(subscribers_save(content));
            case "subscribers_find": return serverCommandSend(subscribers_find(content));
            case "subscribers_all":  return serverCommandSend(subscribers_all(content));


            case "feeds_save": return serverCommandSend(feeds_save(content));
            case "feeds_find": return serverCommandSend(feeds_find(content));
            case "feeds_all":  return serverCommandSend(feeds_all(content));

            case "users_save": return serverCommandLogin(users_save(content));
            case "users_find": return serverCommandSend(users_find(content));
            case "users_all":  return serverCommandSend(users_all(content));


            case "posts_find": return serverCommandSend(posts_find(content));
            case "posts_all":  return serverCommandSend(posts_all(content));
            case "posts_save": return serverCommandSend(posts_save(content));


            case "exit":
                return Json.createObjectBuilder().add("cmd", "exit").build();

            default:
                return JSONHandler.generateAnswer(command_name, Json.createObjectBuilder().build(), false);
        }


    }

    private JsonObject serverCommandSend(JsonObject result) {
        JsonObject worker_task = Json.createObjectBuilder()
                .add("cmd", "respond")
                .add("respond", result)
                .build();

        return worker_task;
    }

    private JsonObject serverCommandLogin(JsonObject result) {

        try {
            JsonObject user = result.getJsonArray("content").getJsonObject(0);
            int client_id = Integer.parseInt(user.getString("id"));

            JsonObject worker_task = Json.createObjectBuilder()
                    .add("cmd", "login")
                    .add("arg", client_id)
                    .add("respond", result)
                    .build();

            return worker_task;
        } catch (Exception e) {
            System.out.println("Cannot login user");
            System.out.println(e.getMessage());
            e.printStackTrace();
            return JSONHandler.generateAnswer("users_login", result, false);
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

 
