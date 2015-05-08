package org.apache.jmeter.visualizers;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.util.Date;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.apache.jmeter.samplers.Clearable;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.visualizers.gui.AbstractVisualizer;

public class TaskStatus extends AbstractVisualizer implements Clearable,
		ActionListener {

	private static final long serialVersionUID = 240L;
	
	private JLabel currentTimeLable = new JLabel();
	private JLabel startTimeLable = new JLabel();
	private JLabel endTimeLable = new JLabel();
	private JLabel durationTimeLable = new JLabel();

	private JTextField currentTimeText = new JTextField();
	private JTextField startTimeText = new JTextField();
	private JTextField endTimeText = new JTextField();
	private JTextField durationTimeText = new JTextField();
	
	private DateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	
	public TaskStatus() {
		super();
		clearData();
		init();
	}

	@Override
	public void add(SampleResult sample) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getLabelResource() {
		return "task_status"; //$NON-NLS-1$
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearData() {
    	currentTimeText.setText("");
    	startTimeText.setText("");
    	endTimeText.setText("");
    	durationTimeText.setText("");
	}

	/**
	 * Main visualizer setup.
	 */
	private void init() {
		this.setLayout(new BorderLayout());
		Border margin = new EmptyBorder(10, 10, 5, 10);
		this.setBorder(margin);
		
		// TITLE
		JLabel titleLabel = new JLabel(getStaticLabel());
        Font curFont = titleLabel.getFont();
        titleLabel.setFont(curFont.deriveFont((float) curFont.getSize() + 4));
        this.add(titleLabel, BorderLayout.NORTH);
        
		// MAIN PANEL
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(null);
		
		currentTimeLable = new JLabel("Current Time : ");
		startTimeLable = new JLabel("Start Time : ");
		endTimeLable = new JLabel("End Time : ");
		durationTimeLable = new JLabel("Duration Time : ");
		
		int lableLength = 150;
		int textLength = 150;
		int lineHeigh = 25;
		int ho = 10;
		currentTimeLable.setBounds(10, ho, lableLength, lineHeigh);
		currentTimeText.setBounds(10 + lableLength + ho, 10, textLength, lineHeigh);
		startTimeLable.setBounds(10, ho * 2 + lineHeigh * 1 , lableLength, lineHeigh);
		startTimeText.setBounds(10 + lableLength + ho, ho * 2 + lineHeigh * 1, textLength, lineHeigh);
		endTimeLable.setBounds(10, ho * 3 + lineHeigh * 2, lableLength, lineHeigh);
		endTimeText.setBounds(10 + lableLength + ho, ho * 3 + lineHeigh * 2, textLength, lineHeigh);
		durationTimeLable.setBounds(10, ho * 4 + lineHeigh * 3, lableLength, lineHeigh);
		durationTimeText.setBounds(10 + lableLength + ho, ho * 4 + lineHeigh * 3, textLength, lineHeigh);
		
		mainPanel.add(currentTimeLable);
		mainPanel.add(currentTimeText);
		
		mainPanel.add(startTimeLable);
		mainPanel.add(startTimeText);
		
		mainPanel.add(endTimeLable);
		mainPanel.add(endTimeText);
		
		mainPanel.add(durationTimeLable);
		mainPanel.add(durationTimeText);
        
		this.add(titleLabel, BorderLayout.NORTH);
        this.add(mainPanel, BorderLayout.CENTER);
	}

}
