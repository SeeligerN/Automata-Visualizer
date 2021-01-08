package ui;

import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import automata.Automata;
import automata.DeterministicConverter;

public class Window extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private AutomataDrawLabel dl;

	private JButton newAutomataButton;
	private JButton printTableButton;
	private JButton checkInputButton;
	private JButton renameAllButton;
	private JButton convertButton;
	private JButton addNodeButton;
	private JCheckBox enablePDABox;

	private NodeEditLabel el;

	private Automata a;

	public Window() {
		this(null);
	}

	public Window(Automata a) {
		if (a == null)
			a = new Automata();

		this.setTitle("Automata Editor");
		this.setSize(1000, 600);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

		dl = new AutomataDrawLabel(this);
		dl.setMaximumSize(new Dimension(10000, 10000));

		newAutomataButton = new JButton("New Automata");
		newAutomataButton.setMinimumSize(new Dimension(250, 1));
		newAutomataButton.addActionListener(ae -> setDisplay(new Automata()));

		printTableButton = new JButton("Print Table");
		printTableButton.setMinimumSize(new Dimension(250, 1));
		printTableButton.addActionListener(ae -> new TablePrinter(this.a.print()));

		checkInputButton = new JButton("Check Input");
		checkInputButton.setMinimumSize(new Dimension(250, 1));
		checkInputButton.addActionListener(ae -> new InputChecker(this));

		renameAllButton = new JButton("Rename all nodes");
		renameAllButton.setMinimumSize(new Dimension(250, 1));
		renameAllButton.addActionListener(ae -> {
			String prefix = JOptionPane.showInputDialog("What prefix is to be used?");
			if (prefix == null)
				return;
			this.a.renameAll(prefix);
			repaint();
		});

		convertButton = new JButton("Convert to DFA");
		convertButton.setMinimumSize(new Dimension(250, 1));
		convertButton.addActionListener(ae -> convertToDFA());

		addNodeButton = new JButton("Add Node");
		addNodeButton.setMinimumSize(new Dimension(250, 1));
		addNodeButton.addActionListener(ae -> addNode());

		enablePDABox = new JCheckBox("enable PDA");
		enablePDABox.setMinimumSize(new Dimension(250, 1));
		// TODO: remove as this should be temporary
//		enablePDABox.setVisible(false);

		el = new NodeEditLabel(a, this);

		GroupLayout gl = new GroupLayout(this.getContentPane());
		gl.setAutoCreateContainerGaps(true);
		gl.setAutoCreateGaps(true);

		gl.setHorizontalGroup(gl.createSequentialGroup().addComponent(dl)
				.addGroup(gl.createParallelGroup().addComponent(newAutomataButton).addComponent(printTableButton)
						.addComponent(checkInputButton).addComponent(renameAllButton).addComponent(convertButton)
						.addComponent(addNodeButton).addComponent(enablePDABox).addComponent(el)));

		gl.setVerticalGroup(gl.createParallelGroup().addComponent(dl)
				.addGroup(gl.createSequentialGroup().addComponent(newAutomataButton).addComponent(printTableButton)
						.addComponent(checkInputButton).addComponent(renameAllButton).addComponent(convertButton)
						.addComponent(addNodeButton).addComponent(enablePDABox).addComponent(el)));

		this.setLayout(gl);
		this.setVisible(true);

		setDisplay(a);
	}

	public void executeOnNextSelect(ActionListener al) {
		dl.executeOnNextSelect(al);
	}

	private void convertToDFA() {
		setDisplay(DeterministicConverter.convertNEAtoDEA(a));
	}

	public void setDisplay(Automata a) {
		this.a = a;
		setEditingNode(-1);
		repaint();
	}

	public Automata getAutomata() {
		return a;
	}

	private void addNode() {
		a.addNode("q" + a.getNodeNames().size());
		repaint();
	}

	public void setEditingNode(int node) {
		el.setNodeToEdit(node);
		dl.selectedNode = node;
	}

	public int getEditing() {
		return dl.getSelectedNode();
	}
	
	public boolean isPDAEditingEnabled() {
		return enablePDABox.isSelected();
	}
}
