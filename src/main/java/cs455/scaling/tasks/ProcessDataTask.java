package cs455.scaling.tasks;

import cs455.scaling.threading.Task;
import cs455.scaling.threading.TaskInterface;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ProcessDataTask extends Task implements TaskInterface {
	
	ByteBuffer buffer;
	SocketChannel sc;
	byte[] payload;
	
	public ProcessDataTask(ByteBuffer buffer){
		super();
		this.buffer = buffer;
	}
	
	public void run() throws IOException {
		payload = buffer.array();
		System.out.println(new String(buffer.array()));
	}
	
}
