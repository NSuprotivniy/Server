package edu.technopolis;


import org.junit.*;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * Created by nsuprotivniy on 31.01.17.
 */


public class TestPostsHandler {

    static PostsHandler postsHandler;

    @BeforeClass
    public static void createPostsHandler() {
        postsHandler = PostsHandler.getInstance();
    }

    @Before
    public void clearDataBase() {
        DataBase db = new DataBase("DataBase/Post.db");

        try {
            db.clear("posts");
            db.close();
        } catch (Exception e) {
            Assert.fail("Can't clear table");
        }
    }

    @AfterClass
    public void closePostsHandler() {
        postsHandler.close();
    }

    @Test
    public void test_save() {
        JsonObject post = Json.createObjectBuilder()
                .add("title", "Lorem Ipsum")
                .add("author", "Marcus Tullius Cicero")
                .add("body", "Lorem ipsum dolor sit amet, consectetuer adipiscing elit.")
                .build();

        JsonObject save_result = postsHandler.save(post);

        JsonObject save_result_post = save_result.getJsonArray("content").getJsonObject(0);

        Assert.assertEquals("status should be successful", "successful", save_result.getString("status"));
        Assert.assertEquals("cmd should be save_post", "save_post", save_result.getString("cmd"));
        Assert.assertEquals("posts titles should be the same", post.getString("title"), save_result_post.getString("title"));
        Assert.assertEquals("posts authors should be the same", post.getString("author"), save_result_post.getString("author"));
        Assert.assertEquals("posts bodies should be the same", post.getString("body"), save_result_post.getString("body"));
    }

    @Test
    public void test_find() {

        JsonObject post = Json.createObjectBuilder()
                .add("title", "Lorem Ipsum")
                .add("author", "Marcus Tullius Cicero")
                .add("body", "Lorem ipsum dolor sit amet, consectetuer adipiscing elit.")
                .build();

        JsonArray save_result_content = postsHandler.save(post).getJsonArray("content");


        JsonObject query = Json.createObjectBuilder()
                .add("title", "Lorem Ipsum")
                .build();

        String result = postsHandler.find(query).toString();
        String expectation = JSONHandler.generateAnswer("find_posts", save_result_content,true).toString();

        Assert.assertEquals("Incorrect find result", expectation, result);
    }

    @Test
    public void test_find_notFound() {
        JsonObject post = Json.createObjectBuilder()
                .add("title", "Lorem Ipsum")
                .add("author", "Marcus Tullius Cicero")
                .add("body", "Lorem ipsum dolor sit amet, consectetuer adipiscing elit.")
                .build();


        JsonObject query = Json.createObjectBuilder()
                .add("title", "Nonexistent post")
                .build();

        JsonObject result = postsHandler.find(query);

        Assert.assertEquals("cmd should be find_post", "find_posts", result.getString("cmd"));
        Assert.assertEquals("status should be successful", "successful", result.getString("status"));
        Assert.assertTrue("content array should be empty", result.getJsonArray("content").isEmpty());
    }

    @Test
    public void test_getAll() {

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

        JsonObject getAll_result = postsHandler.getAll();

        JsonArray records = getAll_result.getJsonArray("content");

        String first_body = records.getJsonObject(0).getString("body");
        String second_body = records.getJsonObject(1).getString("body");
        String third_body = records.getJsonObject(2).getString("body");

        Assert.assertEquals("status should be successful", "successful", getAll_result.getString("status"));
        Assert.assertEquals("cmd should be get_all_posts", "get_all_posts", getAll_result.getString("cmd") );
        Assert.assertEquals("Posts amount should be 3", 3, records.size());
        Assert.assertEquals("First posts body should be equal", post1.getString("body"), first_body);
        Assert.assertEquals("Second posts body should be equal", post2.getString("body"), second_body);
        Assert.assertEquals("Third posts body should be equal", post3.getString("body"), third_body);
    }

    @Test
    public void test_getLastPosts() {
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

        // Change created_at datetime to 10 days from now for post1.
        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:DataBase/Post.db");
            Statement statement = connection.createStatement();
            statement.execute("UPDATE posts SET created_at = datetime('now','localtime', '-10 days') WHERE ID = (SELECT ID FROM posts LIMIT 1);");
            statement.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("SQL error");
        }

        JsonObject query = Json.createObjectBuilder()
                .add("period", "days")
                .add("amount", "5")
                .build();

        JsonObject result = postsHandler.getLastPosts(query);
        JsonArray result_content = result.getJsonArray("content");

        Assert.assertEquals("cmd should be get_last_posts", "get_last_posts", result.getString("cmd"));
        Assert.assertEquals("status should be successful", "successful", result.getString("status"));
        Assert.assertEquals("it should be only two posts in result", 2, result_content.size());

        String result_first_body = result_content.getJsonObject(0).getString("body");
        String result_second_body = result_content.getJsonObject(1).getString("body");

        Assert.assertEquals("first result body should be equal to post2 body", post2.getString("body"), result_first_body);
        Assert.assertEquals("second result body should be equal to post3 body", post3.getString("body"), result_second_body);
    }
}
