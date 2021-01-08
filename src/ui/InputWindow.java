package ui;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import automata.Operation;

import javax.swing.GroupLayout.Alignment;

public class InputWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField inputRequired;
	private JButton addButton, cancelButton;
	
	private JLabel pdaEdit;
	private JLabel stackLabel, operationLabel, pushLabel;
	private JTextField onStack, pushOntoStack;
	private JComboBox<String> operationSelector;
	
	private String input;
	private int operation;
	private String sOnStack;
	private String sPushOntoStack;

	public InputWindow(boolean enablePDAEditing) {
		inputRequired = new JTextField("");
		inputRequired.setMaximumSize(new Dimension(20000, 25));
		
		stackLabel = new JLabel("On stack:");
		stackLabel.setMaximumSize(new Dimension(10000, 25));
		operationLabel = new JLabel("Operation:");
		operationLabel.setMaximumSize(new Dimension(10000, 25));
		pushLabel = new JLabel("Push:");
		pushLabel.setMaximumSize(new Dimension(10000, 25));
		
		onStack = new JTextField("");

		pushOntoStack = new JTextField("");
		
		operationSelector = new JComboBox<>();
		operationSelector.addItem("Ignore Stack");
		operationSelector.addItem("NOP");
		operationSelector.addItem("PUSH");
		operationSelector.addItem("POP");
		operationSelector.addActionListener(ae -> {
			pushLabel.setEnabled(true);
			pushOntoStack.setEnabled(true);
			stackLabel.setEnabled(true);
			onStack.setEnabled(true);
			switch ((String) operationSelector.getSelectedItem()) {
			case "Ignore Stack":
				pushLabel.setEnabled(false);
				pushOntoStack.setEnabled(false);
				stackLabel.setEnabled(false);
				onStack.setEnabled(false);
				break;
			case "NOP":
				pushLabel.setEnabled(false);
				pushOntoStack.setEnabled(false);
				break;
			case "PUSH":
				break;
			case "POP":
				pushLabel.setEnabled(false);
				pushOntoStack.setEnabled(false);
				break;
			}
		});
		operationSelector.setSelectedIndex(0);
		
		pdaEdit = new JLabel();
		pdaEdit.setPreferredSize(new Dimension(10000, 50));
		pdaEdit.setMaximumSize(new Dimension(10000, 50));
		if (!enablePDAEditing)
			pdaEdit.setMaximumSize(new Dimension(10000, 0));
			
		
		GridLayout gl2 = new GridLayout(2, 3);
		gl2.setHgap(8);
		gl2.setVgap(2);
		pdaEdit.setLayout(gl2);
		
		pdaEdit.add(stackLabel);
		pdaEdit.add(operationLabel);
		pdaEdit.add(pushLabel);
		pdaEdit.add(onStack);
		pdaEdit.add(operationSelector);
		pdaEdit.add(pushOntoStack);
		
		addButton = new JButton("Add");
		addButton.addActionListener(ae -> {
			input = inputRequired.getText();
			operation = operationSelector.getSelectedIndex();
			sOnStack = onStack.getText();
			sPushOntoStack = onStack.getText();
			this.dispose();
		});
		
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(ae -> this.dispose());

		GroupLayout gl = new GroupLayout(this.getContentPane());
		gl.setAutoCreateContainerGaps(true);
		gl.setAutoCreateGaps(true);

		gl.setVerticalGroup(gl.createSequentialGroup()
				.addComponent(inputRequired)
				.addComponent(pdaEdit)
				.addGroup(gl.createParallelGroup(Alignment.TRAILING)
						.addComponent(cancelButton)
						.addComponent(addButton)));
		
		gl.setHorizontalGroup(gl.createParallelGroup()
				.addComponent(inputRequired)
				.addComponent(pdaEdit)
				.addGroup(gl.createSequentialGroup()
						.addGap(0, 10000, 10000)
						.addComponent(true, cancelButton)
						.addComponent(addButton)));

		this.setLayout(gl);

		this.add(inputRequired);
		this.add(addButton);

		this.setTitle("Add connection");
		this.pack();
		this.setSize(500, this.getHeight());
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	public static void addInput(Window w, boolean allowsPDAOptions) {
		InputWindow iw = new InputWindow(allowsPDAOptions);

		Runnable r = new Runnable() {
			@Override
			public synchronized void run() {
				
				while (true) {
					if (iw.input != null) {
						System.out.printf("input: %s, '%s' on Stack, %d %s", iw.input, iw.sOnStack, iw.operation, iw.sPushOntoStack);
						Operation o = new Operation(-1, iw.sOnStack, iw.operation, iw.sPushOntoStack);
						o.setInputReq(iw.input);
						add(o, w);
						return;
					}
					if (!iw.isVisible())
						return;

					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		
		Thread t = new Thread(r);
		t.start();
	}
	
	public static void add(Operation op, Window w) {
		if (op == null)
			return;
		int fromNode = w.getEditing();

		w.executeOnNextSelect(ae2 -> {

			if (w.isPDAEditingEnabled()) {
				op.setToNode(ae2.getID());
				addConnection(w, fromNode, op.getInputReq(), op);
			} else {
				for (char c : op.getInputReq().toCharArray()) {

					addConnection(w, fromNode, c + "", ae2.getID());

				}
			}
		});
	}
	
	private static void addConnection(Window w, int fromNode, String input, int toNode) {
		if (toNode == -1 || fromNode == -1)
			return;

		if (w.getAutomata().addInput(input) || w.getAutomata().getInputNames().contains(input))
			w.getAutomata().addConnection(fromNode, w.getAutomata().getInputNames().indexOf(input), toNode);
	}
	
	private static void addConnection(Window w, int fromNode, String input, Operation op) {
		if (!op.isValid() || fromNode == -1)
			return;

		if (w.getAutomata().addInput(input) || w.getAutomata().getInputNames().contains(input))
			w.getAutomata().addConnection(fromNode, w.getAutomata().getInputNames().indexOf(input), op);
		
		op.setInputReq(null);
	}
}
