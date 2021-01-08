package ui;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import automata.Automata;
import automata.Operation;

public class NodeEditLabel extends JLabel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Window w;

	private JSeparator sep;
	private JLabel titleLabel;
	private JLabel nameLabel, finishingLabel;
	private DocumentListener docListener;
	private JTextField nameField;
	private JCheckBox finishingBox;
	private JButton addConnectionButton, removeConnectionButton, removeNodeButton;

	public NodeEditLabel(Automata a, Window w) {
		this.w = w;

		sep = new JSeparator(JSeparator.HORIZONTAL);
		sep.setMaximumSize(new Dimension(100000, 10));

		titleLabel = new JLabel("Node 'q0':");
		titleLabel.setFont(new Font("", Font.BOLD, 25));

		nameLabel = new JLabel("Name:");

		finishingLabel = new JLabel("ACC state:");

		docListener = new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				updateAutomata();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				updateAutomata();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				updateAutomata();
			}
		};

		nameField = new JTextField("q0");
		nameField.getDocument().addDocumentListener(docListener);

		finishingBox = new JCheckBox("", false);
		finishingBox.addActionListener(ae -> updateAutomata());

		addConnectionButton = new JButton("Add Connection");
		addConnectionButton.setMaximumSize(new Dimension(250, 25));
		addConnectionButton.addActionListener(ae -> {
			InputWindow.addInput(w, w.isPDAEditingEnabled());
		});

		removeConnectionButton = new JButton("Remove Connections");
		removeConnectionButton.setMaximumSize(new Dimension(250, 25));
		removeConnectionButton.addActionListener(ae -> {
			int fromNode = w.getEditing();

			w.executeOnNextSelect(ae2 -> {
				if (!w.getAutomata().removeConnections(fromNode, ae2.getID()))
					w.getAutomata().removeConnections(ae2.getID(), fromNode);
				w.repaint();
			});
		});

		removeNodeButton = new JButton("Remove Node");
		removeNodeButton.setMaximumSize(new Dimension(250, 25));
		removeNodeButton.addActionListener(ae -> {
			w.getAutomata().removeNode(w.getEditing());
			w.setEditingNode(-1);
			w.repaint();
		});

		GroupLayout gl = new GroupLayout(this);
		gl.setAutoCreateContainerGaps(true);
		gl.setAutoCreateGaps(true);

		gl.setVerticalGroup(gl.createSequentialGroup().addComponent(sep).addComponent(titleLabel)
				.addGroup(gl.createParallelGroup(Alignment.BASELINE).addComponent(nameLabel).addComponent(nameField))
				.addGroup(gl.createParallelGroup(Alignment.BASELINE).addComponent(finishingLabel)
						.addComponent(finishingBox))
				.addComponent(addConnectionButton).addComponent(removeConnectionButton).addComponent(removeNodeButton));

		gl.setHorizontalGroup(gl.createParallelGroup().addComponent(sep).addComponent(titleLabel).addGroup(gl
				.createSequentialGroup()
				.addGroup(
						gl.createParallelGroup(Alignment.TRAILING).addComponent(nameLabel).addComponent(finishingLabel))
				.addGroup(
						gl.createParallelGroup(Alignment.TRAILING).addComponent(nameField).addComponent(finishingBox)))
				.addComponent(addConnectionButton, Alignment.CENTER)
				.addComponent(removeConnectionButton, Alignment.CENTER)
				.addComponent(removeNodeButton, Alignment.CENTER));

		this.setLayout(gl);

		setNodeToEdit(w.getEditing());
	}

	public void setNodeToEdit(int node) {
		if (node < 0 || node >= w.getAutomata().getNodeNames().size()) {
			this.setMaximumSize(new Dimension(250, 0));
			this.setSize(new Dimension(0, 0));
			return;
		}

		titleLabel.setText("Node '" + w.getAutomata().getNodeNames().get(node) + "':");

		nameField.getDocument().removeDocumentListener(docListener);
		nameField.setText(w.getAutomata().getNodeNames().get(node));
		nameField.getDocument().addDocumentListener(docListener);

		finishingBox.setSelected(w.getAutomata().isFinishingState(node));

		this.setMaximumSize(new Dimension(250, 300));
		this.setSize(new Dimension(1000000, 10000000));

		repaint();
	}

	private void updateAutomata() {
		w.getAutomata().getNodeNames().set(w.getEditing(), nameField.getText());
		w.getAutomata().setFinishing(w.getEditing(), finishingBox.isSelected());

		titleLabel.setText("Node '" + w.getAutomata().getNodeNames().get(w.getEditing()) + "':");

		w.repaint();
	}
}
