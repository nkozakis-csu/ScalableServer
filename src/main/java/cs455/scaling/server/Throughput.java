package cs455.scaling.server;

import javax.sound.sampled.EnumControl;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

public class Throughput extends TimerTask {
	
	private Server server;
	
	public Throughput(Server s){
		server = s;
		
	}
	
	@Override
	public void run() {
		double throughput = 0;
		int numCons = server.activeConnections.get();
		double[] counts = new double[numCons];
		double std=0;
		for (int i = 0; i < numCons; i++) {
			counts[i] = (double)server.messageCount.get(i)/20;
			throughput += counts[i];
			server.messageCount.set(i, 0);
			System.out.println("counts: "+i+" "+counts[i]);
		}

		if (numCons > 0) {
			double mean = throughput / numCons;
			for (int i = 0; i < numCons; i++) {
				std += Math.pow(counts[i] - mean, 2);
			}
		}
		System.out.printf("\nServer Throughput: %.3f messages/s " +
				"\nActive client connections: %d " +
				"\nMean Per-client Throughput: %.3f" +
				"\nStandard Deviation of Per-client Throughput: %.3f\n", throughput, numCons, throughput/numCons, std);

	}
}
