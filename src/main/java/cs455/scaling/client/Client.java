package cs455.scaling.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static cs455.scaling.tasks.Hashing.SHA1FromBytes;

public class Client {
	ByteBuffer buf;
	int rate;
	Random rand;
	LinkedList<String> hashes;
	SocketChannel socketChannel;
	ByteBuffer inBuffer;
	int numToSend = 5000;
	Timer infoTimer;
	AtomicInteger sentCount;
	AtomicInteger recvCount;
	
	public Client(int rate){
		buf = ByteBuffer.allocate(8192);
		inBuffer = ByteBuffer.allocate(1024);
		sentCount = new AtomicInteger(0);
		recvCount = new AtomicInteger(0);
		this.rate = rate;
		rand = new Random();
		hashes = new LinkedList<>();
		infoTimer = new Timer();
		
	}
	
	public void connect(){
		try {
			socketChannel = SocketChannel.open();
			socketChannel.connect(new InetSocketAddress("localhost", 50000));
			socketChannel.configureBlocking(false);
			infoTimer.scheduleAtFixedRate(new Throughput(this), 10000, 10000);
			if (!socketChannel.finishConnect()) {
				System.out.println("Connection Failed");
			}
			System.out.println("Connected");
			while(true) {
				if(numToSend > 0){
					send();
				}
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
		putRandomPayload();
		String hash = SHA1FromBytes(buf.array());
		hashes.addLast(hash);
//		System.out.println("send: "+hashes);
		buf.flip();
		socketChannel.write(buf);
		numToSend--;
		sentCount.getAndIncrement();
	}
	
	public void recv() throws IOException {
		int bytesRead = socketChannel.read(inBuffer);
		if (inBuffer.position() == inBuffer.capacity()){
			String hash = new String(Arrays.copyOf(inBuffer.array(), inBuffer.position()));
			hashes.remove(hash);
			inBuffer.clear();
			recvCount.getAndIncrement();
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
