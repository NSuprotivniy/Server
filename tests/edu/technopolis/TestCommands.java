package edu.technopolis;

/**
 * Created by nsuprotivniy on 28.05.17.
 */


import org.junit.*;

import javax.json.Json;
import javax.json.JsonObject;


public class TestCommands {

    static Commands commands;

    @BeforeClass
    public static void initCommands() {
        commands = new Commands();
    }

    @Before
    public void clearDataBase() {
        DataBase db = new DataBase("DataBase/Post.db");

        try {
            db.clear("posts");
            db.clear("users");
            db.clear("subscribers");
            db.clear("feeds");
            db.close();
        } catch (Exception e) {
            Assert.fail("Cannot clear table");
        }
    }

    @Test
    public void test_subscribers_save() {

        JsonObject author_content = Json.createObjectBuilder()
                .add("name", "author")
                .build();

        JsonObject author_command = Json.createObjectBuilder()
                .add("cmd", "users_save")
                .add("content", author_content)
                .build();

        JsonObject author_result = commands.handle(author_command);

        Assert.assertEquals("status should be successful", "successful", author_result.getString("status"));
        int author_id = Integer.parseInt(author_result.getJsonArray("content").getJsonObject(0).getString("id"));


        System.out.println(author_command.toString());

        JsonObject user_content = Json.createObjectBuilder()
                .add("name", "subscriber")
                .build();

        JsonObject user_command = Json.createObjectBuilder()
                .add("cmd", "users_save")
                .add("content", user_content)
                .build();

        JsonObject user_result = commands.handle(user_command);

        Assert.assertEquals("status should be successful", "successful", user_result.getString("status"));
        int user_id = Integer.parseInt(user_result.getJsonArray("content").getJsonObject(0).getString("id"));

        System.out.println(user_result.toString());


        JsonObject feed_content = Json.createObjectBuilder()
                .add("title", "Test feed")
                .add("author_id", 1)
                .build();

        JsonObject feed_command = Json.createObjectBuilder()
                .add("cmd", "feeds_save")
                .add("content", feed_content)
                .build();

        JsonObject feeds_result = commands.handle(feed_command);

        Assert.assertEquals("status should be successful", "successful", feeds_result.getString("status"));
        int feed_id = Integer.parseInt(feeds_result.getJsonArray("content").getJsonObject(0).getString("id"));

        System.out.println(feeds_result.toString());

        JsonObject subscriber_content = Json.createObjectBuilder()
                .add("feed_id", feed_id)
                .add("user_id", user_id)
                .build();

        JsonObject subscriber_command = Json.createObjectBuilder()
                .add("cmd", "subscribers_save")
                .add("content", subscriber_content)
                .build();

        JsonObject subscriber_result = commands.handle(subscriber_command);

        System.out.println(subscriber_result.toString());

        JsonObject subscriber = subscriber_result.getJsonArray("content").getJsonObject(0);

        Assert.assertEquals("status should be successful", "successful", subscriber_result.getString("status"));
        Assert.assertEquals("user id should be correct", user_id, Integer.parseInt(subscriber.getString("user_id")));
        Assert.assertEquals("feed id should be correct", feed_id, Integer.parseInt(subscriber.getString("feed_id")));


    }

}
