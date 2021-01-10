package automata;

import java.util.ArrayList;
import java.util.List;

import ui.Window;

public class Automata {

	private boolean muteAlerts;

	private List<Boolean> finishingStates;
	private List<String> nodeNames;
	private List<String> inputNames;

	// (node) (input) (list of possible Operations)
	private List<List<List<Operation>>> matrix;

	public Automata(boolean finishByDefault) {
		muteAlerts = false;

		finishingStates = new ArrayList<>();
		nodeNames = new ArrayList<>();
		inputNames = new ArrayList<>();

		matrix = new ArrayList<>();

		addNode("q0", finishByDefault);
	}
	
	public Automata() {
		this(false);
	}

	public boolean addNode(String name) {
		return addNode(name, false);
	}

	public boolean addNode(String name, boolean finishingState) {
		if (nodeNames.contains(name)) {
			printWarnings("Matrix already contains node: " + name);
			return false;
		}

		finishingStates.add(finishingState);
		nodeNames.add(name);

		List<List<Operation>> node = new ArrayList<List<Operation>>();
		for (int i = 0; i < inputNames.size(); i++)
			node.add(new ArrayList<>());

		matrix.add(node);
		return true;
	}

	public void removeNode(String node) {
		removeNode(getNodeNames().indexOf(node));
	}

	public void removeNode(int nodeNr) {

		System.out.println("removing " + nodeNr);

		if (nodeNr == 0)
			return;

		for (int fromNode = 0; fromNode < nodeNames.size(); fromNode++) {

			if (fromNode == nodeNr)
				continue;

			for (int input = 0; input < inputNames.size(); input++) {

				for (int to = 0; to < matrix.get(fromNode).get(input).size(); to++) {

					if (matrix.get(fromNode).get(input).get(to).getToNode() == nodeNr) {
						matrix.get(fromNode).get(input).remove(to);
						to--;
						continue;
					}

					if (matrix.get(fromNode).get(input).get(to).getToNode() > nodeNr)
						matrix.get(fromNode).get(input).get(to).setToNode(matrix.get(fromNode).get(input).get(to).getToNode() - 1);

				}

			}

		}

		matrix.remove(nodeNr);
		nodeNames.remove(nodeNr);

	}

	public boolean addInput(String input) {
		if (inputNames.contains(input)) {
			printWarnings("Matrix already contains input: " + input + "!");
			return false;
		}
		if (input.length() != 1) {
			printWarnings("You cannot add input with more than one char!");
			return false;
		}
		if ("".contains(input)) {
			printWarnings("You cannot add '" + input + "' as an input!");
			return false;
		}

		inputNames.add(input);

		for (List<List<Operation>> node : matrix) {
			node.add(new ArrayList<Operation>());
		}

		return true;
	}

	public void addConnection(String from, String input, String to) {
		addConnection(nodeNames.indexOf(from), inputNames.indexOf(input), nodeNames.indexOf(to));
	}
	
	public void addConnection(int from, int input, int to) {
		addConnection(from, input, new Operation(to));
	}

	public void addConnection(int from, int input, Operation o) {
		if (from == -1 || input == -1 || !o.isValid()) {
			printWarnings("couldn't add connection " + from + " -" + input + "-> " + o);
			return;
		}

		if (matrix.get(from).get(input).contains(o)) {
			printWarnings("connection '" + nodeNames.get(from) + "'(" + from + ") -'" + inputNames.get(input) + "'("
					+ input + ")-> '" + nodeNames.get(o.getToNode()) + "'(" + o.getToNode() + ") already added");
			return;
		}
		matrix.get(from).get(input).add(o);
	}
	
	public boolean removeConnections(int from , int to) {
		if (from == -1 || to == -1) {
			printWarnings("couldn't remove connections " + from + " --> " + to);
			return false;
		}
		
		boolean removedSomething = false;
		for (List<Operation> inputs : matrix.get(from))
			removedSomething = inputs.remove(new Operation(to)) ? true : removedSomething;
		
		return removedSomething;
	}

	public void printWarnings(String message) {
		if (!muteAlerts)
			System.out.println("WARNING: " + message);
	}

	public void muteAlerts(boolean mute) {
		this.muteAlerts = mute;
	}

	public void renameAll(String prefix) {
		for (int name = 0; name < nodeNames.size(); name++)
			nodeNames.set(name, prefix + name);
	}
	
	public boolean isPDA() {
		for (List<List<Operation>> node : matrix)
			for (List<Operation> input : node)
				for (Operation o : input)
					if (o.getOperation() != Operation.IGNORE_STACK)
						return true;
		
		return false;
	}

	public boolean isDeterministic() {

		for (List<List<Operation>> node : matrix)
			for (List<Operation> input : node)
				if (input.size() > 1)
					// returning false if any input qualifies more than one node as the successor
					return false;

		return true;
	}

