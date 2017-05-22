import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Creates a high score dialog, displaying the top 10 scores.
 * Adds a new high score if there is one and lets the user assign his name to it.
 */
public class HighScoresPanel extends JPanel{
	private JTextField newName;
	private HighScoresControl scores;
	private GamePanel parent;
	private int scorePlace;
	
	/**
	 * Constructor. Creates a HighScoresPanel with the top 10 high scores stored 
	 * and adds a new one. (If new score is less than 0, no new scores are added)
	 * @param scores list of the current top 10 high scores
	 * @param newScore the new high score to be added
	 */
	public HighScoresPanel(GamePanel parent, HighScoresControl scores, long newScore){
		this.scores = scores;
		this.parent = parent;
		createPanel(newScore);
	}
	
	/**
	 * Constructor. Creates a HighScoresPanel with the top 10 high scores stored
	 * @param scores list of the current top 10 high scores
	 */
	public HighScoresPanel(GamePanel parent, HighScoresControl scores){
		this(parent,scores,-1);
	}
	
	/**
	 * Create the panel displaying the high scores.
	 * Add new high score if necessary
	 * @param newScore the new high score
	 */
	private void createPanel(long newScore){
		//set up the panels and their layouts
		setLayout(new BoxLayout(this,BoxLayout.LINE_AXIS));
		setPreferredSize(new Dimension(160,185));
		
		JPanel placePanel = new JPanel();
		placePanel.setLayout(new GridLayout(11,1));
		
		JPanel namePanel = new JPanel();
		namePanel.setLayout(new GridLayout(11,1));
		
		JPanel scorePanel = new JPanel();
		scorePanel.setLayout(new GridLayout(11,1));
		
		//add the title row
		placePanel.add(new JLabel("No."));
		namePanel.add(new JLabel("         NAME"));
		scorePanel.add(new JLabel("SCORE"));
		
		for(int i=0; i<10; i++){
			placePanel.add(new JLabel(Integer.toString(i+1)+"."));
		}
		
		//if no new score, show the current top 10
		if(newScore >= scores.getLowScore() || newScore <= 0){
			for(int i = 0;i < 10;i++){
				namePanel.add(new JLabel(scores.getName(i)));
				long tempScore = scores.getScore(i);
				scorePanel.add(new JLabel("  "+String.format("%02d:%02d:%02d", tempScore / 1000 / 60, tempScore / 1000 % 60, tempScore % 1000 / 10)));
			}
		}
		
		//if there is a new score add it and let the user add his name to it
		else{
			JPanel newNamePanel = new JPanel();
			newNamePanel.setLayout(new BoxLayout(newNamePanel,BoxLayout.LINE_AXIS));
			newName = new JTextField(9);
			((AbstractDocument) newName.getDocument()).setDocumentFilter(new CustomDocumentFilter());
			newName.requestFocusInWindow();
			
			//add the score at it's place and rearrange the rest of the scores
			for(int i=0;i<10;i++){
				if(newScore < scores.getScore(i)){
					scorePlace = i;
					for(int j = 9;j>i;j--){
						scores.setHighScore(j,scores.getName(j-1),scores.getScore(j-1));
					}
				break;
				}
			}

			//display the scores before the new one
			for(int i = 0;i < scorePlace;i++){
				namePanel.add(new JLabel(scores.getName(i)));
				long tempScore = scores.getScore(i);
				scorePanel.add(new JLabel("  "+String.format("%02d:%02d:%02d", tempScore / 1000 / 60, tempScore / 1000 % 60, tempScore % 1000 / 10)));
			}
			
			//display the new score with a space to assign a name to it
			newNamePanel.add(newName);
			newNamePanel.setPreferredSize(new Dimension(100, 5));
			namePanel.add(newNamePanel);
			scorePanel.add(new JLabel("  "+String.format("%02d:%02d:%02d", newScore / 1000 / 60, newScore / 1000 % 60, newScore % 1000 / 10)));
			
			//display the scores after the new one
			for(int i = scorePlace+1;i<10;i++){
				namePanel.add(new JLabel(scores.getName(i)));
				long tempScore = scores.getScore(i);
				scorePanel.add(new JLabel("  "+String.format("%02d:%02d:%02d", tempScore / 1000 / 60, tempScore / 1000 % 60, tempScore % 1000 / 10)));
			}
			
			//when a name is typed and Enter is pressed finalize the field and save the score
			newName.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e){
					if(!newName.getText().isEmpty()){
						scores.setHighScore(scorePlace,newName.getText(),newScore);
						try {
							scores.save();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						parent.remove(HighScoresPanel.this);
                        parent.revalidate();
                        parent.repaint();
					}
				}
			});
		}
		
		//add the objects to the mainPanel and shape it
		add(Box.createRigidArea(new Dimension(5, 0)));
		add(placePanel);
		add(Box.createRigidArea(new Dimension(3, 0)));
		add(namePanel);
		add(Box.createHorizontalGlue());
		add(scorePanel);
		add(Box.createRigidArea(new Dimension(5, 0)));
	}
	


	// Filter for the newName text field. Only alphabetical,numerical ,"_" and "-" characters can be entered up to 9 characters.
	private class CustomDocumentFilter extends DocumentFilter{
		private Pattern regexCheck = Pattern.compile("[A-Za-z0-9_-]+");
		private int maxChars = 9;
		
		@Override
		public void insertString(FilterBypass fb, int offset, String string, AttributeSet attrs) throws BadLocationException{
			if (string == null){
				return;
			}
			
			if(regexCheck.matcher(string).matches()&&(fb.getDocument().getLength() + string.length()) <= maxChars) {
				super.insertString(fb,offset,string,attrs);
			} else {
				Toolkit.getDefaultToolkit().beep();
			}
		}
		
		@Override
		public void replace(FilterBypass fb, int offset, int length, String string, AttributeSet attrs) throws BadLocationException{
			if(string == null) {
				return;
			}
			
			if(regexCheck.matcher(string).matches()&&(fb.getDocument().getLength() + string.length() - length) <= maxChars){
				fb.replace(offset,length,string,attrs);
			} else {
				Toolkit.getDefaultToolkit().beep();
			}
		}
	}
}