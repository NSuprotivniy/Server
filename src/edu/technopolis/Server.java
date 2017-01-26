package edu.technopolis;

import jdk.nashorn.internal.parser.JSONParser;

import javax.json.Json;
import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.json.JsonObject;
import javax.json.JsonReader;


/**
 * Created by nsuprotivniy on 24.01.17.
 */
public class Server {

    Server(int port) {
        ServerSocket server;

        try {
            server = new ServerSocket(port);

            System.out.println("Waiting for a client...");
            while (true) {
                Socket client = server.accept();
                System.out.println("Client connected");
                new Thread( new SocketProcessor(client) ).start();
            }


        } catch (IOException e) {
            System.out.println("Couldn't listen to port " + port);
            e.printStackTrace();
        }


    }

    private class SocketProcessor implements Runnable {

        Socket client;
        PostsHandler postsHandler;

        SocketProcessor(Socket client) {

            this.client = client;
            this.postsHandler = new PostsHandler();
        }

        public void run() {
            try {
                listenForClient();
            } catch (Throwable t) {
                System.out.println("Connection error");
                t.printStackTrace();

            } finally {
                try {
                    client.close();
                } catch (Throwable t) {
                    System.out.println("Can't close client");
                    t.printStackTrace();
                }

            }
        }

        private void listenForClient() throws Throwable {

            try {
                InputStream in = client.getInputStream();
                OutputStream out = client.getOutputStream();

                while (true) {
                    String request = new Scanner(in, "UTF-8").useDelimiter("\\r\\n\\r\\n").next();
                    System.out.println(request);

                    //request = "{\"cmd\": \"find_post\",\"content\": {\"author\": \"Noname\"}}";

                    JsonObject result = handleRequest(request);

                    if (result != null) {
                        System.out.println(result.toString());
                        byte[] response = result.toString().getBytes("UTF-8");
                        out.write(response, 0, response.length);
                        out.flush();
                    }
                }


            } catch (IOException e) {
                System.out.println("Stream error");
                e.printStackTrace();
            }
        }

        private JsonObject handleRequest(String request) {
            JsonObject commandJSON;

            try {
                JsonReader reader =  Json.createReader(new StringReader(request));
                commandJSON = reader.readObject();
            } catch (Exception e) {
                System.out.println("Parse error");
                e.printStackTrace();
                return null;
            }


            try {
                String command = commandJSON.getString("cmd");

                switch (command) {
                    case "find_post": return postsHandler.find(commandJSON.getJsonObject("content"));
                    default: return null;
                }


            } catch (Exception e) {
                System.out.println("Error while reading json command");
                e.printStackTrace();
                return null;
            }
        }
    }


    public static void main(String[] args) {
        Server server = new Server(8090);
    }
}
