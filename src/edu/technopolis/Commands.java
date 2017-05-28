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
    private Session session;
    private DataBase db;

    private boolean Aquired;

    Commands() {
        db = new DataBase("DataBase/Post.db");
        posts = new PostsHandler(db);
        feeds = new FeedsHandler(db);
        subscribers = new SubscribersHandler(db);
        users = new UsersHandler(db);
        session = Session.getInstance();
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

        if(login_validate(command) == false) {
            return JSONHandler.generateAnswer("authorize error", Json.createObjectBuilder().build(), false);
        }

        switch (command_name) {

            case "login": return serverCommandLogin(command);

            case "subscribers_save": return serverCommandSend(subscribers_save(content));
            case "subscribers_find": return serverCommandSend(subscribers_find(content));
            case "subscribers_all":  return serverCommandSend(subscribers_all(content));

            case "feeds_save": return serverCommandSend(feeds_save(content));
            case "feeds_find": return serverCommandSend(feeds_find(content));
            case "feeds_all":  return serverCommandSend(feeds_all(content));

            case "users_save": return serverCommandSend(users_save(content));
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

    private boolean login_validate(JsonObject command) {

        if (command.getString("cmd").equals("login")) return true;

        int client_id = command.getInt("client_id");

        System.out.println("Validation " + client_id);

        try {
            session.getClientSocket(client_id);
        } catch (Session.SessionException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private JsonObject serverCommandSend(JsonObject result) {
        JsonObject worker_task = Json.createObjectBuilder()
                .add("cmd", "respond")
                .add("respond", result)
                .build();

        return worker_task;
    }

    private JsonObject serverCommandLogin(JsonObject command) {

        try {

            JsonObject content = command.getJsonObject("content");

            JsonObject find_result = users.find(content);

            JsonObject user;

            if (find_result.getString("status").equals("successful")) {
                user = find_result.getJsonArray("content").getJsonObject(0);
            } else {
                JsonObject save_result = users.save(content);
                user = save_result.getJsonArray("content").getJsonObject(0);
            }


            return Json.createObjectBuilder()
                    .add("cmd", "login")
                    .add("arg", Integer.parseInt(user.getString("id")))
                    .add("respond", JSONHandler.generateAnswer("login", user, true))
                    .build();
        } catch (Exception e) {

            System.out.println(e.getMessage());
            e.printStackTrace();

            return Json.createObjectBuilder()
                .add("cmd", "respond")
                .add("respond", JSONHandler.generateAnswer("login", command, false))
                .build();
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

 
