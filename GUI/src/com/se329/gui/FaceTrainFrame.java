package com.se329.gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.ListModel;
import javax.swing.UIManager;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import javax.swing.JSplitPane;
import javax.swing.JScrollPane;

import java.awt.GridLayout;
import java.awt.BorderLayout;

import javax.swing.JPanel;

import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class FaceTrainFrame {

	public static final String outputFileName = "trainedSubjects.csv";
	
	public static final String exeFilePath = "../faceMap.exe";

	private static FileWriter writer = null;

	private static JFrame frmFaceTrainer;

	private static JLabel photoLabel_1;
	
	private static DefaultListModel<String> listModel = new DefaultListModel<String>();

	//subjects displayed on the right side of the screen
	private static ArrayList<Subject> subjects = new ArrayList<Subject>();
	//the subject you are browsing for photos for, it is not yet in "subjects"
	private static Subject currSub = new Subject(); 

	// Delimiter used in CSV file
	private static final String COMMA_DELIMITER = ",";
	private static final String NEW_LINE_SEPARATOR = "\n";
	// File header for CSV file

	private Firebase firebaseRef = new Firebase("https://torrid-heat-4382.firebaseio.com/");
	private static JTable table;
	private static JTextField nameTxt;
	
	private static final Object[] columnNames = {"Name", "Attendance"};
	private Object[][] tableData;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FaceTrainFrame window = new FaceTrainFrame();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public FaceTrainFrame() {
		querySubjects();
		
	}
	
	private void querySubjects(){
		System.out.println("adding listener");
		  firebaseRef.addChildEventListener(new ChildEventListener() {
		    
		    @Override
		    public void onCancelled(FirebaseError firebaseError) {
		        System.out.println("The read failed: " + firebaseError.getMessage());
		    }
			@Override
			public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {
				System.out.println("child added"); 
				subjects.clear();
				System.out.println("There are " + snapshot.getChildrenCount() + " Subjects");
		          for (DataSnapshot postSnapshot: snapshot.getChildren()) {
		        	  //System.out.println(postSnapshot.getValue());
		            Subject sub = postSnapshot.getValue(Subject.class);
		            sub.setId(previousChildKey);
		            System.out.println(sub.getName() + " " + sub.getTimesAttended());
		            subjects.add(sub);
		          }	
		          System.out.println("Size of arraylist " + subjects.size());
		          setTableData();
		          initialize();
			}
			@Override
			public void onChildChanged(DataSnapshot snapshot, String arg1) {
				
			}
			@Override
			public void onChildMoved(DataSnapshot arg0, String arg1) {
				
			}
			@Override
			public void onChildRemoved(DataSnapshot arg0) {
				
			}
		  });
	}
	private void addSubject(){
		System.out.println("adding subject...");
		Firebase subjectRef = firebaseRef.child("subjects");
		System.out.println(currSub.getName());
		subjectRef.push().setValue(currSub);
		subjects.add(currSub);
		
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		model.addRow(new Object[]{currSub.getName(), "0"});
		
		frmFaceTrainer.getContentPane().validate();
		frmFaceTrainer.getContentPane().repaint();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmFaceTrainer = new JFrame();
		frmFaceTrainer.getContentPane().setBackground(
				UIManager.getColor("FormattedTextField.selectionBackground"));
		frmFaceTrainer.setTitle("Face Recognition Attendance Taker");
		frmFaceTrainer.setBounds(100, 100, 719, 485);
		frmFaceTrainer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmFaceTrainer.setVisible(true);
		frmFaceTrainer.getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		frmFaceTrainer.getContentPane().add(panel, BorderLayout.WEST);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 202, 0, 4, 0 };
		gbl_panel.rowHeights = new int[] { 22, 17, 25, 261, 14, 35, 23,
				0 };
		gbl_panel.columnWeights = new double[] { 1.0, 1.0, 0.0,
				Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0, 0.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		JLabel descLabel = new JLabel("Add a new subject to be recognized.");
		GridBagConstraints gbc_descLabel = new GridBagConstraints();
		gbc_descLabel.anchor = GridBagConstraints.WEST;
		gbc_descLabel.insets = new Insets(0, 0, 5, 5);
		gbc_descLabel.gridx = 0;
		gbc_descLabel.gridy = 0;
		panel.add(descLabel, gbc_descLabel);
		descLabel.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 11));
		
		nameTxt = new JTextField();
		GridBagConstraints gbc_nameTxt = new GridBagConstraints();
		gbc_nameTxt.insets = new Insets(0, 0, 5, 5);
		gbc_nameTxt.fill = GridBagConstraints.HORIZONTAL;
		gbc_nameTxt.gridx = 0;
		gbc_nameTxt.gridy = 1;
		panel.add(nameTxt, gbc_nameTxt);
		nameTxt.setColumns(10);
		
				JLabel nameLabel = new JLabel("Name");
				GridBagConstraints gbc_nameLabel = new GridBagConstraints();
				gbc_nameLabel.anchor = GridBagConstraints.WEST;
				gbc_nameLabel.insets = new Insets(0, 0, 5, 5);
				gbc_nameLabel.gridx = 1;
				gbc_nameLabel.gridy = 1;
				panel.add(nameLabel, gbc_nameLabel);
				nameLabel.setFont(new Font("Arial Black", Font.PLAIN, 11));
								
										photoLabel_1 = new JLabel("Upload 10 photos...");
										GridBagConstraints gbc_photoLabel_1 = new GridBagConstraints();
										gbc_photoLabel_1.anchor = GridBagConstraints.WEST;
										gbc_photoLabel_1.insets = new Insets(0, 0, 5, 5);
										gbc_photoLabel_1.gridx = 0;
										gbc_photoLabel_1.gridy = 2;
										panel.add(photoLabel_1, gbc_photoLabel_1);
										photoLabel_1
												.setFont(new Font("Arial Rounded MT Bold", Font.ITALIC, 11));
						
								JButton photoButton_1 = new JButton("Select Photo");
								GridBagConstraints gbc_photoButton_1 = new GridBagConstraints();
								gbc_photoButton_1.anchor = GridBagConstraints.NORTHWEST;
								gbc_photoButton_1.insets = new Insets(0, 0, 5, 5);
								gbc_photoButton_1.gridx = 1;
								gbc_photoButton_1.gridy = 2;
								panel.add(photoButton_1, gbc_photoButton_1);
								photoButton_1.setFont(new Font("Arial Black", Font.PLAIN, 11));
								photoButton_1.setBackground(Color.CYAN);
								photoButton_1.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent e) {
										currSub.appendPhotoPath("../pics/" + selectPhoto());
									}
								});
				
						JList<String> list = new JList<String>(listModel);
						GridBagConstraints gbc_list = new GridBagConstraints();
						gbc_list.gridwidth = 2;
						gbc_list.insets = new Insets(0, 0, 5, 5);
						gbc_list.fill = GridBagConstraints.BOTH;
						gbc_list.gridx = 0;
						gbc_list.gridy = 3;
						panel.add(list, gbc_list);
						
								JButton addButton = new JButton("Add Subject");
								GridBagConstraints gbc_addButton = new GridBagConstraints();
								gbc_addButton.anchor = GridBagConstraints.WEST;
								gbc_addButton.insets = new Insets(0, 0, 5, 5);
								gbc_addButton.gridx = 0;
								gbc_addButton.gridy = 4;
								panel.add(addButton, gbc_addButton);
								addButton.setBackground(Color.GREEN);
								addButton.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent e) {
										if (nameTxt.getText().isEmpty()) {
											JOptionPane.showMessageDialog(frmFaceTrainer, "Missing name!");
//										} else if (currSub.getPhotoPaths().isEmpty() || currSub.getPhotoPaths().size() < 10) {
//											JOptionPane.showMessageDialog(frmFaceTrainer,
//													"Upload at least 10 photos!");
										} else {
											currSub.setName(nameTxt.getText());
											addSubject();
											resetValues();
										}
									}
								});
						
								JButton cancelButton = new JButton("Cancel");
								GridBagConstraints gbc_cancelButton = new GridBagConstraints();
								gbc_cancelButton.anchor = GridBagConstraints.WEST;
								gbc_cancelButton.insets = new Insets(0, 0, 0, 5);
								gbc_cancelButton.gridx = 0;
								gbc_cancelButton.gridy = 6;
								panel.add(cancelButton, gbc_cancelButton);
								cancelButton.setBackground(Color.RED);
								cancelButton.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent e) {
										resetValues();
									}
								});
								
								JButton runButton = new JButton("Run");
								runButton.setBackground(Color.YELLOW);
								GridBagConstraints gbc_runButton = new GridBagConstraints();
								gbc_runButton.insets = new Insets(0, 0, 0, 5);
								gbc_runButton.gridx = 1;
								gbc_runButton.gridy = 6;
								panel.add(runButton, gbc_runButton);
								runButton.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent e) {
								
										System.out.println("Closing frame and starting executable.");
										createFile();
										
										// Close frame
//										frmFaceTrainer.setVisible(false);
//										frmFaceTrainer.dispose();
										
										startFaceRecognition();
										
									}
								});

		JPanel panel_1 = new JPanel();
		frmFaceTrainer.getContentPane().add(panel_1, BorderLayout.CENTER);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[] { 0, 0 };
		gbl_panel_1.rowHeights = new int[] { 0, 0 };
		gbl_panel_1.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_panel_1.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		panel_1.setLayout(gbl_panel_1);

		table = new JTable(new DefaultTableModel(tableData, columnNames));
		GridBagConstraints gbc_table = new GridBagConstraints();
		gbc_table.fill = GridBagConstraints.BOTH;
		gbc_table.gridx = 0;
		gbc_table.gridy = 0;
		panel_1.add(table, gbc_table);
		
		frmFaceTrainer.getContentPane().validate();
		frmFaceTrainer.getContentPane().repaint();
		
	}

	private static String selectPhoto() {
		File photo;
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		int option = chooser.showOpenDialog(frmFaceTrainer);

		if (option == JFileChooser.APPROVE_OPTION) {
			photo = chooser.getSelectedFile();
			listModel.addElement(photo.getName());
			return photo.getName();
		}

		return null;
	}

	private boolean createFile() {
		try {
			writer = new FileWriter(outputFileName, false);
			
			for(Subject sub : subjects){
				writer.append(sub.getId());
				writer.append(COMMA_DELIMITER);
				writer.append(sub.getName());
				writer.append(COMMA_DELIMITER);
				writer.append(sub.getTimesAttended() + "");
				for(String str : sub.getPhotoPaths()){
					writer.append(COMMA_DELIMITER);
					writer.append(str);
				}
				writer.append(NEW_LINE_SEPARATOR);
			}
		} catch (Exception e) {
			System.out.println("Error creating csv!");
			return false;
		}
		return true;
	}

	private static void resetValues() {
		// Reset all values
		listModel.clear();
		nameTxt.setText("");
		currSub = new Subject();
	}
	
	private void startFaceRecognition(){
		//TODO
		
		if (new File(exeFilePath).exists()) {
		    try {

		        ProcessBuilder pb = new ProcessBuilder(exeFilePath, outputFileName);
		        pb.redirectError();
		        Process p = pb.start();
		        InputStream is = p.getInputStream();
		        int value = -1;
		        while ((value = is.read()) != -1) {
		            System.out.print((char) value);
		        }

		        int exitCode = p.waitFor();

		        System.out.println("exited with " + exitCode);
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		} else {
		    System.err.println(exeFilePath + " does not exist");
		}
	}
	
	private void setTableData(){
		int i = 1;
		tableData = new Object[subjects.size()+1][2];
		tableData[0] = columnNames;
		for(Subject sub : subjects){
			Object[] row = {sub.getName(), sub.getTimesAttended()};
			tableData[i] = row;
			i++;
		}
	}
}