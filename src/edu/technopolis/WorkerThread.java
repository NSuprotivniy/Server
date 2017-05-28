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
    Session session;

    public WorkerThread(SocketChannel sc, String s){
        this.command=s;
        this.sc = sc;
        this.postsHandler = PostsHandler.getInstance();
        this.session = Session.getInstance();
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

        String command = result.getString("cmd");
        switch (command) {
            case "respond":
                result = result.getJsonObject("respond");
                break;
            case "exit":
                close(sc);
                return;
            case "login":
                int clientID = result.getInt("arg");
                try {
                    session.addClient(clientID, sc);
                    System.out.println("Login " + clientID + " successful");
                } catch (Session.SessionException e) {
                    System.err.println("Client " + clientID + "double login");
                    e.printStackTrace();
                }
                result = result.getJsonObject("respond");
                break;
        }

        String respond = result.toString() + "\n";
        ByteBuffer buf = ByteBuffer.allocate(respond.length()*4);
        buf.put(respond.getBytes());
        buf.flip();

        try {
            System.out.println("wrote: " + sc.write(buf));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void close(SocketChannel sc) {
        try {
            sc.close();
        } catch (IOException e1) {
            e1.printStackTrace();
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

            Commands commands = new Commands();
            return commands.handle(commandJSON);


        } catch (Exception e) {
            System.out.println("Error while reading json command");
            System.out.println(e.getMessage());
            e.printStackTrace();
            return JSONHandler.generateAnswer("parse_query", Json.createObjectBuilder().build(), false);
        }
    }

}
