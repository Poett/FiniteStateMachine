package machine;

import java.util.HashMap;
import java.util.HashSet;

public class TransitionMap {

	private HashMap<String, HashSet<State>> transitions;
	
	
	public TransitionMap(HashSet<String> alphabet) 
	{
		transitions = new HashMap<String, HashSet<State>>();
		
		//Initialize empty map
		for(String s : alphabet) 
		{
			transitions.put(s, new HashSet<State>());
		}
	}
	
	
	public void addTransition(String transition, State toState) 
	{
		HashSet<State> list = transitions.get(transition);
		
		if(list != null) 
		{
			list.add(toState);
		}
	}
	
	public HashSet<State> getStates(String transition)
	{
		return transitions.get(transition);
	}
	
	public String toString() 
	{
		String toReturn = "";
		
		
		for(String s : transitions.keySet()) 
		{
			
			HashSet<State> temp = transitions.get(s);
			if(!temp.isEmpty())
			toReturn += s + ": " + transitions.get(s).toString() + "\n";
		}
		
		return toReturn;
	}
	
	
	
}
