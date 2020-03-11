package cs455.scaling.threading;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class ThreadPool {

	public static class BatchTimeoutTask extends TimerTask {

		@Override
		public void run() {
			ThreadPool.getInstance().startNextBatch(); // starts next batch if batch-time is reached
		}
	}


	private int numThreads, batchTime, batchSize;
	private Worker[] workers;
	private final ArrayList<Integer> freeThreads;
	LinkedList<Task> batch;
	Timer batchTimer = new Timer();
	
	private static ThreadPool threadPool = new ThreadPool(); // singleton
	
	public static ThreadPool getInstance() {
		return threadPool;
	}
	
	private ThreadPool(){
		freeThreads = new ArrayList<>();
	} // keep track of which workers are free

	public void setup(int numThreads, int batchSize, int batchTime){
        this.numThreads = numThreads;
        this.batchSize = batchSize;
        this.batchTime = batchTime;
        //create new workers and start them
        workers = new Worker[numThreads];
        for (int i = 0; i < numThreads; i++) {
            freeThreads.add(i);
            workers[i] = new Worker(i);
            workers[i].start();
        }
        batch = new LinkedList<>();
    }
	
	private Worker getAvailableWorker(){
		synchronized (freeThreads) {
			if (freeThreads.size() == 0) {
				try {
					freeThreads.wait(); // wait for available worker if none are free.
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return workers[freeThreads.remove(0)]; // grab next free worker
		}
		
	}
	
	protected void addAvailableWorker(int id){
		synchronized (freeThreads) {
			freeThreads.add(id);
			if (freeThreads.size() == 1) {
				freeThreads.notify(); // notify worker is available if a thread is waiting for one.
			}
		}
	}
	
	public synchronized void addTask(Task t){ // add task to threadpool
		batch.addLast(t);
		if (batch.size() == 1) {
			batchTimer.schedule(new BatchTimeoutTask(), batchTime); // start batch timer if first task in batch
		}
		if (batch.size() == batchSize) { // start next batch if batch is full
			startNextBatch();
		}
	}
	
	private synchronized void startNextBatch() { // sends batch to next available worker
		if(batch.size()>0) {
			batchTimer.purge(); //end batch time
			Worker w = getAvailableWorker();
			w.assign(batch);
			batch = new LinkedList<>();
		}
	}
	
}