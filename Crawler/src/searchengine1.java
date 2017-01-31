import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.List;

import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTextPane;
import javax.swing.JTextArea;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;

public class searchengine1 extends JFrame  {
	String stem;
	String query;
	HashMap<String, Integer> qtokens=new HashMap<String,Integer>();
	static HashMap<String, HashMap<String, Integer>> doctokensnew;
	static HashMap<String, HashMap<String, Integer>> doctokfreqnew;
	Porter1 portstem = new Porter1();
	public static Indexing idx= new Indexing();
	public static Cosinesim cosim= new Cosinesim();	
	ArrayList<String>topresnew=new ArrayList<String>();
	ArrayList<String>moreresnew=new ArrayList<String>();	
	JTextField Searchquery;
	JTextArea output;	
	public searchengine1() 
	{
		setSize(new Dimension(600, 500));
		setBackground(new Color(0, 255, 255));
		getContentPane().setBackground(new Color(0, 128, 0));
		getContentPane().setForeground(Color.ORANGE);
		setTitle("UNT Search Engine");
		getContentPane().setLayout(null);
		Searchquery = new JTextField();
		Searchquery.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				qtokens.clear();
				queryprocess();
				
				try {
					cosim.cossimcal(qtokens);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		});
		Searchquery.setBounds(76, 11, 255, 20);
		getContentPane().add(Searchquery);
		Searchquery.setColumns(10);
		
		//output.setSize(d);
		JButton button = new JButton("Search");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				output.setText(null);
				//System.out.println(qtokens);
				qtokens.clear();
				//System.out.println(qtokens);
				queryprocess();
				
				try {
					topresnew.clear();
					cosim.cossimcal(qtokens);
					topresnew=cosim.topresults();
				System.out.println(topresnew);
				Iterator i=topresnew.iterator();
				while(i.hasNext())
				{
					output.append((String) i.next());
					output.append("\n");
					output.append("\n");
					output.append("\n");
					
				}
				output.setCaretPosition(output.getDocument().getLength());
				} catch (SQLException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		button.setBounds(341, 10, 89, 23);
		getContentPane().add(button);
		
		JButton morebutton = new JButton("More results");
		morebutton.setBackground(new Color(255, 165, 0));
		morebutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				output.setText(null);
				try {
					moreresnew.clear();
					moreresnew=cosim.moreresults();
					Iterator i1=moreresnew.iterator();
					while(i1.hasNext())
					{
						output.append((String) i1.next());
						output.append("\n");
						output.append("\n");
						output.append("\n");
						
					}
					output.setCaretPosition(output.getDocument().getLength());
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		morebutton.setBounds(455, 427, 119, 23);
		getContentPane().add(morebutton);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 58, 564, 358);
		getContentPane().add(scrollPane);
		output = new JTextArea();
		scrollPane.setViewportView(output);
		output.setForeground(Color.BLUE);
		output.setEditable(false);
		
		JLabel lblNewLabel = new JLabel("Search Results");
		lblNewLabel.setForeground(new Color(135, 206, 235));
		lblNewLabel.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 15));
		lblNewLabel.setBounds(10, 33, 144, 20);
		getContentPane().add(lblNewLabel);
		
		
		
	}
	public void queryprocess()
	{
		
		try
		{
			HashMap<String, Integer> stopwords = idx.readstopwords();
			query=Searchquery.getText();
			String s[]=query.split("\\s");
			for(int i=0;i<s.length;i++)
			{
				if(stopwords.containsKey(s[i]))
				continue;
				stem = portstem.stripAffixes(s[i]);
				// Eliminating stop words after stemming
				if (stopwords.containsKey(stem))
				continue;
				Integer count = qtokens.get(stem);
				//System.out.println(count);
				qtokens.put(stem, (count == null) ? 1 : count + 1);
			}
			System.out.println("query hashmap"+qtokens);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}


	/**
}
	 * Launch the application.
	 */
	public static void main(String[] args)throws IOException {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					searchengine1 frame = new searchengine1();
					frame.setVisible(true);
				
						doctokensnew=idx.tokenizer();
				
					doctokfreqnew=idx.doccount;
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