	public String getFinishingStates(String input) {
		Automata a = isDeterministic() ? this : DeterministicConverter.convertNEAtoDEA(this);
		if (!a.isDeterministic())
			return "";

		int currentNode = 0;

		CHAR: for (char c : input.toCharArray()) {

			for (int onPossibleInput = 0; onPossibleInput < a.getInputNames().size(); onPossibleInput++) {

				if (a.getMatrix().get(currentNode).get(onPossibleInput).size() == 1)
					if (a.getInputNames().get(onPossibleInput).equals("" + c)) {
						currentNode = a.getMatrix().get(currentNode).get(onPossibleInput).get(0).getToNode();
						continue CHAR;
					}
			}

			// no possible links
			return a.getNodeNames().get(currentNode);
		}

		return a.getNodeNames().get(currentNode);
	}

	public boolean accepts(String input) {
		Automata a = isDeterministic() ? this : DeterministicConverter.convertNEAtoDEA(this);
		if (!a.isDeterministic())
			return false;

		int currentNode = 0;

		CHAR: for (char c : input.toCharArray()) {

			for (int onPossibleInput = 0; onPossibleInput < a.getInputNames().size(); onPossibleInput++) {

				if (a.getMatrix().get(currentNode).get(onPossibleInput).size() == 1)
					if (a.getInputNames().get(onPossibleInput).equals("" + c)) {
						currentNode = a.getMatrix().get(currentNode).get(onPossibleInput).get(0).getToNode();
						continue CHAR;
					}
			}

			// no possible links
			return false;
		}

		for (String s : a.getNodeNames().get(currentNode).split("\\|"))
			if (isFinishingState(s))
				return true;

		return false;
	}

	public Automata copy() {

		Automata a = new Automata();

		for (String input : inputNames)
			a.addInput(input);

		for (int i = 0; i < matrix.size(); i++) {
			List<List<Operation>> node = matrix.get(i);
			a.addNode(nodeNames.get(i), finishingStates.get(i));

			for (int input = 0; input < inputNames.size(); input++)
				for (Operation op : node.get(input))
					a.addConnection(i, input, op.getToNode());
		}

		return a;
	}

	public List<String> getNodeNames() {
		return nodeNames;
	}

	public int getNodeNR(String name) {
		return nodeNames.indexOf(name);
	}

	public boolean isFinishingState(String name) {
		return isFinishingState(nodeNames.indexOf(name));
	}

	public boolean isFinishingState(int nr) {
		if (nr == -1)
			return false;
		return finishingStates.get(nr);
	}

	public void setFinishing(int nr, boolean finishing) {
		if (nr < 0 || nr >= finishingStates.size())
			return;

		finishingStates.set(nr, finishing);
	}

	public List<String> getInputNames() {
		return inputNames;
	}

	public List<List<List<Operation>>> getMatrix() {
		return matrix;
	}

	public String print() {
		String output = "";
		
		output += String.format("+--------", "");
		for (int i = 0; i < inputNames.size(); i++)
			output += String.format("+----------------", "");
		output += "+\n";

		output += String.format("|%-8s", isDeterministic() ? "DFA" : "NFA");
		for (String input : inputNames) {
			output += String.format("| %-15s", input);
		}
		output += "|\n";

		output += String.format("+--------", "");
		for (int i = 0; i < inputNames.size(); i++)
			output += String.format("+----------------", "");
		output += "+\n";

		for (int n = 0; n < matrix.size(); n++) {
			List<List<Operation>> node = matrix.get(n);
			output += String.format("|%-8s", (finishingStates.get(n) ? "ACC " : "") + nodeNames.get(n));

			for (List<Operation> input : node) {

				String entry = "";
				if (input.isEmpty()) {
					entry = "---";
				} else if (input.size() == 1) {
					entry = nodeNames.get(input.get(0).getToNode());
				} else {
					entry += "{";
					for (int i = 0; i < input.size() - 1; i++) {
						entry += nodeNames.get(input.get(i).getToNode()) + ", ";
					}
					entry += nodeNames.get(input.get(input.size() - 1).getToNode()) + "}";
				}

				output += String.format("| %-15s", entry);
			}
			output += "|\n";
		}

		output += String.format("+--------", "");
		for (int i = 0; i < inputNames.size(); i++)
			output += String.format("+----------------", "");
		output += String.format("+");
		
		return output;
	}

	public static void main(String[] args) {
		Automata a = new Automata();

		a.addNode("q1");
		a.addNode("q2", true);

		a.addInput("1");
		a.addInput("2");
		a.addInput("3");

		a.addConnection("q0", "1", "q0");
		a.addConnection("q0", "2", "q0");
		a.addConnection("q0", "2", "q1");
		a.addConnection("q1", "2", "q2");
		a.addConnection("q2", "2", "q2");
		a.addConnection("q2", "3", "q2");

		System.out.println(a.print());

		System.out.println();

		Automata d = DeterministicConverter.convertNEAtoDEA(a);

		d.renameAll("S");

		System.out.println(d.print());

		new Window(a);
	}
}