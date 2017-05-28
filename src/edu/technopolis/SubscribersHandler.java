package edu.technopolis;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by nsuprotivniy on 28.05.17.
 */

/**
 * Handle all requests to subscribers table in database.
 */


public class SubscribersHandler extends ModelHandler {

    // Class is Singleton. It makes access to subscribers in one place
    // and one connection to data base with serialized mode.
//    private static final SubscribersHandler INSTANCE = new SubscribersHandler();
//    public static SubscribersHandler getInstance() { return INSTANCE; }


    String SEND_POST_CMD = "subscribers_send_post";
    Session session;

    SubscribersHandler() {
        DB_PATH = "DataBase/Post.db";
        TABLE_NAME = "subscribers";
        db = new DataBase(DB_PATH);
        FIND_CMD = "subscribers_find";
        SAVE_CMD = "subscribers_save";
        GET_CMD = "subscribers_get";
        ALL_CMD = "subscribers_all";
        session = Session.getInstance();
    }

    SubscribersHandler(DataBase db) {
        this.db = db;
        DB_PATH = "DataBase/Post.db";
        TABLE_NAME = "subscribers";
        FIND_CMD = "subscribers_find";
        SAVE_CMD = "subscribers_save";
        GET_CMD = "subscribers_get";
        ALL_CMD = "subscribers_all";
        session = Session.getInstance();
    }


    public JsonObject broadcast(JsonArray posts) {

        try {

            JsonObject post = posts.getJsonObject(0);

            int feed_id = Integer.parseInt(post.getString("feed_id"));

            JsonObject clause = Json.createObjectBuilder()
                    .add("feed_id", feed_id)
                    .build();

            JsonObject search_result = find(clause);

            if (search_result.getString("status").equals("unsuccessful")) return search_result;

            JsonArray subscribers = search_result.getJsonArray("content");



            for (int i = 0; i < subscribers.size(); i++) {

                JsonObject subscriber = subscribers.getJsonObject(i);
                int client_id = Integer.parseInt(subscriber.getString("user_id"));

                try {
                    SocketChannel socket = session.getClientSocket(client_id);
                    sendToClient(socket, post);
                } catch (Session.SessionException e) {
                    System.out.println("No socket for " + client_id);
                }

            }

            return JSONHandler.generateAnswer(SEND_POST_CMD, post, true);

        } catch (Exception e) {
            System.out.println("Cannot send post");
            System.out.println(e.getMessage());
            e.printStackTrace();
            return JSONHandler.generateAnswer(SEND_POST_CMD, posts, false);
        }
    }

    private void sendToClient(SocketChannel socket, JsonObject post) throws IOException {

        JsonObject message = JSONHandler.generateAnswer("new_post", post, true);

        String respond = message.toString() + "\n";

        ByteBuffer buf = ByteBuffer.allocate(respond.length()*4);
        buf.put(respond.getBytes());
        buf.flip();
        socket.write(buf);
    }
}
