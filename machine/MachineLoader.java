package machine;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;

public class MachineLoader {

	static HashSet<State> states;
	static HashSet<State> ends;
	static State start;
	static HashSet<String> alphabet;
	static String emptyTransition;
	
	public static Machine loadMachine(String filepath) 
	{
		
		states = new HashSet<State>();
		ends = new HashSet<State>();
		alphabet = new HashSet<String>();
		emptyTransition = null;
		start = null;
		
		Scanner scanner;
		try {
			Machine machine = new Machine();
			scanner = new Scanner(new File(filepath));
		
		
			while(scanner.hasNextLine()) 
			{
				String line = scanner.nextLine();
				
				//Split off the comment section of a line
				line = line.split("//", 2)[0].trim();
				
				
				//Grab info from text
				if(line.startsWith("states")) 
				{
					line = trimLine(line);
					String[] tokens = line.split(",");
					for(String s : tokens) 
					{
						states.add(new State(s));
					}
				}
				else if(line.startsWith("finalStates")) 
				{
					line = trimLine(line);
					String[] tokens = line.split(",");
					for(String s : tokens) 
					{
						if(!(getState(s)==null)) 
						{
							ends.add(getState(s));
						}
						
					}
					
				}
				else if(line.startsWith("startState")) 
				{
					line = trimLine(line);
					start = new State(line);
				}
				else if(line.startsWith("alphabet")) 
				{
					line = trimLine(line);
					String[] tokens = line.split(",");
					for(String s : tokens) 
					{
						if(emptyTransition == null) {emptyTransition = s;}
						alphabet.add(s);
					}
					
				}
				else if(line.startsWith("totalTrans")) 
				{
					
					//Initialize the Machine
					machine.setAlphabet(alphabet);
					machine.setStates(states);
					machine.setEnds(ends);
					machine.setStart(start);
					machine.setEmptyTransition(emptyTransition);
					
					
					int numT = Integer.parseInt(line.split("=")[1]);
					
					for(int i = 0; i < numT; i++) 
					{
						line = scanner.nextLine();
						line = line.split("//")[0].trim();
						
						String[] tokens = line.split(",");
						State a = getState(tokens[0]);
						State b = getState(tokens[2]);
						
						machine.addTransition(a, tokens[1], b);
						
						
					}
				}
				
				
			}
		
		
		
		
		
			scanner.close();
			return machine;
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
			return null;
		}
	}
	
	
	private static String trimLine(String line) 
	{
		String toReturn = line.split("[{]")[1];
		toReturn = toReturn.replaceAll("[}]", "");
		return toReturn;
		
	}
	
	private static State getState(String s) 
	{
		State stateS = new State(s);
		
		Iterator<State> it = states.iterator();
	     while(it.hasNext()){
	        State temp = it.next();
	        if(temp.equals(stateS)) 
	        {
	        	return temp;
	        }
	     }
	     
	     return null;
	}
}
