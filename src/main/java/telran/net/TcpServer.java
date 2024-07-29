package telran.net;
import java.net.*;
public class TcpServer {
	Protocol protocol;
	int port;
	public TcpServer(Protocol protocol, int port) {
		this.protocol = protocol;
		this.port = port;
	}
	public void run() {
		try(ServerSocket serverSocket = new ServerSocket(port)){
			System.out.println("Server is listening on port " + port);
			while(true) {
				Socket socket = serverSocket.accept();
				TcpClientServerSession session =
						new TcpClientServerSession(socket, protocol);
				Thread thread = new Thread(session);
				thread.start();
			}
				
			
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}