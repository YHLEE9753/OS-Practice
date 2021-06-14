// This implementation is for Lab07 in the Operating System courses in SeoulTech
// The original version of this implementation came from UGA

import java.io.*;
import java.util.*;

public class AddressTranslator {
	public static int al;

	public static void main(String[] args) {
		// String inputFile = args[0];
		String inputFile = "InputFile.txt";

		/**
		 * variable of logical address
		 */
		int addr;

		/**
		 * variable of page number
		 */
		int p_num;

		/**
		 * variable of offset
		 */
		int offset;

		/**
		 * variable of frame number
		 */
		int f_num;

		/**
		 * variable of value stored in address
		 */
		int value;

		/**
		 * variable of physics address
		 */
		int phy_addr;

		/**
		 * variable of count of tlb miss
		 */
		int tlb_miss = 0;

		/**
		 * variable of count of page fault
		 */
		int page_fault = 0;

		Scanner scc = new Scanner(System.in);
		System.out.println("Select Algotithm : 1)FIFO 2)LRU (only write 1,2)");
		System.out.print(">>");
		while(true) {
			al = scc.nextInt();
			if(al==1 || al==2) {
				break;
			}
			System.out.print(">>");
		}
		
		
		System.out.println("Write number of Physical Memory size : 1)128 2)set yourself (only write 1,2)");
		System.out.print(">>");
		
		int size = 0;
		int choose;
		while(true) {
			choose = scc.nextInt();
			if(choose==1 || choose==2) {
				break;
			}
			System.out.print(">>");
		}
		
		if(choose==1) {
			size = 128;
		}else if(choose==2) {
			System.out.println("Write number of Physical Memory size :  (number is only between 1 to 256)");
			System.out.print(">>");
			int newsize;
			while(true) {
				newsize = scc.nextInt();
				if(newsize>=1 && newsize<=256) {
					size = newsize;
					break;
				}
				System.out.print(">>");
			}
		}
		
		

		try {
			Scanner sc = new Scanner(new File(inputFile));

			TLB tlb = new TLB();
			PageTable pt = new PageTable();
			PhysicalMemory pm = new PhysicalMemory(size);
			BackStore bs = new BackStore();
			int all_case = 0;
			while (sc.hasNextInt()) {
				all_case++;
				addr = sc.nextInt();
				// 2^16 = 4^8 = 16^4
				// mask the high 16bit
				addr = addr % 65536;
				offset = addr % 256;
				p_num = addr / 256;

				f_num = -1;
        		f_num = tlb.get(p_num);
        		/////////////////////////////
        		// update the LinkedList of page number
        		if(f_num!=-1) { 
					pm.findVictimPN(p_num,false);
        		}
        		/////////////////////////////
				if (f_num == -1) {
					tlb_miss++;
					// frame not in TLB
					// try page table
					f_num = pt.get(p_num);
					////////////////////////////////////////////
	        		// update the LinkedList of page number
					if(f_num != -1) {
						pm.findVictimPN(p_num,false);
					}
					////////////////////////////////////////////
					if (f_num == -1) {
						page_fault++;
						// frame not in page table
						// read frame from BackStore
						Frame f = new Frame(bs.getData(p_num));

						if (al == 1) {
							// FIFO
							if (pm.currentFreeFrame == pm.frames.length) {
								// When physical memory size if full(FIFO)
								// 1. select victim
								pm.pointer = pm.pointer % pm.frames.length;
								pm.currentFreeFrame--; // change 127 and 128 repeat
								f_num = pm.FIFOaddFrame(f); // start again from 0 after 128 numbers
								
								// 2. delete value related to victim in page table
								// This is FIFO, so the first page number is in page_num_arr[0]
								for(int i = 0;i<pt.table.length;i++) {
									if(pt.table[i].getFrameNumber()==f_num) {
										pt.table[i].valid = false;
										pt.table[i].frameNumber=-1;
									}
								}

								// 3. and then new value is added into page table and TLB
								pt.add(p_num, f_num);
								tlb.put(p_num, f_num);
								

							} else {
								// Original
								// add frame to PhysicalMemory
								f_num = pm.addFrame(f);
								pt.add(p_num, f_num);
								tlb.put(p_num, f_num);
							}

						} else if (al == 2) {
							// LRU
							if (pm.currentFreeFrame == pm.frames.length) {
								// When physical memory size if full(LRU)
								// 1. select victim from LinkedList of page number and update the LinkedList of page number
								int victimpage = pm.findVictimPN(p_num,true);
								// through page number, get relevant frame number if page table
								int victimframenumber = pt.table[victimpage].getFrameNumber();
								// set page table invalid
								pt.table[victimpage].valid = false;
								pt.table[victimpage].frameNumber=-1;
								
								
								pm.currentFreeFrame--; // change 127 and 128 repeat
								// If size of physical memory is too small, there can be error.
								try {
									// you change initial frame of victim frame number to new frame
									f_num = pm.LRUaddFrame(f, victimframenumber); 
								}catch(Exception e) {
									f_num = pm.addFrame(f);
								}
								// 3. and then new value is added into page table and TLB
								pt.add(p_num, f_num);
								tlb.put(p_num, f_num);
								
								
							} else {
								// Original
								// add frame to PhysicalMemory
								// update the LinkedList of page number
								pm.findVictimPN(p_num,false); 
								f_num = pm.addFrame(f);
								pt.add(p_num, f_num);
								tlb.put(p_num, f_num);
							}
							
						} 
					}
				}

				phy_addr = f_num * 256 + offset;
				value = pm.getValue(f_num, offset);

				System.out.println(
						String.format("Virtual address: %s Physical address: %s Value: %s", addr, phy_addr, value));
			}

			System.out.println(String.format("TLB miss: %s, Page Fault: %s", tlb_miss, page_fault));
			System.out.println("TLB miss rate : " + (double) tlb_miss / all_case + ", Page Fault rate : "
					+ (double) page_fault / all_case);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
}
