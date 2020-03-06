package cs455.scaling.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.*;

import static cs455.scaling.tasks.Hashing.SHA1FromBytes;

public class Client {
	ByteBuffer buf;
	int rate;
	Random rand;
	LinkedList<String> hashes;
	SocketChannel socketChannel;
	ByteBuffer inBuffer;
	
	public Client(int rate){
		buf = ByteBuffer.allocate(8192);
		inBuffer = ByteBuffer.allocate(1024);
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
//				recv();
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
		putRandomPayload();
		String hash = SHA1FromBytes(buf.array());
		hashes.addLast(hash);
		buf.flip();
		socketChannel.write(buf);
		System.out.println("Writing buffer with has: "+ hash);
	}
	
	public void recv() throws IOException {
		int bytesRead = socketChannel.read(inBuffer);
		if (bytesRead > 0){
			System.out.println(Arrays.toString(Arrays.copyOf(inBuffer.array(), inBuffer.limit())));
			inBuffer.clear();
		}
	}
	
	public void putRandomPayload(){
		buf.clear();
		rand.nextBytes(buf.array());
		buf.position(buf.capacity());
	}
	
	public static void main(String[] args) {
		Client c = new Client(Integer.parseInt(args[0]));
		c.connect();
		Scanner scan = new Scanner(System.in);
		scan.nextLine();
	}
	
}
