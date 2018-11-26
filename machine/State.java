package machine;

import java.util.HashSet;

public class State 
{

	HashSet<String> stateSet;
	
	public State() 
	{
		stateSet = new HashSet<String>();
	}
	
	public State(String... states) 
	{
		stateSet = new HashSet<String>();
		
		for(String s : states) 
		{
			stateSet.add(s);
		}
	}
	
	public State(HashSet<State> states) 
	{
		stateSet = new HashSet<String>();
		
		for(State s : states) 
		{
			stateSet.addAll(s.getStates());
		}
	}
	
	public void addState(HashSet<String> state) 
	{
		stateSet.addAll(state);
	}
	
	public void addAll(HashSet<State> states) 
	{
		for(State s : states) 
		{
			stateSet.addAll(s.getStates());
		}
	}
	
	public void addState(String state) 
	{
		stateSet.add(state);
	}
	
	public boolean containsState(State s) 
	{
		for(String state : s.getStates()) 
		{
			if(!stateSet.contains(state)) {return false;}
		}
		
		return true;
	}
	
	
	public HashSet<String> getStates() {return stateSet;}

	
	public String toString() 
	{
		return this.stateSet.toString();
		//return StateAlias.instance().getStateAlias(this);
	}
	
	@Override
	public boolean equals(Object rhs) {
		return stateSet.equals(((State)rhs).getStates());
	}
	
	@Override
	public int hashCode() {
		return this.stateSet.hashCode();
	}
	
}
