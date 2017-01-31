package edu.technopolis;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by nsuprotivniy on 28.01.17.
 */
public class PostsSubscriber {

    private Socket socket;
    private PrintWriter out;

    PostsSubscriber(Socket socket){
        this.socket = socket;
        try {
            this.out = new PrintWriter(socket.getOutputStream(), true);
        } catch (Exception e) {
            System.out.println("Can't get output stream for client socket.");
            e.printStackTrace();
        }
    }

    public void handle_event(JsonObject post) {
        JsonObject message = JSONHandler.generateAnswer("new_post", post, true);
        out.println(message.toString());
    }

}
