package cs455.scaling.tasks;

import cs455.scaling.server.Server;
import cs455.scaling.threading.Task;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import static cs455.scaling.tasks.Hashing.SHA1FromBytes;

public class ProcessDataTask extends Task {
	
//	ByteBuffer buffer;
	Server server;
	SocketChannel socket;
	byte[] payload;
	
	public ProcessDataTask(Server s, byte[] bytes, SocketChannel sc){
		super();
		this.payload = bytes;
		this.socket = sc;
		this.server = s;
	}
	
	public void run() throws IOException {
		String test = SHA1FromBytes(payload);
		server.messageCount.getAndIncrement();
		byte[] replyBytes = test.getBytes();
		ByteBuffer reply = ByteBuffer.allocate(replyBytes.length);
		reply.put(replyBytes);
		reply.flip();
		socket.write(reply);
	}
	
}
