package ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.CubicCurve2D;

import javax.swing.JLabel;
import javax.swing.event.MouseInputListener;

import automata.Operation;

public class AutomataDrawLabel extends JLabel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int nodeWidth = 0;
	private int nodeSpacing = 150;

	private Window w;
	protected int selectedNode = -1;

	protected int offSetX = 0;
	protected int offSetY = 0;
	
	private ActionListener onNextSelect;

	public AutomataDrawLabel(Window toSelect) {
		this.addMouseListener(new NodeSelector());

		this.selectedNode = -1;
		this.w = toSelect;
	}

	@Override
	public void paint(Graphics g2) {
		super.paint(g2);
		Graphics2D g = (Graphics2D) g2;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());

		g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke(2));

		nodeWidth = 50;

		int remaining = (int) (this.getWidth() * .75);
		nodeWidth = (int) (this.getWidth() * .08);
		remaining -= nodeWidth;
		if (w.getAutomata().getNodeNames().size() < 2)
			nodeSpacing = 100000;
		else
			nodeSpacing = remaining / (w.getAutomata().getNodeNames().size() - 1);

		offSetX = this.getWidth() / 2 - (nodeSpacing * (w.getAutomata().getNodeNames().size() - 1) + nodeWidth) / 2;
		offSetY = this.getHeight() / 2 - nodeWidth / 2;
		g.translate(offSetX, offSetY);

		paintStart(g, 0, 0);
		for (int i = 0; i < w.getAutomata().getNodeNames().size(); i++)
			paintNode(g, nodeSpacing * i, 0, w.getAutomata().getNodeNames().get(i),
					w.getAutomata().isFinishingState(i));

		for (int onNodeA = 0; onNodeA < w.getAutomata().getNodeNames().size(); onNodeA++) {

			for (int toNode = 0; toNode < w.getAutomata().getNodeNames().size(); toNode++) {
				String possibleInputs = "";

				for (int input = 0; input < w.getAutomata().getInputNames().size(); input++) {

					if (w.getAutomata().getMatrix().get(onNodeA).get(input).contains(new Operation(toNode)))
						possibleInputs += "," + w.getAutomata().getInputNames().get(input);
				}

				if (!possibleInputs.isEmpty())
					possibleInputs = possibleInputs.substring(1);

				paintConnection(g, nodeSpacing * onNodeA, 0, toNode - onNodeA, possibleInputs);

			}
		}

		paintSlection(g);
	}

	public void paintNode(Graphics2D g, int x, int y, String name, boolean finishing) {

		int fontSize = nodeWidth / 2;
		Font f = new Font("Calibri", Font.BOLD, fontSize);
		if (nodeWidth <= g.getFontMetrics(f).stringWidth(name)) {
			fontSize /= (double) g.getFontMetrics(f).stringWidth(name) / fontSize;
			f = new Font("Calibri", Font.BOLD, fontSize);
		}

		g.setColor(Color.LIGHT_GRAY);
		g.fillOval(x, y, nodeWidth, nodeWidth);
		g.setColor(Color.BLACK);
		g.drawOval(x, y, nodeWidth, nodeWidth);
		g.setColor(Color.BLACK);
		if (finishing) {
			int margin = nodeWidth / 10;
			g.drawOval(x + margin, y + margin, nodeWidth - margin * 2, nodeWidth - margin * 2);
		}

		g.setFont(f);
		g.drawString(name, x + nodeWidth / 2 - g.getFontMetrics().stringWidth(name) / 2,
				y + nodeWidth / 2 + fontSize / 3);
	}

	public void paintStart(Graphics2D g, int nodeX, int nodeY) {
		g.setColor(Color.BLACK);

		Polygon triangle = new Polygon(
				new int[] { (int) (nodeX - nodeWidth * .1), (int) (nodeX - nodeWidth * .5),
						(int) (nodeX - nodeWidth * .5) },
				new int[] { (int) (nodeY + nodeWidth * .5), (int) (nodeY + nodeWidth * .8),
						(int) (nodeY + nodeWidth * .2) },
				3);

		g.setColor(this.getBackground());
		g.fill(triangle);
		g.setColor(Color.BLACK);
		g.draw(triangle);
	}

	public void paintConnection(Graphics2D g, int nodeX, int nodeY, int offset, String reference) {
		if (reference.isEmpty())
			return;
		
		int lines = 1;
		for (char c : reference.toCharArray())
			if (c == '\n')
				lines ++;

		int heightPerOffset = (int) (nodeWidth * 5 / w.getAutomata().getNodeNames().size());
		
		int leftX = (int) (nodeX + .35 * nodeWidth);
		int rightX = (int) (nodeX + .65 * nodeWidth);
		if (offset == 0) {
			int vOutDraw = 50;
			int lOutDraw = 20;
			CubicCurve2D cc = new CubicCurve2D.Double(leftX, nodeY + nodeWidth, leftX - lOutDraw,
					nodeY + nodeWidth + vOutDraw, rightX + lOutDraw, nodeY + nodeWidth + vOutDraw, rightX,
					nodeY + nodeWidth);
			g.setColor(Color.BLACK);
			g.draw(cc);
			g.drawLine(rightX + offset * nodeSpacing, nodeY + nodeWidth,
					(int) (rightX + offset * nodeSpacing + nodeWidth * .2), (int) (nodeY + nodeWidth * 1.1));
			g.drawLine(rightX + offset * nodeSpacing, nodeY + nodeWidth,
					(int) (rightX + offset * nodeSpacing - nodeWidth * .1), (int) (nodeY + nodeWidth * 1.2));

			int fontSize = nodeWidth / 3;
			Font f = new Font("Calibri", 0, fontSize);
			g.setFont(f);
			g.setColor(Color.BLACK);
			g.drawString(reference, nodeX + nodeWidth / 2 - g.getFontMetrics().stringWidth(reference) / 2,
					(int) (nodeY + fontSize * .4 + nodeWidth + vOutDraw));
		}
		if (offset > 0) {
			int vOutDraw = offset * heightPerOffset;
			CubicCurve2D cc = new CubicCurve2D.Double(rightX, nodeY, rightX, nodeY - vOutDraw,
					leftX + offset * nodeSpacing, nodeY - vOutDraw, leftX + offset * nodeSpacing, nodeY);

			g.setColor(Color.BLACK);
			g.draw(cc);

			g.drawLine(leftX + offset * nodeSpacing, nodeY, (int) (leftX + offset * nodeSpacing - nodeWidth * .2),
					(int) (nodeY - nodeWidth * .1));
			g.drawLine(leftX + offset * nodeSpacing, nodeY, (int) (leftX + offset * nodeSpacing + nodeWidth * .1),
					(int) (nodeY - nodeWidth * .2));

			int fontSize = nodeWidth / 3;
			Font f = new Font("Calibri", 0, fontSize);
			g.setFont(f);
			g.setColor(Color.BLACK);
			g.drawString(reference,
					(rightX + (leftX + offset * nodeSpacing)) / 2 - g.getFontMetrics().stringWidth(reference) / 2,
					(int) (nodeY - heightPerOffset * offset * .75) - lines * g.getFont().getSize());
		}

		if (offset < 0) {
			int vOutDraw = offset * heightPerOffset;
			CubicCurve2D cc = new CubicCurve2D.Double(leftX, nodeY + nodeWidth, leftX, nodeY + nodeWidth - vOutDraw,
					rightX + offset * nodeSpacing, nodeY + nodeWidth - vOutDraw, rightX + offset * nodeSpacing,
					nodeY + nodeWidth);

			g.setColor(Color.BLACK);
			g.draw(cc);

			g.drawLine(rightX + offset * nodeSpacing, nodeY + nodeWidth,
					(int) (rightX + offset * nodeSpacing + nodeWidth * .2), (int) (nodeY + nodeWidth * 1.1));
			g.drawLine(rightX + offset * nodeSpacing, nodeY + nodeWidth,
					(int) (rightX + offset * nodeSpacing - nodeWidth * .1), (int) (nodeY + nodeWidth * 1.2));

			int fontSize = nodeWidth / 3;
			Font f = new Font("Calibri", 0, fontSize);
			g.setFont(f);
			g.setColor(Color.BLACK);
			g.drawString(reference,
					(leftX + (rightX + offset * nodeSpacing)) / 2 - g.getFontMetrics().stringWidth(reference) / 2,
					(int) (nodeY + nodeWidth - heightPerOffset * offset * .75) + lines * g.getFont().getSize());
		}
	}

	public void paintSlection(Graphics2D g) {
		if (selectedNode < 0)
			return;
		if (selectedNode >= w.getAutomata().getNodeNames().size())
			return;

		g.setColor(new Color(0, 0, 255, 100));
		g.fillOval(nodeSpacing * selectedNode, 0, nodeWidth, nodeWidth);
	}

	public int getSelectedNode() {
		return selectedNode;
	}

	public void executeOnNextSelect(ActionListener al) {
		this.onNextSelect = al;
	}

	private class NodeSelector implements MouseInputListener {

		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
			int selected = -1;
			
			if (e.getButton() != MouseEvent.BUTTON3)
				selected = (e.getX() - offSetX + nodeSpacing / 2 - nodeWidth / 2) / nodeSpacing;

			if (onNextSelect == null) {
				if (w != null) {
					selectedNode = selected;
					w.setEditingNode(selectedNode);
				}
				
			} else {
				onNextSelect.actionPerformed(new ActionEvent(this, selected, ""));
				
				onNextSelect = null;
			}

			repaint();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void mouseDragged(MouseEvent e) {
		}

		@Override
		public void mouseMoved(MouseEvent e) {
		}
	}
}
