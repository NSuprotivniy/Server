package edu.technopolis;

/**
 * Created by nsuprotivniy on 24.01.17.
 */

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

    Client(String addr, int port) {

        int portNum = port;
        String hostName = "none";
        try {
            //hostName = InetAddress.getLocalHost().getHostName();
            hostName = InetAddress.getByName(addr).getHostName();
            System.out.println(hostName);
        }
        catch (UnknownHostException e) {
            System.out.println("UnknownHost");
            System.exit(-1);
        }

        try(
                Socket socket = new Socket(hostName, portNum);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        ){

            new Thread( new ServerListner(in) ).start();

            String request;

            Scanner sc = new Scanner(System.in);

            System.out.println(
                    "0 - find post by author\n" +
                            "1 - find post by title\n" +
                            "2 - get all posts\n" +
                            "3 - save post\n" +
                            "4 - subscribe posts\n" +
                            "5 - get last posts\n" +
                            "9 - exit\n"
            );

            while (true) {

                switch (sc.nextInt()) {
                    case 0:
                        request = "{\"cmd\": \"find_post\",\"content\": {\"author\": \"Marcus Tullius Cicero\"}}";
                        break;
                    case 1:
                        request = "{\"cmd\": \"find_post\",\"content\": {\"title\": \"Lorem Ipsum\"}}";
                        break;
                    case 2:
                        request = "{\"cmd\": \"get_all_posts\",\"content\": {}}";
                        break;
                    case 3:
                        request = "{\"cmd\": \"save_post\",\"content\": {\"title\": \"Lorem Ipsum\",\"author\": \"Marcus Tullius Cicero\",\"body\": \"Lorem ipsum dolor sit amet, consectetuer adipiscing elit.\"}}";
                        break;
                    case 4:
                        request = "{\"cmd\": \"subscribe_posts\", \"content\": {}}";
                        break;
                    case 5:
                        request = "{\"cmd\": \"getLastPosts\", \"content\": {\"period\": \"day\", \"amount\": \"7\"}}";
                        break;
                    default:
                        request = "{\"cmd\": \"exit\",\"content\": {}}";
                        break;
                }


                System.out.println(request);
                out.println(request);
            }


        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }


    private class ServerListner implements Runnable {
        BufferedReader in;

        ServerListner(BufferedReader in) {
            this.in = in;
        }
        public void run() {
            String response;
            try {

                while ((response = in.readLine()) != null) {
                    System.out.println(response);
                }
            } catch (Exception e) {
                System.out.println("Can't read from server");
            }
            
        }
    }

    public static void main(String[] args) {
        Client client = new Client("127.0.0.1", 8080);
    }
}
