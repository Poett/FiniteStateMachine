package machine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class TransitionMap {
	
	
	/*
	 * FromState
	 * 		symbol1 -> [State... states]
	 * 		symbol2 -> [State... states] 
	 */

	private HashSet<String> alphabet;
	private HashMap<String, HashSet<State>> transitions;
	private State fromState;
	
	public TransitionMap(HashSet<String> alphabet, State fromState) 
	{
		this.alphabet = alphabet;
		this.fromState = fromState;
		transitions = new HashMap<String, HashSet<State>>();
		
		//Initialize empty map
		for(String s : this.alphabet) 
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
	
	
	public HashMap<State, TransitionMap> reverseMap() 
	{
		HashMap<State, TransitionMap> machine_partial = new HashMap<State, TransitionMap>();
		
		//Collect all to-states for this transition map
		for(HashSet<State> set : transitions.values()) 
		{
			for(State s : set) 
			{
				machine_partial.put(s, new TransitionMap(alphabet, s));
			}
		}
		
		
		//Construct the machine partial
		for(String a : transitions.keySet()) //for each transition
		{
			for(State s : transitions.get(a)) //for each resulting state from transition
			{
				machine_partial.get(s).addTransition(a, fromState); //map resulting state towards fromState with transition (reversing)
			}
		}
		
		
		return machine_partial;
	}
	
	public void combine(TransitionMap t) 
	{
		if(!(t.fromState.equals(this.fromState))) 
		{
			return;
		}
		
		for(String a : alphabet) 
		{
			this.transitions.get(a).addAll(t.getStates(a));
		}
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
