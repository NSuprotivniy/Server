package edu.technopolis;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
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

        System.out.println("body");
        System.out.println(personObject.getString("body"));
    }
}
