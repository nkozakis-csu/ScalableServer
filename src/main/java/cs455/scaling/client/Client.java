package cs455.scaling.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;
import static cs455.scaling.tasks.Hashing.SHA1FromBytes;

public class Client {
	ByteBuffer buf;
	int rate;
	Random rand;
	LinkedList<String> hashes;
	SocketChannel socketChannel;
	
	public Client(int rate){
		buf = ByteBuffer.allocate(8192);
		this.rate = rate;
		rand = new Random();
		hashes = new LinkedList<>();
		
	}
	
	public void connect(){
		try {
			socketChannel = SocketChannel.open();
			socketChannel.connect(new InetSocketAddress("localhost", 50000));
			if (!socketChannel.finishConnect()) {
				System.out.println("Connection Failed");
			}
			System.out.println("Connected");
			while(true) {
				send();
				recv();
				try {
					Thread.sleep(1000/rate);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void send() throws IOException {
		buf.clear();
		byte[] payload = generateRandomPayload();
		String hash = SHA1FromBytes(payload);
		hashes.addLast(hash);
		buf.put(payload);
		buf.flip();
		socketChannel.write(buf);
		buf.flip();
	}
	
	public void recv(){
	
	}
	
	public byte[] generateRandomPayload(){
		byte[] payload = new byte[8192];
		rand.nextBytes(payload);
		return payload;
	}
	
	public static void main(String[] args) {
		Client c = new Client(Integer.parseInt(args[0]));
		c.connect();
		Scanner scan = new Scanner(System.in);
		scan.nextLine();
	}
	
}
