package cs455.scaling.threading;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class ThreadPool {

	public static class BatchTimeoutTask extends TimerTask {

		@Override
		public void run() {
			ThreadPool.getInstance().startNextBatch();
		}
	}


	private int numThreads, batchTime, batchSize;
	private Worker[] workers;
	private final ArrayList<Integer> freeThreads;
	LinkedList<Task> batch;
	Timer batchTimer = new Timer();
	
	private static ThreadPool threadPool = new ThreadPool();
	
	public static ThreadPool getInstance() {
		return threadPool;
	}
	
	private ThreadPool(){
		freeThreads = new ArrayList<>();
	}

	public void setup(int numThreads, int batchSize, int batchTime){
        this.numThreads = numThreads;
        this.batchSize = batchSize;
        this.batchTime = batchTime;
        workers = new Worker[numThreads];
        for (int i = 0; i < numThreads; i++) {
            freeThreads.add(i);
            workers[i] = new Worker(i);
            workers[i].start();
        }
        batch = new LinkedList<>();
    }
	
	public Worker getAvailableWorker(){
		synchronized (freeThreads) {
			if (freeThreads.size() == 0) {
				try {
					freeThreads.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return workers[freeThreads.remove(0)];
		}
		
	}
	
	public void addAvailableWorker(int id){
		synchronized (freeThreads) {
			freeThreads.add(id);
			freeThreads.notify();
		}
	}
	
	public synchronized void addTask(Task t){
		batch.addLast(t);
		if (batch.size() == 1) {
			batchTimer.schedule(new BatchTimeoutTask(), batchTime);
		}
		if (batch.size() == batchSize) {
			startNextBatch();
		}
	}
	
	public synchronized void startNextBatch() {
		if(batch.size()>0) {
			batchTimer.purge();
			Worker w = getAvailableWorker();
			w.assign(batch);
			batch = new LinkedList<>();
		}
	}
	
}