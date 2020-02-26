package cs455.scaling.threading;

import java.util.ArrayList;
import java.util.LinkedList;

public class ThreadPool {
	
	int numThreads;
	Worker[] workers;
	ArrayList<Integer> freeThreads;
	LinkedList<Task> jobs;
	
	private static ThreadPool threadPool = new ThreadPool(1);
	
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
		jobs.addLast(t);
	}
	
	public void startNextJob(){
		Worker w = getAvailableWorker();
		Task t = jobs.removeFirst();
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
		pool.startNextJob();
		pool.startNextJob();
		pool.startNextJob();
		pool.startNextJob();
		pool.startNextJob();
		pool.startNextJob();
		pool.startNextJob();
		pool.startNextJob();
		pool.startNextJob();
		System.out.println("moved on");
	}
}
