

public class Philosopher implements Runnable {
   // static array to check chopsticks
   // If value is 1, it means it is used by thread.
   // If value is 0, it means it isn’t used
   static int[] chopsticks_arr = new int[miscsubs.NUMBER_PHILOSOPHERS];
   // static Object to control wait and notify
   static Object both = new Object();
   static Object finish = new Object();
   static boolean flag = true;
   // philosopher's id
   int id;

   // Constructor
   Philosopher(int id) {
      this.id = id;
   }

   @Override
   public void run() {
      // TODO Auto-generated method stub
      // loop until miscubs.TotalEats is under 500
      while (miscsubs.TotalEats < 500) {
         // Firstly, delay to thread
         miscsubs.RandomDelay();

         // to use wait
         synchronized (both) {
            // If right chopstick is used, philosopher should wait
            if (chopsticks_arr[id] == 1) {
               // wait until notify and right chopstick is available
               while (true) {
                  try {
                     both.wait();
                  } catch (InterruptedException e) {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
                  }
                  // if right chopstick is available, break;
                  if (chopsticks_arr[id] == 0) {
                     break;
                  }
               }
            }
            // The right chopstick is not used. philosopher can use it
            chopsticks_arr[id] = 1;
         }

         
         synchronized (both) {
            if (chopsticks_arr[(id + 1) % miscsubs.NUMBER_PHILOSOPHERS] == 1) {
               // To avoid deadlock, if you get right chopstick but you can not get left chopstick, 
               // put down right chopsticks and go back by using continue
               chopsticks_arr[id] = 0;
               continue;
            }
            // The left chopstick is not used. philosopher can use it
            chopsticks_arr[(id + 1) % miscsubs.NUMBER_PHILOSOPHERS] = 1;
         }
         
         // to use notifyAll
         synchronized (both) {
            // if miscsubs.TotalEats==miscsubs.MAX_EATS philosopher put down all chopsticks and break;
            if(miscsubs.TotalEats==miscsubs.MAX_EATS) {
               chopsticks_arr[id] = 0;
               chopsticks_arr[(id + 1) % miscsubs.NUMBER_PHILOSOPHERS] = 0;
               break;
            }
            // Now philosopher get both chopsticks
            miscsubs.StartEating(id); // start eating
            miscsubs.RandomDelay(); // delay to thread
            miscsubs.DoneEating(id); // done eating

            // philosopher put down both chopsticks
            chopsticks_arr[id] = 0;
            chopsticks_arr[(id + 1) % miscsubs.NUMBER_PHILOSOPHERS] = 0;

            // notify other thread that chopsticks can be used
            both.notifyAll();
         }
      }
      // notify main that miscsubs.TotalEats==miscsubs.MAX_EATS
      synchronized(finish) {
          finish.notifyAll();
      }
   }
}