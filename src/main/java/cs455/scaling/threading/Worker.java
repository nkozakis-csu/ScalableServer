package cs455.scaling.threading;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.lang.reflect.Method;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.function.Function;

public class Worker extends Thread{
	
	public boolean terminate = false;
	public volatile LinkedList<Task> batch;
	public int id;
	
	public Worker(int id){
		this.id = id;
	}
	
	public void run() {
		while(!terminate){
			if (batch != null){
				//run all tasks in batch
				for(Task t : batch){
					try {
						t.run();
					} catch (IOException | NoSuchAlgorithmException e) {
						e.printStackTrace();
					}
					
				}
				batch = null;
				ThreadPool.getInstance().addAvailableWorker(this.id); //re add self to threadpool's available worker list
			}else{
				synchronized (this){
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public synchronized void assign(LinkedList<Task> batch){ // assign task a new batch
		this.batch = batch;
		notify();
	}
}
