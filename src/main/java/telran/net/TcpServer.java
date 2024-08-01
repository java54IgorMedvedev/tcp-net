package telran.net;
import java.net.*;
import static telran.net.TcpConfigurationProperties.*;
public class TcpServer implements Runnable{
	Protocol protocol;
	int port;
	boolean running = true;
	public TcpServer(Protocol protocol, int port) {
		this.protocol = protocol;
		this.port = port;
	}
	public void shutdown() {
		running = false;
	}
	public void run() {
		try(ServerSocket serverSocket = new ServerSocket(port)){
			//TODO using ServerSocket method setSoTimeout 
			System.out.println("Server is listening on port " + port);
			serverSocket.setSoTimeout(SOCKET_TIMEOUT);
			while(running) {
				try {
					Socket socket = serverSocket.accept();

					TcpClientServerSession session =
							new TcpClientServerSession(socket, protocol, this);
					session.start();
				} catch (SocketTimeoutException e) {
					
				}
				
			}
			
				
			
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}