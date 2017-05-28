package edu.technopolis;

/**
 * Created by nsuprotivniy on 28.05.17.
 */


import org.junit.*;

import javax.json.Json;
import javax.json.JsonObject;
import java.util.Random;


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

        JsonObject author_set = generateUser();
        int author_id = Integer.parseInt(author_set.getJsonArray("content").getJsonObject(0).getString("id"));

        JsonObject user_set = generateUser();
        int user_id = Integer.parseInt(user_set.getJsonArray("content").getJsonObject(0).getString("id"));

        JsonObject feed_set = generateFeed(user_id);
        int feed_id = Integer.parseInt(feed_set.getJsonArray("content").getJsonObject(0).getString("id"));


        JsonObject subscriber_set = generateSubscriber(user_id, feed_id);
        JsonObject subscriber = subscriber_set.getJsonArray("content").getJsonObject(0);

        Assert.assertEquals("status should be successful", "successful", subscriber_set.getString("status"));
        Assert.assertEquals("user id should be correct", user_id, Integer.parseInt(subscriber.getString("user_id")));
        Assert.assertEquals("feed id should be correct", feed_id, Integer.parseInt(subscriber.getString("feed_id")));


    }


    @Test public void test_posts_save() {

        JsonObject user_set = generateUser();
        int user_id = Integer.parseInt(user_set.getJsonArray("content").getJsonObject(0).getString("id"));

        JsonObject feed_set = generateFeed(user_id);
        int feed_id = Integer.parseInt(feed_set.getJsonArray("content").getJsonObject(0).getString("id"));

        JsonObject posts_set = generatePost(user_id, feed_id);
        JsonObject subscriber = posts_set.getJsonArray("content").getJsonObject(0);

        Assert.assertEquals("status should be successful", "successful", posts_set.getString("status"));
        Assert.assertEquals("author id should be correct", user_id, Integer.parseInt(subscriber.getString("author_id")));
        Assert.assertEquals("feed id should be correct", feed_id, Integer.parseInt(subscriber.getString("feed_id")));


    }


    private JsonObject generateUser() {

        Random random = new Random();

        JsonObject content = Json.createObjectBuilder()
                .add("name", "test_user_" + random.nextInt())
                .build();

        JsonObject command = Json.createObjectBuilder()
                .add("cmd", "users_save")
                .add("content", content)
                .build();

        JsonObject result = commands.handle(command);

        Assert.assertEquals("cmd should be login", "login", result.getString("cmd"));

        result = result.getJsonObject("respond");

        Assert.assertEquals("status should be successful", "successful", result.getString("status"));

        return  result;
    }


    private JsonObject generateFeed(int author_id) {

        Random random = new Random();

        JsonObject content = Json.createObjectBuilder()
                .add("title", "test_feed_" + random.nextInt())
                .add("author_id", author_id)
                .build();

        JsonObject command = Json.createObjectBuilder()
                .add("cmd", "feeds_save")
                .add("content", content)
                .build();

        JsonObject result = commands.handle(command);

        Assert.assertEquals("cmd should be respond", "respond", result.getString("cmd"));

        result = result.getJsonObject("respond");

        Assert.assertEquals("status should be successful", "successful", result.getString("status"));

        return result;
    }

    private JsonObject generateSubscriber(int user_id, int feed_id) {
        JsonObject content = Json.createObjectBuilder()
                .add("feed_id", feed_id)
                .add("user_id", user_id)
                .build();

        JsonObject command = Json.createObjectBuilder()
                .add("cmd", "subscribers_save")
                .add("content", content)
                .build();

        JsonObject result = commands.handle(command);

        Assert.assertEquals("cmd should be respond", "respond", result.getString("cmd"));

        result = result.getJsonObject("respond");

        Assert.assertEquals("status should be successful", "successful", result.getString("status"));

        return result;

    }

    private JsonObject generatePost(int author_id, int feed_id) {

        Random random = new Random();

        JsonObject content = Json.createObjectBuilder()
                .add("title", "post_title" + random.nextInt())
                .add("body", "post_body" + random.nextInt())
                .add("feed_id", feed_id)
                .add("author_id", author_id)
                .build();

        JsonObject command = Json.createObjectBuilder()
                .add("cmd", "posts_save")
                .add("content", content)
                .build();

        JsonObject result = commands.handle(command);

        Assert.assertEquals("cmd should be respond", "respond", result.getString("cmd"));

        result = result.getJsonObject("respond");

        Assert.assertEquals("status should be successful", "successful", result.getString("status"));

        return result;

    }
}
