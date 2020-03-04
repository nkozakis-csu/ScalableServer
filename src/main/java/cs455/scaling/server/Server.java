package cs455.scaling.server;

import cs455.scaling.tasks.ProcessDataTask;
import cs455.scaling.tasks.RegisterTask;
import cs455.scaling.threading.ThreadPool;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;

public class Server {
	Selector selector;
	ServerSocketChannel serverSocketChannel;
	
	public Server() throws IOException {
		selector = Selector.open();
		this.startServer();
	}
	
	public void handleSockets() {
		while (true) {
			try {
				System.out.println("handling sockets");
				selector.select();
				Set<SelectionKey> keys = selector.selectedKeys();
				Iterator<SelectionKey> keysIterator = keys.iterator();
				while (keysIterator.hasNext()) {
					SelectionKey key = keysIterator.next();
					if (key.isAcceptable()) {
						ThreadPool.getInstance().addTask(new RegisterTask(selector, (ServerSocketChannel) key.channel()));
						//					this.register(selector, (ServerSocketChannel) key.channel());
					}
					
					if (key.isReadable()) {
						SocketChannel sc = (SocketChannel) key.channel();
						ByteBuffer buffer = ByteBuffer.allocate(8192);
						System.out.println("reading channel");
						int numRead = sc.read(buffer);
						if (numRead > 0) {
							ThreadPool.getInstance().addTask(new ProcessDataTask(buffer));
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
			ThreadPool.getInstance().setup(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
			s.handleSockets();
//			ThreadPool.getInstance().addTask(new Task(s::handleSockets));
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}
