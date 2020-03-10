package cs455.scaling.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static cs455.scaling.tasks.Hashing.SHA1FromBytes;

public class Client {
	ByteBuffer buf;
	String ip;
	int port;
	int rate;
	Random rand;
	LinkedList<String> hashes;
	SocketChannel socketChannel;
	ByteBuffer inBuffer;
	Timer infoTimer;
	AtomicInteger sentCount;
	AtomicInteger recvCount;
	
	public Client(String ip, int port, int rate){
		this.ip = ip;
		this.port = port;
		buf = ByteBuffer.allocate(8192);
		inBuffer = ByteBuffer.allocate(40);
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
			socketChannel.connect(new InetSocketAddress(ip, port));
			socketChannel.configureBlocking(false);
			infoTimer.scheduleAtFixedRate(new Throughput(this), 20000, 20000);
			if (!socketChannel.finishConnect()) {
				System.out.println("Connection Failed");
			} else {
				System.out.println("Connected");
				while (true) {
					send();
					recv();
					try {
						Thread.sleep(1000 / rate);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (IOException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	public void send() throws IOException, NoSuchAlgorithmException {
		putRandomPayload();
		String hash = SHA1FromBytes(buf.array());
		hashes.addLast(hash); // add to list of hashes sent
		buf.flip();
		socketChannel.write(buf);
		sentCount.getAndIncrement();
	}
	
	public void recv() throws IOException {
		int bytesRead = socketChannel.read(inBuffer);
		if (inBuffer.position() == inBuffer.capacity()){
			String hash = new String(inBuffer.array());
			boolean removed = hashes.remove(hash);
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
		Client c = new Client(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]));
		c.connect();
		Scanner scan = new Scanner(System.in);
		scan.nextLine();
	}
	
}
