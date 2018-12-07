package machine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class Machine {

	/*
	 * Maping of the machine states and transitions Each State maps to a Transition
	 * Map Alphabet symbols will map to an array of states
	 */
	private HashMap<State, TransitionMap> machine;

	private HashSet<State> states; // A Set of all states in machine

	private State start; // A state used as initial states
	private HashSet<State> ends; // A Set of States used as final states

	private HashSet<String> alphabet; // A Set of Strings used as an alphabet
	private String emptyTransition; // Alphabet symbol used as empty

	/*
	 * Empty machine constructor with alphabet
	 */
	public Machine(HashSet<String> alphabet) {
		this.alphabet = alphabet;
		this.states = new HashSet<State>();
		this.ends = new HashSet<State>();
		this.start = null;
		this.emptyTransition = null;

		this.machine = new HashMap<State, TransitionMap>();
	}

	/*
	 * Empty machine constructor
	 */
	public Machine() {
		this.alphabet = null;
		this.states = new HashSet<State>();
		this.ends = new HashSet<State>();
		this.start = null;
		this.emptyTransition = null;

		this.machine = new HashMap<State, TransitionMap>();
	}

	/*
	 * Machine methods
	 */

	public HashSet<State> getStates() {
		return states;
	}

	public void setStates(HashSet<State> states) {
		this.machine = new HashMap<State, TransitionMap>();

		for (State s : states) {
			addState(s);
		}
	}

	public State getStart() {
		return start;
	}

	public void setStart(State start) {
		this.start = start;
	}

	public HashSet<State> getEnds() {
		return ends;
	}

	public void setEnds(HashSet<State> ends) {
		this.ends = ends;
	}

	public void addEnds(State s) {
		if (states.contains(s))
			this.ends.add(s);
	}

	public HashSet<String> getAlphabet() {
		return alphabet;
	}

	public void setAlphabet(HashSet<String> alphabet) {
		this.alphabet = alphabet;
	}

	public String getEmptyTransition() {
		return emptyTransition;
	}

	public void setEmptyTransition(String emptyTransition) {
		this.emptyTransition = emptyTransition;
	}

	public void addState(State s) {

		if (this.states.add(s)) // .add(s) returns true if state added
		{
			machine.put(s, new TransitionMap(this.alphabet, s)); // initialize an transition map for the state
		}
	}
	
//	public HashMap<State, TransitionMap> removeState(State s) 
//	{
//		
//		machine.remove(s);
//		states.remove(s);
//		ends.remove(s);
//		if(start.equals(s)) {start = null;}
//
//		HashMap<State, TransitionMap> removedTransitions = new HashMap<>();
//		
//		for(State s : machine.keySet()) 
//		{
//			for(String a : alphabet) 
//			{
//				
//			}
//		}
//	}

	public HashSet<State> transition(State p, String a) 
	{
		return machine.get(p).getStates(a);
	}
	
	public State transitionDFA(State p, String a) 
	{
		return machine.get(p).getStates(a).iterator().next();
	}
	
	// Add transition from one state to another state
	public void addTransition(State from_State, String transition, State to_State) {
		machine.get(from_State).addTransition(transition, to_State);
	}

	// Get Closure Set for a specific state
	public HashSet<State> getClosures(State s) {
		HashSet<State> closure = new HashSet<State>();
		closure.add(s);

		HashSet<State> temp = machine.get(s).getStates(emptyTransition);

		for (State n : temp) {
			closure.addAll(getClosures(n)); // Add all the states for the next level of empty transitions
		}

		return closure;

	}
	
	/*
	 * Right will be merged into left
	 * Left's name will change to Left|Right
	 * Any other state going to right will go to left instead
	 * Right will be removed from the machine
	 * If Right was a final or initial, left will become that as well
	 */
	public boolean merge(State left, State right) 
	{
		
		if(!(states.contains(left) && states.contains(right))) {return false;}
		
		//Remove right from machine and states
		states.remove(right);
		machine.remove(right);
		if(ends.remove(right)) {ends.add(left);}
		if(start.equals(right)) {start = left;}
		
		//Redirect all transitions to right to left
		for(State s : machine.keySet()) 
		{
			TransitionMap t = machine.get(s);
			t.convert(left, right);
		}
		
		
		return true;
	}

	public Machine reverse() {
		Machine reversedMachine = new Machine(this.alphabet);
		reversedMachine.setEmptyTransition(emptyTransition);
		reversedMachine.alphabet.add(emptyTransition);
		reversedMachine.setStates(this.states);

		// Initialize the new transition mapping hashmap
		HashMap<State, TransitionMap> reversedMachineMap = new HashMap<>();
		for (State s : this.states) {
			reversedMachineMap.put(s, new TransitionMap(this.alphabet, s));
		}

		// Create all machine partials by reversing TransitionMaps
		ArrayList<HashMap<State, TransitionMap>> machine_partials = new ArrayList<>();
		for (State s : machine.keySet()) {
			machine_partials.add(machine.get(s).reverseMap());
		}

		// For each machine partial, combine that into reversedMachineMap
		for (HashMap<State, TransitionMap> partial : machine_partials) {
			for (State s : partial.keySet()) {
				reversedMachineMap.get(s).combine(partial.get(s));
			}
		}

		// Set the new transition map for the reversed machine
		reversedMachine.machine = reversedMachineMap;

		// Set the reversed machine's start and final
		// Start
		if (this.ends.size() > 1) // Create a new Start State to connect to all ends
		{
			State newStart = new State("NEW");
			reversedMachine.addState(newStart);
			reversedMachine.setStart(newStart);

			for (State e : this.ends) {
				reversedMachine.addTransition(newStart, emptyTransition, e);
			}

		} else {
			reversedMachine.setStart((State) this.ends.toArray()[0]);
		}
		// Final
		reversedMachine.addEnds(this.start);

		return reversedMachine;

	}

	public Machine toDFA() {

		if (emptyTransition == null) {
			return this;
		}

		Machine dfa = new Machine(this.alphabet);
		dfa.alphabet.remove(emptyTransition);
		dfa.setEmptyTransition(emptyTransition);

		// Hashmap of closure sets - saves the states transitioned via empty including
		// itself
		HashMap<State, HashSet<State>> closureSets = new HashMap<State, HashSet<State>>();

		// Collect the closure sets
		for (State s : this.states) {
			closureSets.put(s, getClosures(s));
		}

		// Map out new machine with closure
		Queue<HashSet<State>> queue = new LinkedList<HashSet<State>>(); // used to queue up transitions to check

		queue.add(closureSets.get(start));

		boolean initial = true;
		HashSet<State> ends = new HashSet<State>();

		while (!queue.isEmpty()) {
			HashSet<State> stateSet = queue.poll();
			State newState = new State(stateSet.toString());
			dfa.addState(newState);

			if (initial) {
				initial = false;
				dfa.setStart(newState);
			}

			for (State s : stateSet)
				if (this.ends.contains(s))
					dfa.addEnds(newState);
			// for each state in the state set,
			for (String a : alphabet) {
				HashSet<State> transitionStateSet = new HashSet<State>();

				for (State s : stateSet) {
					HashSet<State> temp = machine.get(s).getStates(a); // Get all the states this specific state maps to
																		// with transition a

					// Look at the closureSets Map to get the closure set for State c
					for (State c : temp) {
						transitionStateSet.addAll(closureSets.get(c));
					}
				}

				if (!transitionStateSet.isEmpty()) {
					State transitionState = new State(transitionStateSet.toString());

					// if the transitionState is unchecked, add to queue
					if (!dfa.machine.containsKey(transitionState)) {
						queue.add(transitionStateSet);
					}
					dfa.machine.get(newState).addTransition(a, transitionState); // map new transition to new state
				}
			}
		}

		return dfa;
	}

	public Machine minimize(Minimizer m) {
		return m.minimize(this);
	}

	public String toString() {
		String toReturn = "";

		toReturn += "Alphabet: " + alphabet.toString() + "\n";
		if (emptyTransition != null)
			toReturn += "Empty Transition: " + emptyTransition + "\n";
		toReturn += "States: " + states.toString() + "\n";
		toReturn += "Start: " + this.start.toString() + "\n";
		toReturn += "Finals: " + this.ends.toString() + "\n\n";

		for (State s : machine.keySet()) {
			toReturn += s + "\n" + machine.get(s).toString() + "\n";
		}

		return toReturn;
	}

	public enum Minimizer {
		Huffman {
			private boolean[][] distinctTable;
			private State[] stateArray;
			
			@Override
			public Machine minimize(Machine machine) {
				Machine huff = machine.toDFA();

				stateArray = huff.states.toArray(new State[huff.states.size()]); //Bridge to index-based array
				distinctTable = new boolean[stateArray.length][stateArray.length]; //create the distinct table
				
				/*
				 * Nested loop indexes over distinction matrix
				 * 
				 * i:(0 -> second to last element)
				 * 		j:(i+1 -> last element)
				 */
				//Initial nested loop that distinguishes finals vs nonfinals
				for(int i = 0; i < stateArray.length-2; i++) 
				{
					for(int j = i+1 ; j < stateArray.length-1; j++) 
					{
						State p = stateArray[i];
						State q = stateArray[j];
						//if a is final
						if(huff.ends.contains(p))
							//and b is nonfinal
							if(!(huff.ends.contains(q)))
								distinctTable[i][j] = true;
						//or if a is nonfinal
						else
							//and b is final
							if(huff.ends.contains(q))
								distinctTable[i][j] = true;
					}
				}
				
				//Loops through each pairs of state to check their distinction
				boolean checkDistinguished = true; //Switch for while loop ; checks for no change
				while(checkDistinguished) 
				{
					//Default to turn off switch, if there's a change, it will switch back on
					checkDistinguished = false;
					
					//Matrix nested loop
					for(int i = 0; i < stateArray.length-2; i++) 
					{
						for(int j = i+1 ; j < stateArray.length-1; j++) 
						{
							//If distinction is empty, check it
							if(distinctTable[i][j] == false) 
							{
								//If a distinction was found, set it on the table and allow for recheck
								if(getDistinct(stateArray[i], stateArray[j], huff)) 
								{
									
									distinctTable[i][j] = true; //set distinct(i, j)
									checkDistinguished = true; //switch on
								}
							}
						}
					}
					
				}
				
				//Last nested loop to check if any states are not distinct
				//If any pairs are not distinct, merge them
				for(int i = 0; i < stateArray.length-2; i++) 
				{
					for(int j = i+1 ; j < stateArray.length-1; j++) 
					{
						//If distinction is empty, merge it
						if(distinctTable[i][j] == false) 
						{
							huff.merge(stateArray[i], stateArray[j]);
						}
					}
				}
				
				
				return huff;
			}
			
			private int getIndexOf(State s) 
			{
				int index = -1;
				
				for(int i = 0; i < stateArray.length; i++) 
				{
					if(stateArray[i].equals(s))
						index = i;
				}
				
				return index;
			}
			
			private boolean getDistinct(State p, State q, Machine machine) 
			{	
				for(String a : machine.alphabet) 
				{
					State pa = machine.transitionDFA(p, a);
					State qa = machine.transitionDFA(q, a);
					
					int pai = getIndexOf(pa);
					int qai = getIndexOf(qa);
					
					if(distinctTable[pai][qai] == true) 
					{
						return true;
					}
				}
				
				return false;
			}
		},
		
		Brzozowski {
			@Override
			public Machine minimize(Machine machine) {
				return machine.toDFA().reverse().toDFA().reverse().toDFA();

			}
		},
		Hopcroft {
			@Override
			public Machine minimize(Machine machine) {
				Machine hop = new Machine(machine.alphabet);

				return hop;
			}
		};

		public abstract Machine minimize(Machine machine);

	}
}
