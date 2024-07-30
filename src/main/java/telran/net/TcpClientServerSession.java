package telran.net;

import java.net.*;
import java.io.*;

public class TcpClientServerSession extends Thread {
    private static final int SOCKET_TIMEOUT_MS = 60000; 
    private final Socket socket;
    private final Protocol protocol;
    private volatile boolean isShutdown = false;

    public TcpClientServerSession(Socket socket, Protocol protocol) {
        this.socket = socket;
        this.protocol = protocol;
    }

    public void shutdown() {
        isShutdown = true;
    }

    @Override
    public void run() {
        try {
            socket.setSoTimeout(SOCKET_TIMEOUT_MS);
            try (BufferedReader receiver = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintStream sender = new PrintStream(socket.getOutputStream())) {
                String line;
                while (!isShutdown && (line = receiver.readLine()) != null) {
                    String responseStr = protocol.getResponseWithJSON(line);
                    sender.println(responseStr);
                }
            }
        } catch (SocketTimeoutException e) {
            if (isShutdown) {
                System.out.println("Session closed due to server shutdown.");
            } else {
                System.out.println("Session closed due to idle timeout.");
            }
        } catch (IOException e) {
            System.out.println("Session error: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Error closing socket: " + e.getMessage());
            }
        }
    }
}
