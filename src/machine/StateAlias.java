package machine;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class StateAlias {

	private static StateAlias list_instance = null;
	
	private static ArrayList<String> list_array;
	
	private static HashMap<State, String> list_map;
	
	private StateAlias() 
	{
		
		list_array = new ArrayList<String>();
		list_map = new HashMap<State, String>();
		
		Scanner scanner;
		try {
			scanner = new Scanner(new File("machine/aliases.txt"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		while(scanner.hasNext()) 
		{
			list_array.add(scanner.nextLine());
		}
		
		scanner.close();
	}
	
	public static StateAlias instance() 
	{
		if(list_instance == null) {list_instance = new StateAlias();}
		return list_instance;
	}
	
	public void declareNewAlias(State s) 
	{
		int index = Math.abs(s.hashCode() % list_array.size());
		String alias = list_array.get(index);
		
		while(list_map.containsValue(alias)) 
		{
			alias = list_array.get(++index % list_array.size());
		}
		
		list_map.put(s, alias);
		
	}
	
	public String getStateAlias(State s) 
	{
		if(list_map.containsKey(s)) 
		{
			return list_map.get(s);
		}
		else 
		{
			declareNewAlias(s);
			return list_map.get(s);
		}
	}
	 
	
}
