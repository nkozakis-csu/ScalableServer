package cs455.scaling.tasks;

import cs455.scaling.server.Server;
import cs455.scaling.threading.Task;
import cs455.scaling.threading.TaskInterface;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import static cs455.scaling.tasks.Hashing.SHA1FromBytes;

public class ProcessDataTask extends Task implements TaskInterface {
	
	ByteBuffer buffer;
	Server server;
	byte[] payload;
	
	public ProcessDataTask(Server s, ByteBuffer buffer){
		super();
		this.buffer = buffer;
		this.server = s;
	}
	
	public void run() throws IOException {
		payload = buffer.array();
		String test = SHA1FromBytes(payload);
//		System.out.println(test);
		server.messageCount.getAndIncrement();
	}
	
}
