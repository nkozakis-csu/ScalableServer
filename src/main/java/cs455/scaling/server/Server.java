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
	
	public Server() throws IOException {
		selector = Selector.open();
		messageCount = new AtomicInteger(0);
		activeConnections = new AtomicInteger(0);
		infoTimer = new Timer();
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
						ThreadPool.getInstance().addTask(new RegisterTask(this, selector, (ServerSocketChannel) key.channel()));
						//					this.register(selector, (ServerSocketChannel) key.channel());
					}
					
					if (key.isReadable()) {
						SocketChannel sc = (SocketChannel) key.channel();
						ByteBuffer buffer = ByteBuffer.allocate(8192);
						int numRead = sc.read(buffer);
						if (numRead > 0) {
							ThreadPool.getInstance().addTask(new ProcessDataTask(this, buffer));
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
//		ThreadPool.getInstance().addTask(new Task(this::handleSockets));
	}
	
	public void startServer(){
		try {
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.bind(new InetSocketAddress(50000));
			serverSocketChannel.configureBlocking(false);
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
			System.out.println("Server listening on: "+serverSocketChannel.getLocalAddress());
			infoTimer.scheduleAtFixedRate(new Throughput(this), 0, 20000);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
//	public void register(Selector s, ServerSocketChannel ssc){
//		try {
//			SocketChannel sc = ssc.accept();
//			if (sc != null) {
//				sc.configureBlocking(false);
//				sc.register(s, SelectionKey.OP_READ);
//				System.out.println("registered client: " + sc.getRemoteAddress());
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	
	public static void main(String[] args) {
		try {
			Server s = new Server();
			s.startServer();
			ThreadPool.getInstance().setup(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
			s.handleSockets();
//			ThreadPool.getInstance().addTask(new Task(s::handleSockets));
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}
