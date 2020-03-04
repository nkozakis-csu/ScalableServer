package cs455.scaling.threading;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.lang.reflect.Method;
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
				for(Task t : batch){
					try {
						t.run();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				}
				batch = null;
				ThreadPool.getInstance().addAvailableWorker(this.id);
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
	
	public void assign(LinkedList<Task> batch){
		this.batch = batch;
		synchronized (this) { notify(); }
	}
}
