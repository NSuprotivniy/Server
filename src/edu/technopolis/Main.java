package edu.technopolis;


import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        try {
            Server server = new Server(8080);
            server.close();
        } catch (IOException e) {
            System.out.println("Can't listen port 8080");
        }
    }
}
