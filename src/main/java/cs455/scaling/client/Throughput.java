package cs455.scaling.client;

import cs455.scaling.client.Client;

import java.util.TimerTask;

public class Throughput extends TimerTask {
	
	private Client client;
	
	public Throughput(Client c){
		client = c;
		
	}
	
	@Override
	public void run() {
		System.out.printf("Client in last 20 seconds sent: %d \t Received: %d \n",client.sentCount.get(), client.recvCount.get());
		client.sentCount.set(0);
		client.recvCount.set(0);
	}
}
