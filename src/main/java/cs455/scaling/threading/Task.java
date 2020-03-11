package cs455.scaling.threading;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public abstract class Task {
	
	public abstract void run() throws IOException, NoSuchAlgorithmException;
	

}
