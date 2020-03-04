package cs455.scaling.server;

import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

public class Throughput extends TimerTask {
	
	private Server server;
	
	public Throughput(Server s){
		server = s;
		
	}
	
	@Override
	public void run() {
		System.out.printf("Server Throughput: %d messages/s \nActive client connections: %d\n",server.messageCount.get()/10, server.activeConnections.get());
		server.messageCount.set(0);
	}
}
