package cs455.scaling.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class Server {
	
	ByteBuffer buf = ByteBuffer.allocate(64);
	
	public Server(){

	}
	
	public void listen(){
		try {
			ServerSocketChannel sock = ServerSocketChannel.open();
			sock.socket().bind(new InetSocketAddress(50000));
			while (true){
				SocketChannel socketChannel = sock.accept();
				if (!socketChannel.finishConnect()) {
					System.out.println("connection failed");
				}
				System.out.println("Connection received");
				while(socketChannel != null){
					System.out.println("reading channel");
					int numRead = socketChannel.read(buf);
					if(numRead>0){
						System.out.println("Server: "+buf.getChar());
						buf.clear();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Done reading");
	}
	
	public static void main(String[] args) {
		Server s = new Server();
		s.listen();
//		Scanner scan = new Scanner(System.in);
//		scan.nextLine();
	}
}
