package machine;

import java.util.HashSet;

public class State 
{

	String state;
	
	private static boolean useAlias = false;
	
	public State(String state) 
	{
		this.state = state;
		
	}
	
	public static void setAlias(boolean setAlias) 
	{
		useAlias = setAlias;
	}
	
	
	@Override
	public String toString() {
		if (useAlias)
			return StateAlias.instance().getStateAlias(this);
		else
			return state;
	}
	
	@Override
	public boolean equals(Object obj) {
		return state.equals(((State)obj).state);
	}
	
	@Override
	public int hashCode() {
		return state.hashCode();
	}
	
}
