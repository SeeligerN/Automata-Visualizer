package ui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.GroupLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class InputChecker extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Window w;

	private JTextField inputField;
	private JLabel outputField;
	private JLabel outputField2;

	public InputChecker(Window w) {
		super(w, "Check input", true);
		this.w = w;

		inputField = new JTextField();
		inputField.setMaximumSize(new Dimension(500, 25));
		inputField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				updateOutputField();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				updateOutputField();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				updateOutputField();
			}
		});

		outputField = new JLabel();

		outputField2 = new JLabel();

		GroupLayout gl = new GroupLayout(this.getContentPane());
		gl.setAutoCreateContainerGaps(true);
		gl.setAutoCreateGaps(true);

		gl.setVerticalGroup(gl.createSequentialGroup().addComponent(inputField).addComponent(outputField)
				.addComponent(outputField2));

		gl.setHorizontalGroup(
				gl.createParallelGroup(Alignment.CENTER).addComponent(inputField).addComponent(outputField).addComponent(outputField2));

		this.setLayout(gl);

		updateOutputField();

		this.setSize(250, 125);
		this.setLocationRelativeTo(w);
		this.setVisible(true);
	}

	private void updateOutputField() {
		String endingStates = w.getAutomata().getFinishingStates(inputField.getText());
		boolean accepts = w.getAutomata().accepts(inputField.getText());

		if (endingStates.isEmpty())
			outputField.setText("Couldn't compile automata!");
		else
			outputField.setText("input ends on '" + endingStates + "'");

		outputField2.setForeground(accepts ? Color.GREEN : Color.RED);
		outputField2.setText(accepts ? "ACC" : "DEC");
		
		repaint();
	}
}
