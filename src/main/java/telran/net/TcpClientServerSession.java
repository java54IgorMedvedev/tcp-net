package telran.net;

import java.net.*;
import java.io.*;
public class TcpClientServerSession {
	Socket socket;
	Protocol protocol;
	
	public TcpClientServerSession(Socket socket, Protocol protocol) {
		this.socket = socket;
		this.protocol = protocol;
	}
	public void run() {
		try (BufferedReader receiver =
				new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintStream sender = new PrintStream(socket.getOutputStream())){
			String line = null;
			while((line = receiver.readLine()) != null) {
				String responseStr = protocol.getResponseWithJSON(line);
				sender.println(responseStr);
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
