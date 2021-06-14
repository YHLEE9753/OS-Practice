// Use java threads to simulate the Dining Philosophers Problem
// YOUR NAME HERE.  Programming assignment 2 (from ece.gatech.edu) */


import java.util.ArrayList;

class dining {
	public static void main(String args[]) {
		System.out.println("Starting the Dining Philosophers Simulation\n");
		miscsubs.InitializeChecking();
		// Your code here...
		// ArrayList to allocate thread
		ArrayList<Thread> thread_arr = new ArrayList<>();
		for (int i = 0; i < miscsubs.NUMBER_PHILOSOPHERS; i++) {
			// make new thread
			Thread t = new Thread(new Philosopher(i));
			// add thread to ArrayList
			thread_arr.add(t);
		}
		// Start thread
		for (int i = 0; i < miscsubs.NUMBER_PHILOSOPHERS; i++) {
			thread_arr.get(i).start();
		}

		// while loop to prevent miscubs.LogResults unintendedly
		while (miscsubs.TotalEats < miscsubs.MAX_EATS) {
			// it is waited until notify when miscsubs.TotalEats == miscsubs.MAX_EATS
			synchronized (Philosopher.finish) {
				try {
					Philosopher.finish.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		for (int i = 0; i < miscsubs.NUMBER_PHILOSOPHERS; i++) {
			thread_arr.get(i).interrupt();
		}
		// End of your code
		System.out.println();
		System.out.println("Simulation Ends..");
		miscsubs.LogResults();
	}

};