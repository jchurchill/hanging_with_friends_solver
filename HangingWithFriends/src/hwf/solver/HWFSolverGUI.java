package hwf.solver;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.*;

public class HWFSolverGUI extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;

	// Board to contain everything that will go on the GUI
	JTabbedPane tabPanes = new JTabbedPane();
	
	/////// Create Word Tab ///////
	// Button for creating word from set of letters
	JButton createButton;
	// Result display area
	JScrollPane createResultsPane;
	// Result display list
	JList createDataList;
	// Letter Tray
	JTextField createLetterTray;
	// Search time report
	JLabel createTimeLabel;
	
	/////// Guess Word Tab ///////
	// Buttons for letters
	JButton letterButtons[] = new JButton[26];
	// Button for querying dictionary when guessing
	JButton queryButton;
	// Reset button for letter selection
	JButton resetLetters;
	// Input for query
	JTextField queryField;
	// Result count label
	JLabel queryResultCountLabel;
	// Result Display List
	JList queryDataList;
	// Result Display Area
	JScrollPane queryResultsPane;
	// Search time report
	JLabel guessTimeLabel;
	// Top guessable letters
	// (Display List)
	JList freqDataList;
	// (Display Area)
	JScrollPane freqResultsPane;
	// Vowel Rule Checkbox
	JCheckBox vowelRuleBox;
	
	// Engine that handles all back-end work
	WordFinder solver;

	@SuppressWarnings("unused")
	public static void main(String args[]) {
		// Run the GUI
		HWFSolverGUI gui = new HWFSolverGUI();
	}
	
	public HWFSolverGUI() {
		super("Hanging With Friends Solver v1.0 by Justin Churchill");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().add(tabPanes);
		
		// Create tabs in tab panel
		tabPanes.addTab(
				"Create Word",
				new ImageIcon(getClass().getResource("/resource/tile.gif")),
				makeCreateWordTab(),
				"Create a word from a given set of letters.");
		tabPanes.addTab(
				"Guess Word",
				new ImageIcon(getClass().getResource("/resource/tile.gif")),
				makeGuessWordTab(),
				"Help for guessing an opponent's word.");
		
		// Load solving engine (load dictionaries)
		try {
			solver = new WordFinder();
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
		
		// Make the window appear once all is finished
		setResizable(false);
		createButtonPressed();
		queryButtonPressed();
		pack();
		setVisible(true);
	}
	
	private JPanel makeCreateWordTab() {
		JPanel result = new JPanel();
		result.setLayout(new BoxLayout(result, BoxLayout.LINE_AXIS));
		JPanel leftHalf = new JPanel();
		leftHalf.setLayout(new BoxLayout(leftHalf, BoxLayout.PAGE_AXIS));
		JPanel rightHalf = new JPanel();
		rightHalf.setLayout(new BoxLayout(rightHalf, BoxLayout.PAGE_AXIS));
		result.add(Box.createHorizontalStrut(30));
		result.add(leftHalf);
		result.add(Box.createHorizontalStrut(30));
		result.add(rightHalf);
		result.add(Box.createHorizontalStrut(30));
		
		// Left half
		JPanel p0 = new JPanel();
		p0.setLayout(new BoxLayout(p0, BoxLayout.LINE_AXIS));
		p0.add(Box.createGlue());
		JPanel p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.PAGE_AXIS));
		p2.add(new JLabel("Enter the letters"));
		p2.add(new JLabel("from your tray here:"));
		p2.add(Box.createVerticalStrut(5));
		p2.add(new JLabel("(* = wildcard)"));
		p0.add(p2);
		p0.add(Box.createGlue());
		leftHalf.add(Box.createVerticalStrut(30));
		leftHalf.add(Box.createGlue());
		leftHalf.add(p0);
		leftHalf.add(Box.createVerticalStrut(10));
		createLetterTray = new JTextField("MYLETTERS");
		createLetterTray.setHorizontalAlignment(JTextField.CENTER);
		createLetterTray.setMaximumSize(new Dimension(150, 80));
		leftHalf.add(createLetterTray);
		leftHalf.add(Box.createVerticalStrut(10));
		JPanel p1 = new JPanel();
		p1.setLayout(new BoxLayout(p1, BoxLayout.LINE_AXIS));
		p1.add(Box.createGlue());
		createButton = new JButton("Find words!");
		createButton.setActionCommand("create");
		createButton.addActionListener(this);
		p1.add(createButton);
		p1.add(Box.createGlue());
		leftHalf.add(p1);
		leftHalf.add(Box.createGlue());
		leftHalf.add(Box.createVerticalStrut(30));
		
		// Right half
		JPanel p3 = new JPanel();
		p3.setLayout(new BoxLayout(p3, BoxLayout.LINE_AXIS));
		p3.add(Box.createGlue());
		p3.add(new JLabel("Results"));
		p3.add(Box.createGlue());
		rightHalf.add(Box.createVerticalStrut(30));
		rightHalf.add(p3);
		createDataList = new JList();
		createDataList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		createResultsPane = new JScrollPane(createDataList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		rightHalf.add(Box.createVerticalStrut(10));
		rightHalf.add(createResultsPane);
		
		JPanel p4 = new JPanel();
		p4.setLayout(new BoxLayout(p4, BoxLayout.LINE_AXIS));
		p4.add(Box.createGlue());
		createTimeLabel = new JLabel("Waiting for search.");
		p4.add(createTimeLabel);
		p4.add(Box.createGlue());
		rightHalf.add(Box.createVerticalStrut(10));
		rightHalf.add(p4);
		rightHalf.add(Box.createVerticalStrut(30));
		return result;
	}
	
	private JPanel makeGuessWordTab() {
		JPanel result = new JPanel();
		result.setLayout(new BoxLayout(result, BoxLayout.LINE_AXIS));
		JPanel leftHalf = new JPanel();
		leftHalf.setLayout(new BoxLayout(leftHalf, BoxLayout.PAGE_AXIS));
		JPanel rightHalf = new JPanel();
		rightHalf.setLayout(new BoxLayout(rightHalf, BoxLayout.PAGE_AXIS));
		result.add(Box.createHorizontalStrut(30));
		result.add(leftHalf);
		result.add(Box.createHorizontalStrut(30));
		result.add(rightHalf);
		result.add(Box.createHorizontalStrut(30));
		
		// Left half
		JPanel p0 = new JPanel();
		p0.setLayout(new BoxLayout(p0, BoxLayout.LINE_AXIS));
		p0.add(Box.createGlue());
		p0.add(new JLabel("Already guessed letters:"));
		p0.add(Box.createGlue());
		leftHalf.add(Box.createVerticalStrut(30));
		leftHalf.add(p0);
		leftHalf.add(Box.createVerticalStrut(10));
		JPanel letters = new JPanel();
		letters.setLayout(new BoxLayout(letters, BoxLayout.PAGE_AXIS));
		int lettersPerRow = 6;
		for(int i=0; i<5; i++) {
			JPanel row = new JPanel();
			row.setLayout(new BoxLayout(row, BoxLayout.LINE_AXIS));
			for(int j=0; j<lettersPerRow; j++) {
				if(i*lettersPerRow+j<26) {
					letterButtons[i*lettersPerRow+j] = new JButton(""+(char)('A'+i*lettersPerRow+j));
					letterButtons[i*lettersPerRow+j].addActionListener(this);
					letterButtons[i*lettersPerRow+j].setActionCommand("letter" + i*lettersPerRow+j);
					letterButtons[i*lettersPerRow+j].setPreferredSize(new Dimension(50,30));
					letterButtons[i*lettersPerRow+j].setBackground(Color.white);
					row.add(letterButtons[i*lettersPerRow+j]);
				}
			}
			row.add(Box.createGlue());
			letters.add(row);
		}
		leftHalf.add(letters);
		leftHalf.add(Box.createVerticalStrut(15));
		
		JPanel reset = new JPanel();
		reset.setLayout(new BoxLayout(reset, BoxLayout.LINE_AXIS));
		resetLetters = new JButton("Reset Letters");
		resetLetters.setActionCommand("reset");
		resetLetters.addActionListener(this);
		reset.add(Box.createGlue());
		reset.add(resetLetters);
		reset.add(Box.createHorizontalStrut(10));
		reset.add(new JLabel("Use HWF vowel rule:"));
		vowelRuleBox = new JCheckBox();
		vowelRuleBox.setSelected(true);
		reset.add(vowelRuleBox);
		reset.add(Box.createGlue());
		leftHalf.add(reset);
		leftHalf.add(Box.createVerticalStrut(15));
		
		JPanel p5 = new JPanel();
		p5.setLayout(new BoxLayout(p5, BoxLayout.LINE_AXIS));
		p5.add(Box.createGlue());
		p5.add(new JLabel("Enter Query:"));
		p5.add(Box.createGlue());
		leftHalf.add(p5);
		leftHalf.add(Box.createVerticalStrut(5));
		
		queryField = new JTextField("Q.ERY");
		queryField.setHorizontalAlignment(JTextField.CENTER);
		leftHalf.add(queryField);
		leftHalf.add(Box.createVerticalStrut(5));
		JPanel p1 = new JPanel();
		p1.setLayout(new BoxLayout(p1, BoxLayout.LINE_AXIS));
		p1.add(Box.createGlue());
		queryButton = new JButton("Find words!");
		queryButton.setActionCommand("query");
		queryButton.addActionListener(this);
		p1.add(queryButton);
		p1.add(Box.createGlue());
		leftHalf.add(p1);
		leftHalf.add(Box.createVerticalStrut(30));
		
		// Right half
		JPanel p3 = new JPanel();
		p3.setLayout(new BoxLayout(p3, BoxLayout.LINE_AXIS));
		p3.add(Box.createGlue());
		queryResultCountLabel = new JLabel("Results (0)");
		p3.add(queryResultCountLabel);
		p3.add(Box.createGlue());
		rightHalf.add(Box.createVerticalStrut(30));
		rightHalf.add(p3);
		queryDataList = new JList();
		queryDataList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		queryResultsPane = new JScrollPane(queryDataList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		rightHalf.add(Box.createVerticalStrut(10));
		rightHalf.add(queryResultsPane);
		
		JPanel p6 = new JPanel();
		p6.setLayout(new BoxLayout(p6, BoxLayout.LINE_AXIS));
		p6.add(Box.createGlue());
		p6.add(new JLabel("Most Common Letters"));
		p6.add(Box.createGlue());
		rightHalf.add(Box.createVerticalStrut(10));
		rightHalf.add(p6);
		freqDataList = new JList();
		freqDataList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		freqResultsPane = new JScrollPane(freqDataList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		freqResultsPane.setMaximumSize(new Dimension(100, 100));
		rightHalf.add(Box.createVerticalStrut(10));
		rightHalf.add(freqResultsPane);
		
		JPanel p4 = new JPanel();
		p4.setLayout(new BoxLayout(p4, BoxLayout.LINE_AXIS));
		p4.add(Box.createGlue());
		guessTimeLabel = new JLabel("Waiting for search.");
		p4.add(guessTimeLabel);
		p4.add(Box.createGlue());
		rightHalf.add(Box.createVerticalStrut(10));
		rightHalf.add(p4);
		rightHalf.add(Box.createVerticalStrut(30));
		
		return result;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().indexOf("letter") == 0) {
			// Is a letter button (in the query tab)
			JButton source = (JButton)e.getSource();
			if(source.getBackground() == Color.green) {
				source.setBackground(Color.white);
			}
			else {
				source.setBackground(Color.green);
			}
		}
		else if("query".equals(e.getActionCommand())) {
			// Is the guery for possible words button
			queryButtonPressed();
		}
		else if("create".equals(e.getActionCommand())) {
			// Is the create word button
			createButtonPressed();
		}
		else if("reset".equals(e.getActionCommand())) {
			// Is the reset letters button
			for(int i=0; i<letterButtons.length; i++) {
				letterButtons[i].setBackground(Color.white);
			}
		}
		
	}
	
	private void createButtonPressed() {
		try {
			String letters = createLetterTray.getText();
			letters = letters.replace(" ", "");
			long timeTaken = System.currentTimeMillis();
			ArrayList<String> found = solver.makeQuery("", "", letters, false);
			if(found.size() == 0) {
				createDataList.setListData(new String[] {"<none>"});
			}
			else {
				createDataList.setListData(found.toArray());
			}
			timeTaken = System.currentTimeMillis() - timeTaken;
			createTimeLabel.setText(String.format("Search took %.3f seconds.", (double)timeTaken/1000.0));
		} catch (Exception e1) {
			createDataList.setListData(new String[] {"Error.","Please check your letters","and try again."});
			createTimeLabel.setText("Waiting for search.");
		}
	}
	
	private void queryButtonPressed() {
		String guessed = "";
		for(JButton b : letterButtons) {
			if(b.getBackground().equals(Color.green)) {
				guessed += b.getText();
			}
		}
		// Any letters in the query field were guessed but should not be
		// part of the excluded list, if they were selected
		String query = queryField.getText().toUpperCase();
		String excluding = "";
		for(int i=0; i<guessed.length(); i++) {
			if(query.indexOf(guessed.charAt(i)) == -1) {
				excluding += guessed.charAt(i); 
			}
		}
		try {
			long timeTaken = System.currentTimeMillis();
			ArrayList<String> queryResult = solver.makeQuery(query, excluding, "", vowelRuleBox.isSelected());
			if(queryResult.size() == 0) {
				queryDataList.setListData(new String[] {"<none>"});
			}
			else {
				queryDataList.setListData(queryResult.toArray());
			}
			timeTaken = System.currentTimeMillis() - timeTaken;
			guessTimeLabel.setText(String.format("Search took %.3f seconds.", (double)timeTaken/1000.0));
			queryResultCountLabel.setText("Results (" + queryResult.size() + ")");
			// Frequency report stuff
			int[] freqs = WordFinder.letterFrequency(queryResult);
			ArrayList<CharIntPair> freqs2 = new ArrayList<CharIntPair>();
			for(int i=0; i<freqs.length; i++) {
				if(query.indexOf('A'+i) == -1) {
					freqs2.add(new CharIntPair((char)('A'+i), freqs[i]));
				}
			}
			Collections.sort(freqs2);
			ArrayList<String> freqReport = new ArrayList<String>();
			for(int i=0; i<freqs2.size(); i++) {
				freqReport.add(freqs2.get(i).ch + " - " + freqs2.get(i).i);
			}
			freqDataList.setListData(freqReport.toArray());
		} catch (Exception e) {
			// Invalid query
			queryDataList.setListData(new String[] {"Error.","Please check your query","and try again."});
		}
	}

}

class CharIntPair implements Comparable<CharIntPair>{
	public char ch;
	public int i;
	
	public CharIntPair(char ch, int i) {
		this.ch = ch;
		this.i = i;
	}

	@Override
	public int compareTo(CharIntPair arg0) {
		// TODO Auto-generated method stub
		return arg0.i-i;
	}
}