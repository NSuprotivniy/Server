package edu.technopolis;

/**
 * Created by nsuprotivniy on 24.01.17.
 */

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {


    Scanner sc = new Scanner(System.in);
    int client_id;

    Client(String addr, int port) {

        int portNum = port;
        String hostName = "none";
        try {
            //hostName = InetAddress.getLocalHost().getHostName();
            hostName = InetAddress.getByName(addr).getHostName();
            System.out.println(hostName);
        } catch (UnknownHostException e) {
            System.out.println("UnknownHost");
            System.exit(-1);
        }



        try (
                Socket socket = new Socket(hostName, portNum);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        ) {

            new Thread(new ServerListener(in)).start();
            Thread.yield();

            login(out);

            String request;

            System.out.println("save post");
            System.out.println("find post");
            System.out.println("all posts");
            System.out.println("save user");
            System.out.println("find user");
            System.out.println("all users");
            System.out.println("save feed");
            System.out.println("find feed");
            System.out.println("all feeds");
            System.out.println("save subscriber");
            System.out.println("find subscribers");
            System.out.println("all subscribers");

            System.out.println("exit");

            while (true) {

                switch (sc.nextLine()) {

                    case "save post":
                        request = posts_save();
                        break;
                    case "find post":
                        request = posts_find();
                        break;
                    case "all posts":
                        request = posts_all();
                        break;
                    case "save user":
                        request = users_save();
                        break;
                    case "find user":
                        request = users_find();
                        break;
                    case "all users":
                        request = users_all();
                        break;
                    case "save feed":
                        request = feeds_save();
                        break;
                    case "find feed":
                        request = feeds_find();
                        break;
                    case "all feeds":
                        request = feeds_all();
                        break;
                    case "save subscriber":
                        request = subscribers_save();
                        break;
                    case "find subscribers":
                        request = subscribers_find();
                        break;
                    case "all subscribers":
                        request = subscribers_all();
                        break;

                    case "exit":
                        request = "{\"cd\": \"exit\",\"content\": {}}";
                        break;

                    default: continue;
                }


                System.out.println(request);
                out.println(request);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void login(PrintWriter out) {

        System.out.println("Login");

        System.out.println("User name");
        String user_name = sc.nextLine();

        JsonObject content = Json.createObjectBuilder()
                .add("name", user_name)
                .build();

        JsonObject command = Json.createObjectBuilder()
                .add("cmd", "login")
                .add("content", content)
                .build();

        out.println(command.toString());
    }


    private String users_all() {

        JsonObject command = Json.createObjectBuilder()
                .add("cmd", "users_all")
                .add("client_id", client_id)
                .build();

        return command.toString();
    }

    private String users_find() {

        System.out.println("User name");
        String user_name = sc.nextLine();

        JsonObject content = Json.createObjectBuilder()
                .add("name", user_name)
                .build();

        JsonObject command = Json.createObjectBuilder()
                .add("cmd", "users_find")
                .add("client_id", client_id)
                .add("content", content)
                .build();
        return command.toString();
    }

    private String users_save() {

        System.out.println("User name");
        String user_name = sc.nextLine();

        JsonObject content = Json.createObjectBuilder()
                .add("name", user_name)
                .build();

        JsonObject command = Json.createObjectBuilder()
                .add("cmd", "users_save")
                .add("client_id", client_id)
                .add("content", content)
                .build();
        return command.toString();
    }

    private String feeds_all() {

        JsonObject command = Json.createObjectBuilder()
                .add("cmd", "feeds_all")
                .add("client_id", client_id)
                .build();
        return command.toString();
    }

    private String feeds_find() {

        System.out.println("Feed title");
        String feeds_title = sc.nextLine();


        JsonObject content = Json.createObjectBuilder()
                .add("title", feeds_title)
                .build();

        JsonObject command = Json.createObjectBuilder()
                .add("cmd", "feeds_find")
                .add("client_id", client_id)
                .add("content", content)
                .build();
        return command.toString();
    }

    private String feeds_save() {

        System.out.println("Feed title");
        String feeds_title = sc.nextLine();


        JsonObject content = Json.createObjectBuilder()
                .add("title", feeds_title)
                .add("author_id", client_id)
                .build();

        JsonObject command = Json.createObjectBuilder()
                .add("cmd", "feeds_save")
                .add("client_id", client_id)
                .add("content", content)
                .build();
        return command.toString();
    }

    private String posts_save() {

        System.out.println("Post title");
        String posts_title = sc.nextLine();
        System.out.println("Post body");
        String posts_body = sc.nextLine();
        System.out.println("Feed id");
        int feed_id = sc.nextInt();


        JsonObject content = Json.createObjectBuilder()
                .add("title", posts_title)
                .add("body", posts_body)
                .add("author_id", client_id)
                .add("feed_id", feed_id)
                .build();

        JsonObject command = Json.createObjectBuilder()
                .add("cmd", "posts_save")
                .add("client_id", client_id)
                .add("content", content)
                .build();
        return command.toString();
    }

    private String posts_all() {


        JsonObject command = Json.createObjectBuilder()
                .add("cmd", "posts_all")
                .add("client_id", client_id)
                .build();
        return command.toString();
    }

    private String posts_find() {

        System.out.println("Post title");
        String posts_title = sc.nextLine();

        JsonObject content = Json.createObjectBuilder()
                .add("title", posts_title)
                .add("client_id", client_id)
                .build();

        JsonObject command = Json.createObjectBuilder()
                .add("cmd", "posts_find")
                .add("client_id", client_id)
                .add("content", content)
                .build();
        return command.toString();
    }

    private String subscribers_save() {

        System.out.println("Feed id");
        int feed_id = sc.nextInt();

        JsonObject content = Json.createObjectBuilder()
                .add("user_id", client_id)
                .add("feed_id", feed_id)
                .build();

        JsonObject command = Json.createObjectBuilder()
                .add("cmd", "subscribers_save")
                .add("client_id", client_id)
                .add("content", content)
                .build();
        return command.toString();
    }

    private String subscribers_all() {

        JsonObject command = Json.createObjectBuilder()
                .add("cmd", "subscribers_all")
                .add("client_id", client_id)
                .build();
        return command.toString();
    }

    private String subscribers_find() {

        System.out.println("Feed id");
        int feed_id = sc.nextInt();

        JsonObject content = Json.createObjectBuilder()
                .add("feed_id", feed_id)
                .build();

        JsonObject command = Json.createObjectBuilder()
                .add("cmd", "subscribers_find")
                .add("client_id", client_id)
                .add("content", content)
                .build();
        return command.toString();
    }



    private class ServerListener implements Runnable {
        BufferedReader in;

        ServerListener(BufferedReader in) {
            this.in = in;
        }
        public void run() {
            String response;
            try {

                while ((response = in.readLine()) != null) {

                    System.out.println("response: " + response);

                    response = response.replaceAll("\0\r?\n", "");

                    JsonReader reader = Json.createReader(new StringReader(response));
                    JsonObject json = reader.readObject();

                    if (
                            json.getString("status").equals("successful") &&
                            json.getString("cmd").equals("login")
                            ) {

                        client_id = Integer.parseInt(json.getJsonObject("content").getString("id"));
                        System.out.println("Client id: " + client_id);

                    }
                }



            } catch (Exception e) {
                System.out.println("Can't read from server");
                e.printStackTrace();
            }
            
        }
    }

    public static void main(String[] args) {
        Client client = new Client("127.0.0.1", 8080);
    }
}
