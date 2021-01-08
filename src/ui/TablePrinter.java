package ui;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class TablePrinter extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JLabel outputLabel;

	public TablePrinter(String table) {
		
		table = table.replace("\n", "<br/>");
		table = "<html><pre>" + table;
		table += "</pre></html>";
		
		outputLabel = new JLabel(table);
		outputLabel.setText(table);
		
		this.add(outputLabel);
		
		this.pack();
		this.setTitle("Automata Table");
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setVisible(true);
	}
	
}
