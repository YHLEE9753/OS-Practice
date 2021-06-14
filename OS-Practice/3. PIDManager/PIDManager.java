
public interface PIDManager {
	// range of allowable PIDs(inclusive)
	public static final int MIN_PID = 4;
	public static final int MAX_PID = 127;
	
	// return a valid PID or -1 if
	// none are available
	
	public int getPID();
	
	// return a valid PID, possibly blocking the
	// calling process until one is available.
	
	public int getPIDWait();
	
	// Relaease the pid
	// Throw an IllegalArgunmentException if the pid
	// is outside of the range of PID values
	
	public void releasePID(int pid);
}
