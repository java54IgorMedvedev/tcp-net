package telran.net;

import java.io.IOException;
import java.net.*;

public class TcpServer {
    private static final int SERVER_SOCKET_TIMEOUT_MS = 10000; 
    private final Protocol protocol;
    private final int port;
    private volatile boolean running = true;

    public TcpServer(Protocol protocol, int port) {
        this.protocol = protocol;
        this.port = port;
    }

    public void shutdown() {
        running = false;
    }

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setSoTimeout(SERVER_SOCKET_TIMEOUT_MS);
            System.out.println("Server is listening on port " + port);
            while (running) {
                try {
                    Socket socket = serverSocket.accept();
                    TcpClientServerSession session = new TcpClientServerSession(socket, protocol);
                    session.start();
                } catch (SocketTimeoutException e) {
                    System.out.println("Server socket timed out waiting for connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Server encountered an error: " + e.getMessage(), e);
        }
        System.out.println("Server has been shut down.");
    }
}
