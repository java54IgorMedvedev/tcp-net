package telran.net;

import java.net.*;
import java.io.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class TcpServer {
    private static final int SERVER_SOCKET_TIMEOUT_MS = 60000; 
    private final Protocol protocol;
    private final int port;
    private volatile boolean running = true;
    private final CopyOnWriteArrayList<TcpClientServerSession> sessions = new CopyOnWriteArrayList<>();

    public TcpServer(Protocol protocol, int port) {
        this.protocol = protocol;
        this.port = port;
    }

    public void shutdown() {
        running = false;
        for (TcpClientServerSession session : sessions) {
            session.shutdown();
        }
        sessions.clear();
    }

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setSoTimeout(SERVER_SOCKET_TIMEOUT_MS);
            System.out.println("Server is listening on port " + port);
            while (running) {
                try {
                    Socket socket = serverSocket.accept();
                    TcpClientServerSession session = new TcpClientServerSession(socket, protocol);
                    sessions.add(session);
                    session.start();
                } catch (SocketTimeoutException e) {
                    if (running) {
                        System.out.println("Server socket timed out waiting for connection: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Server error: " + e.getMessage());
        }
    }
}
