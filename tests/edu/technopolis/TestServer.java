package edu.technopolis;

import org.junit.*;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by nsuprotivniy on 04.02.17.
 */
public class TestServer {

    private Server server;

    private Socket client;
    private BufferedReader client_in;
    private PrintWriter client_out;


    @BeforeClass
    public static void initDataBase() {
        DataBase db = new DataBase("DataBase/Post.db");

        try {
            db.clear("posts");
            db.close();
        } catch (Exception e) {
            Assert.fail("Can't clear table");
        }

        PostsHandler postsHandler = PostsHandler.getInstance();

        JsonObject post1 = Json.createObjectBuilder()
                .add("title", "Lorem Ipsum")
                .add("author","Marcus Tullius Cicero")
                .add("body","Lorem ipsum dolor sit amet, consectetuer adipiscing elit.")
                .build();

        JsonObject post2 = Json.createObjectBuilder()
                .add("title", "Delightful")
                .add("author","Vangelis Bibakis")
                .add("body","Delightful remarkably mr on announcing themselves entreaties favourable.")
                .build();

        JsonObject post3 = Json.createObjectBuilder()
                .add("title", "Principles")
                .add("author","Vangelis Bibakis")
                .add("body","Now principles discovered off increasing how reasonably middletons men.")
                .build();

        postsHandler.save(post1);
        postsHandler.save(post2);
        postsHandler.save(post3);

        postsHandler.close();

    }

    @Before
    public void createServerAndClient() {
        try {
            Server server = new Server(8080);
            server.close();
        } catch (IOException e) {
            Assert.fail("Can't listen port 8080");
        }

        String hostName = "none";

        try {
            hostName = InetAddress.getByName("127.0.0.1").getHostName();
            System.out.println(hostName);
        }
        catch (UnknownHostException e) {
            Assert.fail("Localhost error. " + e.getMessage());
        }

        try {
            client = new Socket(hostName, 8080);
            client_in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            client_out = new PrintWriter(client.getOutputStream(), true);
        } catch (Exception e) {
            Assert.fail("Client socket error: " + e.getMessage());
        }
    }

    @After
    public void closeServer() {
        try {
            server.close();
            client.close();
        } catch (Exception e) {
            Assert.fail("Socket error: " + e.getMessage());
        }
    }

    @Test
    public void test_exit() {
        JsonObject request = Json.createObjectBuilder()
                .add("cmd", "exit")
                .build();

        client_out.println(request.toString());

        String response;

        try {
            response = client_in.readLine();
        } catch (IOException e) {
            Assert.fail("Read from client error");
        }

        Assert.assertTrue("Client connection is closed", client.isClosed());



    }


}
