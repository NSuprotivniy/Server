package edu.technopolis;

import javax.json.*;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by nsuprotivniy on 25.01.17.
 */
public class JSONHandler {


    public String readFile(String path, Charset encoding) {
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(path));
            return new String(encoded, encoding);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void main(String[] args) {

        JSONHandler jsonHandler = new JSONHandler();

        String data = jsonHandler.readFile("data.json", StandardCharsets.UTF_8);
        JsonReader reader = Json.createReader(new StringReader(data));
        JsonObject personObject = reader.readObject();
        reader.close();


        System.out.println(personObject.getString("table"));
        JsonObject fields = personObject.getJsonObject("fields");

        fields.forEach((s, jsonValue) -> {
            System.out.println(s);
            System.out.println(jsonValue.toString());

        });

        JsonObject content = Json.createObjectBuilder()
                .add("title", "Lorem Ipsum")
                .add("author", "Noname")
                .add("body", "Lorem ipsum dolor sit amet, consectetuer adipiscing elit.")
                .build();

        content.forEach((s, jsonValue) -> {
            System.out.println(s);
            content.getString(s);
        });


    }
}
