package edu.technopolis;

import java.nio.channels.SocketChannel;
import java.util.HashMap;

/**
 * Created by kubri on 5/28/2017.
 */
public class Session {
    private static final Session INSTANCE = new Session();
    private HashMap<Integer, SocketChannel> map;
    public static Session getInstance() { return INSTANCE; }

    Session() {
        map = new HashMap<>();
    }

    public void addClient(int clientID, SocketChannel clientSc) throws SessionException {
        if (map.containsKey(clientID))
            throw new SessionException();
        map.put(clientID, clientSc);
    }

    public SocketChannel getClientSocket(int clientID) throws SessionException {
        SocketChannel sc = map.get(clientID);
        if(sc == null)
            throw new SessionException();
        return sc;
    }

    class SessionException extends Exception {
    }
}

