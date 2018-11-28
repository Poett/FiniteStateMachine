package machine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
			@Override
			public Machine minimize(Machine machine) {
				Machine huff = new Machine(machine.alphabet);

				return huff;
			}
		},
		Brzozowski {
			@Override
			public Machine minimize(Machine machine) {
				return machine.toDFA().reverse().toDFA().reverse();

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
