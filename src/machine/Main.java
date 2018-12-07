package machine;

public class Main {

	public static void main(String[] args) {
		
		Machine m = MachineLoader.loadMachine("machine.txt");
		
		State.setAlias(true);
		
		
		
		System.out.println("---------NFA---------");
		System.out.println(m);
		System.out.println("---------DFA---------");
		System.out.println(m.toDFA());
		System.out.println("---------NFA---------");
		System.out.println(m);
		System.out.println("---------Minimized---------");

		System.out.println("---------Brzozowski---------");
		System.out.println(m.minimize(Machine.Minimizer.Brzozowski));
	
	}

}
