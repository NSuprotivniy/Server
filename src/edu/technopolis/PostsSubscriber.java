package edu.technopolis;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by nsuprotivniy on 28.01.17.
 */
public class PostsSubscriber {

    private SocketChannel socket;
    private PrintWriter out;

    PostsSubscriber(SocketChannel socket){
        this.socket = socket;
    }

    public void handle_event(JsonObject post) {
        JsonObject message = JSONHandler.generateAnswer("new_post", post, true);

        String respond = message.toString() + "\n";
        ByteBuffer buf = ByteBuffer.allocate(respond.length()*4);
        CharBuffer cbuf = buf.asCharBuffer();
        cbuf.put(respond);
        cbuf.flip();

        try {
            System.out.println("wrote: " + socket.write(buf));
            System.out.println("Trying to write" + message.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
