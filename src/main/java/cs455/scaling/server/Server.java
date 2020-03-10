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
import java.util.concurrent.atomic.AtomicIntegerArray;

public class Server {
	Selector selector;
	ServerSocketChannel serverSocketChannel;
	
	private Timer infoTimer;
	
	public AtomicIntegerArray messageCount;
	public AtomicInteger activeConnections;
	int port;
	ByteBuffer buffer;
	
	public Server(int port) throws IOException {
		selector = Selector.open();
		this.port = port;
		messageCount = new AtomicIntegerArray(10000);
		activeConnections = new AtomicInteger(0);
		infoTimer = new Timer();
		buffer = ByteBuffer.allocate(8192);
	}
	
	public void handleSockets() {
		while (true) {
			try {
				selector.selectNow();
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
						if (buffer.position() == buffer.capacity()) {
							ThreadPool.getInstance().addTask(
									new ProcessDataTask(this, Arrays.copyOfRange(buffer.array(), 0, buffer.limit()), sc, (int) key.attachment()));
							buffer.clear();
						}
					}
					keysIterator.remove();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void startServer(){
		try {
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.bind(new InetSocketAddress(this.port));
			serverSocketChannel.configureBlocking(false);
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
			infoTimer.scheduleAtFixedRate(new Throughput(this), 20000, 20000);
			System.out.println("Listening on "+serverSocketChannel.getLocalAddress());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		try {
			Server s = new Server(Integer.parseInt(args[0]));
			ThreadPool.getInstance().setup(Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
			s.startServer();
			s.handleSockets();
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}
