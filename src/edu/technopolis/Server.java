package edu.technopolis;

import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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

        SocketProcessor(Socket client) {
            this.client = client;
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
                JSONHandler jsonHandler = new JSONHandler();

                while (true) {
                    String data = new Scanner(in, "UTF-8").useDelimiter("\\r\\n\\r\\n").next();
                    System.out.println(data);

                    Matcher exit = Pattern.compile("exit").matcher(data);
                    if (exit.find()) break;

                    data = jsonHandler.readFile("data.json", StandardCharsets.UTF_8);
                    byte[] response = data.getBytes("UTF-8");

                    out.write(response, 0, response.length);

                    out.flush();
                }


            } catch (IOException e) {
                System.out.println("Stream error");
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {
        Server server = new Server(8000);
    }
}
