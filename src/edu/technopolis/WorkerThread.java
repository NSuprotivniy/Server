package edu.technopolis;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by kubri on 5/28/2017.
 */

public class WorkerThread implements Runnable {
    private String command;
    private SocketChannel sc;
    private PostsHandler postsHandler;

    public WorkerThread(SocketChannel sc, String s){
        this.command=s;
        this.sc = sc;
        this.postsHandler = PostsHandler.getInstance();
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName()+" Start. Command = "+command);
        processCommand();
        System.out.println(Thread.currentThread().getName()+" End.");
    }

    private void processCommand() {
        JsonObject result = handleRequest(command, sc);
        System.out.println("res: "+ result);

        String respond = result.toString() + "\n";
        ByteBuffer buf = ByteBuffer.allocate(respond.length()*4);
        CharBuffer cbuf = buf.asCharBuffer();
        cbuf.put(respond);
        cbuf.flip();

        try {
            System.out.println("wrote: " + sc.write(buf));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString(){
        return this.command;
    }

    private JsonObject handleRequest(String request, SocketChannel client) {
        JsonObject commandJSON;

        try {
            JsonReader reader = Json.createReader(new StringReader(request));
            commandJSON = reader.readObject();
        } catch (Exception e) {
            System.out.println("Parse error");
            System.out.println(e.getMessage());
            e.printStackTrace();
            return JSONHandler.generateAnswer("parse_query", Json.createObjectBuilder().build(), false);
        }

        try {
            String command = commandJSON.getString("cmd");

            switch (command) {
                case "exit":
                    return null;
                case "subscribe_posts":
                    return subscribePosts(client);
                case "find_posts":
                    return postsHandler.find(commandJSON.getJsonObject("content"));
                case "get_all_posts":
                    return postsHandler.getAll();
                case "getLastPosts":
                    return postsHandler.getLastPosts(commandJSON.getJsonObject("content"));
                case "save_post":
                    return postsHandler.save(commandJSON.getJsonObject("content"));
                default:
                    return JSONHandler.generateAnswer(command, Json.createObjectBuilder().build(), false);
            }


        } catch (Exception e) {
            System.out.println("Error while reading json command");
            System.out.println(e.getMessage());
            e.printStackTrace();
            return JSONHandler.generateAnswer("parse_query", Json.createObjectBuilder().build(), false);
        }
    }

    private JsonObject subscribePosts(SocketChannel client) {
        try {
            PostsSubscriber subscriber = new PostsSubscriber(client);
            postsHandler.addSubscriber(subscriber);
            return JSONHandler.generateAnswer("subscribe_posts", Json.createObjectBuilder().build(), true);
        } catch (Exception e) {
            System.out.println("Can't create subscriber");
            System.out.println(e.getMessage());
            e.printStackTrace();
            return JSONHandler.generateAnswer("subscribe_posts", Json.createObjectBuilder().build(), false);
        }
    }
}
