package cs455.scaling.server;

import com.sun.org.apache.xerces.internal.dom.PSVIElementNSImpl;
import sun.nio.ch.ThreadPool;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;

public class Server {
	
	ByteBuffer buf = ByteBuffer.allocate(256);
	Selector selector;
	ServerSocketChannel serverSocketChannel;
	
	public Server(int numThreads) throws IOException {
		selector = Selector.open();
		this.startServer();
	}
	
	public void handleSockets() throws IOException, InterruptedException {
		while (true) {
			selector.select();
			Set<SelectionKey> keys = selector.selectedKeys();
			Iterator<SelectionKey> keysIterator = keys.iterator();
			while (keysIterator.hasNext()) {
				SelectionKey key = keysIterator.next();
				if (key.isAcceptable()) {
					this.register(selector, serverSocketChannel);
				}
				
				if (key.isReadable()) {
					System.out.println("reading channel");
					SocketChannel sc = (SocketChannel) key.channel();
					int numRead = sc.read(buf);
					if (numRead > 0) {
						System.out.println("Server: " + buf.getChar());
						buf.clear();
						
					}
				}
			}
			Thread.sleep(1000);
		}
	}
	
	public void startServer(){
		try {
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.socket().bind(new InetSocketAddress(50000));
			serverSocketChannel.configureBlocking(false);
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
			System.out.println("Server listening on: "+serverSocketChannel.getLocalAddress());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void register(Selector s, ServerSocketChannel ssc){
		try {
			SocketChannel sc = ssc.accept();
			sc.configureBlocking(false);
			sc.register(s, SelectionKey.OP_READ);
			System.out.println("registered client: "+sc.getRemoteAddress());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		try {
			Server s = new Server(Integer.parseInt(args[0]));
			s.handleSockets();
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}
