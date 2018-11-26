package machine;

public class Main {

	public static void main(String[] args) {
		
		Machine m = MachineLoader.loadMachine("machine/machine.txt");
		
		
		System.out.println("---------NFA---------");
		System.out.println(m);
		System.out.println("---------DFA---------");
		System.out.println(m.toDFA());
		System.out.println("---------Minimized---------");
		System.out.println(m.minimize(Machine.Minimizer.Brzozowski));
	}

}
