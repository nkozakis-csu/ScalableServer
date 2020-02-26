package cs455.scaling.threading;

import java.io.InterruptedIOException;
import java.lang.reflect.Method;
import java.util.function.Function;

public class Worker extends Thread{
	
	public boolean terminate = false;
	public volatile Task task;
	public int id;
	
	public Worker(int id){
		this.id = id;
	}
	
	public void run() {
		while(!terminate){
			if (task != null){
				task.run();
				task = null;
				ThreadPool.getInstance().addAvailableWorker(this.id);
			}else{
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void assign(Task t){
		task = t;
	}
}
