package telran.net;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static telran.net.TcpConfigurationProperties.*;
public class TcpServer implements Runnable{
	Protocol protocol;
	int port;
	boolean running = true;
	ExecutorService executor;
	public TcpServer(Protocol protocol, int port) {
		this.protocol = protocol;
		this.port = port;
		executor = Executors.newFixedThreadPool(getNumberOfThreads());
	}
	private int getNumberOfThreads() {
		
		return Runtime.getRuntime().availableProcessors();
	}
	public void shutdown() {
		running = false;
		executor.shutdownNow();
		try {
			executor.awaitTermination(MAX_WAITING_TIME_IN_SECONDS, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			//No interruptions
		}
	}
	public void run() {
		try(ServerSocket serverSocket = new ServerSocket(port)){
			// using ServerSocket method setSoTimeout for unblocking "accept" 
			//otherwise there is case of forever waiting on "accept" method
			System.out.println("Server is listening on port " + port);
			serverSocket.setSoTimeout(SOCKET_TIMEOUT);
			while(running) {
				try {
					Socket socket = serverSocket.accept();

					TcpClientServerSession session =
							new TcpClientServerSession(socket, protocol, this);
					executor.execute(session);
				} catch (SocketTimeoutException e) {
					
				}
				
			}
		
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}