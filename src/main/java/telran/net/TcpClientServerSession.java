package telran.net;

import java.net.*;
import java.io.*;

public class TcpClientServerSession extends Thread {
    private static final int SOCKET_TIMEOUT_MS = 60000; 
    private final Socket socket;
    private final Protocol protocol;
    private boolean isShutdown = false;

    public TcpClientServerSession(Socket socket, Protocol protocol) {
        this.socket = socket;
        this.protocol = protocol;
        try {
            socket.setSoTimeout(SOCKET_TIMEOUT_MS);
        } catch (SocketException e) {
            throw new RuntimeException("Failed to set socket timeout", e);
        }
    }

    @Override
    public void run() {
        try (BufferedReader receiver = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintStream sender = new PrintStream(socket.getOutputStream())) {
            String line;
            while (!isShutdown && (line = receiver.readLine()) != null) {
                if ("shutdown".equalsIgnoreCase(line.trim())) {
                    isShutdown = true;
                    continue;
                }
                String responseStr = protocol.getResponseWithJSON(line);
                sender.println(responseStr);
            }
        } catch (SocketTimeoutException e) {
            System.out.println("Session timed out due to inactivity: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Session encountered an error: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Failed to close socket: " + e.getMessage());
            }
        }
    }
}
