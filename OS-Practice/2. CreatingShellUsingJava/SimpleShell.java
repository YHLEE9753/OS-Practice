import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimpleShell {
	// absolutePath
	static String absolutePath = "";
	
	// historycheck = whether command is ! or 1!
	// historycode = previous command in history by using !, !!
	static List<String> history;
	static boolean historycheck = true;
	static String historycode;

	
	
	// cd function
	static String cd_Function(List<String> list, String nowPath) {
		// changing path is empty now.
		String path = "";
		
		
		// 1. cd - absolutePath
		if(list.size() == 1) {
			String p = "/home"+File.separator+absolutePath;
			return p;
		}
		
		// split input directory by '/'
		String[] newarray = list.get(1).split("/");
		List<String> newlist = new ArrayList<String>(Arrays.asList(newarray));
				
		// 2. cd /home... - absolute path
		if (list.get(1).charAt(0) == '/' && list.size()==2) {
			for (int i = 1; i < newlist.size(); i++) {
				path = path + File.separator + newlist.get(i);
			}
		}
		// 3. cd .. (move to upper directory)
		else if (list.get(1).equals("..") && list.size() == 2) {
			// make new list of nowPath
			String[] upparr = nowPath.split("/");
			List<String> uplist = new ArrayList<String>(Arrays.asList(upparr));
			for (int i = 1; i < uplist.size() - 1; i++) {
				path = path + File.separator + uplist.get(i);
			}
		}
		// 4. cd ./user --- (. is current directiory)
		else if(list.get(1).charAt(0) == '.' && list.size() == 2) {
			// make new list of nowPath
			String[] upparr = nowPath.split("/");
			List<String> uplist = new ArrayList<String>(Arrays.asList(upparr));
			for(int i = 1;i<uplist.size();i++) {
				path = path + File.separator + uplist.get(i);
			}
			for(int i = 1;i<newlist.size();i++) {
				path = path + File.separator + newlist.get(i);
			}
		}
		// 5. cd user/ - relativePath
		else if(list.size()==2){
			path = nowPath;
			for (int i = 0; i < newlist.size(); i++) {
				path = path + File.separator + newlist.get(i);
			}
		}else {
			
		}
		
		
		// to use exists function, use File 
		File file = new File(path);
		// if path is valid, return changed path
		if (file.exists()) {
			return path;
		} else {
			// if path is not valid, return origianl path
			System.out.println("Directory is not valid");
			return nowPath;
		}
	}
	
	
	
	
	// history function 
	static void history_function(List<String> list, String nowPath) {
		// to check 56 is Integer or not in command 'history 56', make boolean b.
		boolean b = false;
		try {
			b = Integer.valueOf(list.get(1)) instanceof Integer;
		}catch(Exception e) {
			
		}
		
		// 1. history
		if(list.size()==1) {
			for (int i = 0; i < history.size(); i++) {
				System.out.println(i + " " + history.get(i));
			}
		}
		// 2. history <Integer>
		else if(list.size()==2 && b == true) {
			int n = Integer.valueOf(list.get(1));
			// to handle history size is smaller than integer
			if(history.size()-n<=0) {
				for(int i = 0;i<history.size();i++) {
					System.out.println(i + " " + history.get(i));
				}
			}else {
				for(int i = history.size()-n;i<history.size();i++) {
					System.out.println(i + " " + history.get(i));
				}
			}
		}
		// 3. history !!
		else if(list.get(1).equals("!!") && list.size()==2) {
			if(history.size()==1) {
				// if it is first command, print comment
				System.out.println("There is no previous command");
			}
			// previous code should be executed without print jsh>
			String s = history.get(history.size() - 2);
			historycode = s;
			history.set(history.size()-1, s);
			historycheck = false;
		}
		// 4. history !<number>
		else if(list.get(1).charAt(0) == '!' && list.size()==2) {
			try {
				int n = Integer.valueOf(list.get(1).substring(1));
				// previous code should be executed without print jsh>
				if (n >= 0 && n < history.size()) {
					String s = history.get(n);
					historycode = s;
					history.set(history.size()-1, s);
					historycheck = false;
				} else {
					// if integer is not valid, print comment
					System.out.println("Integer number is not valid");
				}
			// if type of n is not int(ex) history !23s34)
			}catch(Exception e) {
				System.out.println("command is not valid");
			}
			
		}
		// if command is not valid.
		else {
			System.out.println("command is not valid");
		}
	}

	
	
	
	public static void main(String[] args) throws IOException {
		String commandLine;
		BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
		// path using getProperty
		String path = System.getProperty("user.dir");
		absolutePath = System.getProperty("user.name");
		// history list to get command 
		history = new ArrayList();

		while (true) {
			// read what the user entered
			// if historycheck is true, get command and put into history
			if (historycheck) {
				System.out.print("jsh> ");
				commandLine = console.readLine();
				history.add(commandLine);
			// if historycheck is false, reason is ! and !! command
			} else {
				commandLine = historycode;
				historycheck = true;
			}

			//ArrayList to get commandline
			String[] array = commandLine.split(" ");
			List<String> list = new ArrayList<String>(Arrays.asList(array));

			//////////////////////////////////////////////////////////////////
			// handle the command that not ls, ps and cat 
			
			// 0. cat - if file is not valid 
			if(list.get(0).equals("cat")) {
				if(!new File(list.get(1)).isFile()) {
					System.out.println("File is not valid");
				}
			}
			// 1. nothing input
			else if (commandLine.equals(""))
				continue;
			// 2. exit, quit
			else if (commandLine.equals("exit") || commandLine.equals("quit")) {
				System.out.println("Goodbye");
				break;
			}
			// 3. cd
			else if (list.get(0).equals("cd")) {
				// cd_Function execute
				String newpath = cd_Function(list, path);
				// path is updated
				path = newpath;
				continue;
			}
			// 4. history
			else if (list.get(0).equals("history")) {
				// history function execute
				history_function(list,path);
				continue;
			}
			
			//////////////////////////////////////////////////////////////////
			// create ProcessBuilder object
			try {
				ProcessBuilder pb = new ProcessBuilder(list);
				pb.directory(new File(path));
				Process process = pb.start();
				// create BufferedReader to read the output string of the process builder
				InputStream is = process.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				// read the output of the process
				String line;
				while ((line = br.readLine()) != null) {
					System.out.println(line);
				}
				// buffer close
				br.close();
			}catch(Exception e){
				// if command is not valid, print comment.
				// to put wrong command into history.
				System.out.println("command is not valid");
				continue;
			}
			
		}
		// buffer close and exit system
		console.close();
		System.exit(0);
	}
}
