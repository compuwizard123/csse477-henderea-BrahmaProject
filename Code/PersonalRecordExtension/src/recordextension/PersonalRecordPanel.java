package recordextension;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;


import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * A simple personal record application.
 * 
 * @author Chandan R. Rupakheti (rupakhet@rose-hulman.edu)
 */
public class PersonalRecordPanel extends JPanel {
	public String name;
	public String address;
	public String interest;
	public String bio;
	
	public PersonalRecordPanel() {
		layout(this);
	}
	
	public void layout(JPanel contentPane) {
		contentPane.setLayout(new BorderLayout());
		// Labels used
		JLabel lblName = new JLabel("Name");
		JLabel lblAddress = new JLabel("Address");
		JLabel lblInterest = new JLabel("Interest");
		JLabel lblBio = new JLabel("Biography");
		
		// Textfields used
		final JTextField txtName = new JTextField();
		final JTextField txtAddress = new JTextField();
		final JTextField txtInterest = new JTextField();
		
		// Textarea used
		final JTextArea txtBiography = new JTextArea(10,20);
		
		// Buttons
		JButton butClear = new JButton("Clear");
		JButton butSave = new JButton("Save");
		JButton butRead = new JButton("Read");
		JButton butView = new JButton("View");
		
		// Top Panel with table like layout (grid layout)
		GridLayout layoutTop = new GridLayout(3,2);
		JPanel panelTop = new JPanel();
		panelTop.setLayout(layoutTop);		
		
		// Add widgets to the top panel	
		panelTop.add(lblName); 
		panelTop.add(txtName); // First row
		panelTop.add(lblAddress);
		panelTop.add(txtAddress); // Second row
		panelTop.add(lblInterest);
		panelTop.add(txtInterest); // Third row
		
		// Add the top panel to the top section of the content pane
		contentPane.add(panelTop, BorderLayout.PAGE_START);
		
		// Center Panel with BorderLayout
		JPanel panelCenter = new JPanel();
		BorderLayout layoutCenter = new BorderLayout();
		panelCenter.setLayout(layoutCenter);
		
		// Add widgets to the center panel
		panelCenter.add(lblBio, BorderLayout.PAGE_START);
		panelCenter.add(txtBiography, BorderLayout.CENTER);
		
		// Add the center panel to the center section of the content pane
		contentPane.add(panelCenter, BorderLayout.CENTER);
		
		// Bottom panel with FlowLayout as its default layout manager
		JPanel panelBottom = new JPanel();
		
		// Add widgets to the bottom panel
		panelBottom.add(butClear);
		panelBottom.add(butSave);
		panelBottom.add(butRead);
		panelBottom.add(butView);
		
		// Add the bottom panel to the bottom section of the content pane
		contentPane.add(panelBottom, BorderLayout.PAGE_END);
		
		// Add action listeners
		// Clear button
		butClear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// Clear things
				txtName.setText("");
				txtAddress.setText("");
				txtInterest.setText("");
				txtBiography.setText("");
			}
		});
		
		// Save button
		butSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				name = txtName.getText();
				address = txtAddress.getText();
				interest = txtInterest.getText();
				bio = txtBiography.getText();
				
				try {
					FileWriter fstream = new FileWriter("data.txt");
					BufferedWriter writer = new BufferedWriter(fstream);
					writer.write(name + "\n");
					writer.write(address + "\n");
					writer.write(interest + "\n");
					writer.write(bio + "\n");
					writer.close();
				}
				catch(Exception ex) {
					ex.printStackTrace();
				}
				
				try {
					String html = "<html>" +
							"<body>" +
							"<table>" +
							"<tr><td>Name</td><td>" + name + "</td></tr>" +
							"<tr><td>Address</td><td>" + address + "</td></tr>" +
							"<tr><td>Interest</td><td>" + interest + "</td></tr>" +
							"<tr><td colspan=\"2\">Biography</td></tr>" +
							"<tr><td colspan=\"2\">" + bio + "</td></tr>" +
							"<table>" +
							"</body>" +
							"</html>";
					
					FileWriter fstream = new FileWriter("data.html");
					BufferedWriter writer = new BufferedWriter(fstream);
					writer.write(html);
					writer.close();
				}
				catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		
		// Read Button
		butRead.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					FileReader fReader = new FileReader("data.txt");
					BufferedReader reader = new BufferedReader(fReader);
					
					name = reader.readLine();
					address = reader.readLine();
					interest = reader.readLine();
					
					String line = "";					
					while((line = reader.readLine()) != null) {
						bio += line;
					}
					
					reader.close();
				}
				catch(Exception ex) {
					ex.printStackTrace();
				}
				
				txtName.setText(name);
				txtAddress.setText(address);
				txtInterest.setText(interest);
				txtBiography.setText(bio);
			}
		});
		
		// For showing html view
		final JFrame browserFrame = new JFrame("Record View");
		browserFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		JPanel panelBrowser = (JPanel)browserFrame.getContentPane();
		final JLabel lblBrowser = new JLabel();
		
		// Add label to panel
		panelBrowser.add(lblBrowser, BorderLayout.CENTER);	
		butView.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String html = "<html>" +
								"<body>" +
								"<table>" +
								"<tr><td>Name</td><td>" + name + "</td></tr>" +
								"<tr><td>Address</td><td>" + address + "</td></tr>" +
								"<tr><td>Interest</td><td>" + interest + "</td></tr>" +
								"<tr><td colspan=\"2\">Biography</td></tr>" +
								"<tr><td colspan=\"2\">" + bio + "</td></tr>" +
								"<table>" +
								"</body>" +
								"</html>";
				lblBrowser.setText(html);
				browserFrame.pack();
				browserFrame.setVisible(true);
			}
		});
	}
}
