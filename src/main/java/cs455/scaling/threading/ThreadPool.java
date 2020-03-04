package cs455.scaling.threading;

import sun.jvm.hotspot.debugger.cdbg.EnumType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class ThreadPool {

	public class BatchTimeoutTask extends TimerTask {

		@Override
		public void run() {
			ThreadPool.getInstance().startNextBatch();
		}
	}


	private int numThreads, batchTime, batchSize;
	Worker[] workers;
	ArrayList<Integer> freeThreads;
	LinkedList<Task> batch;
	Timer batchTimer = new Timer();
	
	private static ThreadPool threadPool = new ThreadPool();
	
	public static ThreadPool getInstance() {
		return threadPool;
	}
	
	private ThreadPool(){
	}

	public void setup(int numThreads, int batchSize, int batchTime){
        this.numThreads = numThreads;
        this.batchSize = batchSize;
        this.batchTime = batchTime;
        workers = new Worker[numThreads];
        freeThreads = new ArrayList<>();
        for (int i = 0; i < numThreads; i++) {
            freeThreads.add(i);
            workers[i] = new Worker(i);
            workers[i].start();
        }
        batch = new LinkedList<>();
    }
	
	public synchronized Worker getAvailableWorker(){
		if(freeThreads.size()==0) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return workers[freeThreads.remove(0)];
		
	}
	
	public synchronized void addAvailableWorker(int id){
		freeThreads.add(new Integer(id));
		notify();
	}
	
	public void addTask(Task t){
		synchronized (batch) {
			batch.addLast(t);
			if (batch.size() == 1) {
				batch.notify();
				batchTimer.schedule(new BatchTimeoutTask(), batchTime);
			}
			if (batch.size() == batchSize) {
				startNextBatch();
			}
		}
	}
	
	public void startNextBatch(){
		synchronized (batch){
			batchTimer.purge();
			Worker w = getAvailableWorker();
			w.assign(getBatch());
			batch = new LinkedList<>();
		}
	}

	public LinkedList<Task> getBatch(){
		if(batch.size()==0)
			try {
				batch.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		return batch;
	}

	public static void main(String[] args) {
		ThreadPool pool = getInstance();
		Task t = new Task(Task::printTime);
		pool.addTask(t);
		pool.addTask(t);
		pool.addTask(t);
		pool.addTask(t);
		pool.addTask(t);
		pool.addTask(t);
		pool.addTask(t);
		pool.addTask(t);
		pool.addTask(t);
		pool.addTask(t);
		pool.addTask(t);
		pool.addTask(t);
		pool.addTask(t);
	}
}