import java.util.LinkedList;

/**
 * Class to emulate the physical memory
 */
public class PhysicalMemory {
	/**
	 * variable to emulate frames in memory
	 */
	Frame[] frames;
	/**
	 * we need a variable to store how many frames are used
	 */
	int currentFreeFrame;

	/**
	 * Constructor
	 */
	public PhysicalMemory(int size) {
		this.frames = new Frame[size];
		this.currentFreeFrame = 0;
	}

	/**
	 * function to add a new frame to memory
	 *
	 * @param f Frame to be added
	 * @return int the frame number just added
	 */
	public int addFrame(Frame f) {
		this.frames[this.currentFreeFrame] = new Frame(f.data);
		this.currentFreeFrame++;
		return this.currentFreeFrame - 1;
	}

	int pointer = 0;

	public int FIFOaddFrame(Frame f) {
		// FIFO
		// pointer is increasing up to 128.
		// after 128, it is set to 0 and increase again(0,1,2,...)
		this.frames[this.pointer] = new Frame(f.data);
		this.pointer++;
		this.currentFreeFrame++;
		// return frame_number
		return this.pointer - 1;
	}
	
	

	// LRU
	// LinkedList of page numbers
	LinkedList<Integer> pm_list = new LinkedList<>();
	
	// method to update LinkedList of page numbers
	// if pageReplacement is false, only update. return value is invalid.
	// if pageReplacement is true, update and then find victim using poll() method
	public int findVictimPN(int pagenumber, boolean pageReplacement) {
		// check LinkedList of page numbers whether same value is in there
		boolean flag = true;
		for (int i = 0; i < pm_list.size(); i++) {
			if (pm_list.get(i) == pagenumber) {
				pm_list.remove(i);
				pm_list.addLast(pagenumber);
				flag = false;
				break;
			}
		}
		// if there is no same value, add to list
		if (flag) {
			pm_list.add(pagenumber);
		}

		// find victim
		if(pageReplacement) {
			// LRU use victim as the oldest value is list
			int victim = pm_list.poll();
			return victim;
		}
		// return -1 is invalid.
		return -1;
	}
	
	// method to add Frame to Physical Memory
	// you got victim frame number. you can change victim to new frame
	public int LRUaddFrame(Frame f, int victim) {
		this.frames[victim] = new Frame(f.data);
		this.currentFreeFrame++;
		return victim;
	}

	/**
	 * function to get value in memory
	 *
	 * @param f_num  int frame number
	 * @param offset int offset
	 * @return int content in specified location
	 */
	public int getValue(int f_num, int offset) {
		Frame frame = this.frames[f_num];
		return frame.data[offset];
	}

}

/**
 * wrapper class to group all frame related logics
 */
class Frame {
	/**
	 * variable to store datas of this frame
	 */
	int[] data;

	/**
	 * Constructor
	 *
	 * @param d int[256] for initializing frame
	 */
	public Frame(int[] d) {
		this.data = new int[256];
		for (int i = 0; i < 256; i++) {
			this.data[i] = d[i];
		}
	}

	/**
	 * Copy Constructor
	 *
	 * @param f Frame to be copied
	 */
	public Frame(Frame f) {
		this.data = new int[256];
		System.arraycopy(f.data, 0, this.data, 0, 256);
	}
}
