package cs455.scaling.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {
	ByteBuffer buf;
	
	public Client(){
		buf = ByteBuffer.allocate(64);
		
		
	}
	
	public void connect(){
		try {
			SocketChannel socketChannel = SocketChannel.open();
			socketChannel.connect(new InetSocketAddress("localhost", 50000));
			if (!socketChannel.finishConnect()){
				System.out.println("Connection Failed");
			}
			System.out.println("Connected");
			while(socketChannel != null){
				if(buf.position()==0)
					buf.put(("data: "+System.currentTimeMillis()).getBytes());
				buf.flip();
				socketChannel.write(buf);
				buf.flip();
				buf.clear();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public byte[] generateRandomMessage(){
		return new byte[10];
	}
	
	public static void main(String[] args) {
		Client c = new Client();
		c.connect();
		Scanner scan = new Scanner(System.in);
		scan.nextLine();
	}
	
}
