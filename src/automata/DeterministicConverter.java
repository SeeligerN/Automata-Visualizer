package automata;

import java.util.ArrayList;
import java.util.List;

public class DeterministicConverter {

	public static Automata convertNEAtoDEA(Automata n) {
		for (String s : n.getNodeNames())
			if (s.contains("|")) {
				System.out.println("ERROR: Node names must not include '|'");
				return n;
			}

		Automata d = new Automata(n.isFinishingState(0));
		d.getNodeNames().set(0, n.getNodeNames().get(0));
		d.muteAlerts(true);

		List<String> inputs = n.getInputNames();
		for (String input : inputs)
			d.addInput(input);

		for (int line = 0; line < d.getMatrix().size(); line++) {
			String[] includesNodesInN = d.getNodeNames().get(line).split("\\|");

			for (int input = 0; input < d.getInputNames().size(); input++) {

				List<String> newEntity = new ArrayList<>();

				for (String currentNodeOnD : includesNodesInN) {

					for (int nodeTo = 0; nodeTo < n.getMatrix().get(n.getNodeNR(currentNodeOnD)).get(input)
							.size(); nodeTo++) {
						String toAddToEntity = n.getNodeNames()
								.get(n.getMatrix().get(n.getNodeNR(currentNodeOnD)).get(input).get(nodeTo).getToNode());
						if (!newEntity.contains(toAddToEntity))
							newEntity.add(toAddToEntity);
					}

				}

				if (newEntity.isEmpty())
					continue;

				String entityToAdd = "";
				boolean finishingState = false;
				for (String s : newEntity) {
					entityToAdd += "|" + s;
					finishingState = n.isFinishingState(s) ? true : finishingState;
				}
				entityToAdd = entityToAdd.substring(1);
				d.addNode(entityToAdd, finishingState);
				
				d.addConnection(d.getNodeNR(d.getNodeNames().get(line)), input, d.getNodeNR(entityToAdd));
			}
		}

		d.muteAlerts(false);
		return d;
	}
}
