package telran.net;

import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import static telran.net.TcpConfigurationProperties.*;

public class TcpServer implements Runnable {
    Protocol protocol;
    int port;
    boolean running = true;
    private final ExecutorService executor;

    public TcpServer(Protocol protocol, int port) {
        this.protocol = protocol;
        this.port = port;
        this.executor = Executors.newFixedThreadPool(getNumberOfThreads());
    }

    private int getNumberOfThreads() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.availableProcessors();
    }

    public void shutdown() {
        running = false;
        executor.shutdown();
    }

    public void awaitTermination() throws InterruptedException {
        executor.awaitTermination(1, TimeUnit.DAYS);
    }

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);
            serverSocket.setSoTimeout(SOCKET_TIMEOUT);
            while (running) {
                try {
                    Socket socket = serverSocket.accept();
                    executor.execute(new TcpClientServerSession(socket, protocol, this));
                } catch (SocketTimeoutException e) {
                    System.out.println("Server socket timed out waiting for connection: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
