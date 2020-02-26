package cs455.scaling.threading;

import java.util.ArrayList;
import java.util.LinkedList;

public class ThreadPool {
	
	int numThreads;
	Worker[] workers;
	ArrayList<Integer> freeThreads;
	final LinkedList<Task> jobs;
	
	private static ThreadPool threadPool = new ThreadPool(2);
	
	public static ThreadPool getInstance() {
		return threadPool;
	}
	
	private ThreadPool(int numThreads){
		this.numThreads = numThreads;
		workers = new Worker[numThreads];
		freeThreads = new ArrayList<>();
		for (int i = 0; i < numThreads; i++) {
			freeThreads.add(i);
			workers[i] = new Worker(i);
			workers[i].start();
		}
		jobs = new LinkedList<>();
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
		synchronized (jobs) {
			jobs.addLast(t);
			jobs.notify();
		}
	}
	
	public Task getNextTask(){
		synchronized (jobs){
			if(jobs.size()==0)
				try {
					jobs.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			return jobs.removeFirst();
		}
	}
	
	public void startNextJob(){
		Worker w = getAvailableWorker();
		Task t = getNextTask();
		w.assign(t);
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
		while(true) {
			System.out.println("Starting task");
			pool.startNextJob();
		}
	}
}
