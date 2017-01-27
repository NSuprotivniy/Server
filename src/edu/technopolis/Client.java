package edu.technopolis;

/**
 * Created by nsuprotivniy on 24.01.17.
 */

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    public static void main(String[] args) {
        int portNum = 8080;
        String hostName = "none";
        try {
            //hostName = InetAddress.getLocalHost().getHostName();
            hostName = InetAddress.getByName("127.0.0.1").getHostName();
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
            String request, response;

            request = "{\"cmd\": \"find_post\",\"content\": {\"author\": \"Noname\"}}";
            System.out.println(request);
            out.println(request);


            request = "{\"cmd\": \"find_post\",\"content\": {\"title\": \"Lorem Ipsum\"}}";
            System.out.println(request);
            out.println(request);

            request = "{\"cmd\": \"get_all_posts\",\"content\": {}}";
            System.out.println(request);
            out.println(request);

            request = "{\"cmd\": \"save_post\",\"content\": {\"title\": \"Lorem Ipsum\",\"author\": \"Noname\",\"body\": \"Lorem ipsum dolor sit amet, consectetuer adipiscing elit.\"}}";
            System.out.println(request);
            out.println(request);

            request = "{\"cmd\": \"exit\",\"content\": {}}";
            System.out.println(request);
            out.println(request);

            while ((response = in.readLine()) != null) {
                System.out.println(response);
            }


        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
