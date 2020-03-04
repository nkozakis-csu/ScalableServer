package cs455.scaling.server;

import cs455.scaling.tasks.ProcessDataTask;
import cs455.scaling.tasks.RegisterTask;
import cs455.scaling.threading.ThreadPool;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
	Selector selector;
	ServerSocketChannel serverSocketChannel;
	
	private Timer infoTimer;
	
	public AtomicInteger messageCount;
	public AtomicInteger activeConnections;
	ByteBuffer buffer;
	
	public Server() throws IOException {
		selector = Selector.open();
		messageCount = new AtomicInteger(0);
		activeConnections = new AtomicInteger(0);
		infoTimer = new Timer();
		buffer = ByteBuffer.allocate(8192);
	}
	
	public void handleSockets() {
		while (true) {
			try {
				selector.select();
				Set<SelectionKey> keys = selector.selectedKeys();
				Iterator<SelectionKey> keysIterator = keys.iterator();
				while (keysIterator.hasNext()) {
					SelectionKey key = keysIterator.next();
					if (key.isAcceptable()) {
						SocketChannel sc = serverSocketChannel.accept();
						if (sc != null) {
							ThreadPool.getInstance().addTask(new RegisterTask(this, selector, sc));
						}
					}
					
					if (key.isReadable()) {
						SocketChannel sc = (SocketChannel) key.channel();
						int numRead = sc.read(buffer);
						if (numRead > 0) {
							ThreadPool.getInstance().addTask(new ProcessDataTask(this, Arrays.copyOfRange(buffer.array(), 0, buffer.limit()), sc));
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void startServer(){
		try {
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.bind(new InetSocketAddress(50000));
			serverSocketChannel.configureBlocking(false);
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
			System.out.println("Server listening on: "+serverSocketChannel.getLocalAddress());
			infoTimer.scheduleAtFixedRate(new Throughput(this), 10000, 10000);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		try {
			Server s = new Server();
			ThreadPool.getInstance().setup(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
			s.startServer();
			s.handleSockets();
//			ThreadPool.getInstance().addTask(new Task(s::handleSockets));
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}
