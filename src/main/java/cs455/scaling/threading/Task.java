package cs455.scaling.threading;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class Task {

	Runnable runnable;
	
	public Task(){
		runnable = Task::printTime;
	}

	public Task(Runnable runnable){
		this.runnable = runnable;
	}
	
	public static void printTime(){
		System.out.println(System.currentTimeMillis());
	}

	public void run() throws IOException, NoSuchAlgorithmException {
		runnable.run();
	}

	public static void main(String[] args) {
		Runnable pt = Task::printTime;
		pt.run();
	}

}
