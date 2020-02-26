package cs455.scaling.threading;

public class Task {
	public Runnable runMethod;
	
	public Task(Runnable runMethod){
		this.runMethod = runMethod;
	}
	
	public static void printTime(){
		System.out.println(System.currentTimeMillis());
	}
	

	public void run(){
		this.runMethod.run();
	}

}
