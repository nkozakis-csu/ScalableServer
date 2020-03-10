package cs455.scaling.tasks;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class Hashing {
	
	public static String SHA1FromBytes(byte[] data) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA1");
		byte[] hash  = digest.digest(data);
		BigInteger hashInt = new BigInteger(1, hash);
		String ret = hashInt.toString(16);
		for (int i = 0; i < 40-ret.length(); i++) {
			ret = '0'+ret;
		}
		return ret;
	}

}
