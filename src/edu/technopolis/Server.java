package edu.technopolis;

import javax.json.Json;
import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.json.JsonObject;
import javax.json.JsonReader;


/**
 * Created by nsuprotivniy on 24.01.17.
 */



public class Server {

    int socket = -1;
    private ExecutorService executor;
    private Commands[] commands;

    Server(int port, int threadNum) throws IOException {
        socket = port;
        executor = Executors.newFixedThreadPool(threadNum);
        commands = new Commands[threadNum];
        for(int i = 0; i < threadNum; i++)
            commands[i] = new Commands();
    }

    private static void close(SocketChannel sc) {
        try {
            sc.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private Commands getCommandsInstance() throws ServerException {
        for(int i = 0; i < commands.length - 1; i++) {
            if(commands[i].aquire())
                return commands[i];
        }
        throw new ServerException();
    }



    public void select() {
        HashMap<SocketChannel, ByteBuffer> map = new HashMap<>();
        try (ServerSocketChannel open = openAndBind(socket)) {
            open.configureBlocking(false);
            while (true) {
                SocketChannel accept = open.accept(); //не блокируется
                if (accept != null) {
                    accept.configureBlocking(false);
                    map.put(accept, ByteBuffer.allocateDirect(1024));
                }
                map.keySet().removeIf(sc -> !sc.isOpen());
                map.forEach((sc, byteBuffer) -> {
                    try {
                        int read = sc.read(byteBuffer);
                        if (read == -1) {
                            System.out.println("Closing socket");
                            close(sc);
                        } else if (read > 0) {
                            byteBuffer.flip();
                            byte[] bytes = new byte[byteBuffer.remaining()]; // create a byte array the length of the number of bytes written to the buffer
                            byteBuffer.get(bytes); // read the bytes that were written
                            String request = new String(bytes);
                            byteBuffer.compact();
                            Commands command = commands[0];
                            try {
                                command = getCommandsInstance();
                            } catch (ServerException e) {
                                System.out.println("Connection pool is empty.");
                                e.printStackTrace();
                            }
                            Runnable task = new WorkerThread(sc, request, command);
                            executor.execute(task);
                        }
                    } catch (IOException e) {
                        close(sc);
                        e.printStackTrace();
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static ServerSocketChannel openAndBind(int port) throws IOException {
        ServerSocketChannel open = ServerSocketChannel.open();
        open.bind(new InetSocketAddress(port));
        return open;
    }

    public class ServerException extends Exception {

    }

    public static void main(String[] args) {
        try {
            Server server = new Server(8080, 4);
            server.select();
        } catch (IOException e) {
            System.out.println("Can't listen port 8080");
        }

    }
}
