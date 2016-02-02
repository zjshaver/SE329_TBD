package com.se329.gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import com.firebase.client.Firebase;

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

public class FaceTrainFrame {

	public static final String outputFileName = "trainedSubjects.csv";

	private static FileWriter writer = null;

	private static JFrame frmFaceTrainer;

	private static JLabel photoLabel_1;

	private static ArrayList<Subject> subjects = new ArrayList<Subject>(); //subjects displayed on the right side of the screen
	private static Subject currSub = new Subject(); //the subject you are browsing for photos for, it is not yet in "subjects"

	/* Current subject's properties */
	// private static File photo1;
	// private static File photo2;
	// private static File photo3;

	// Delimiter used in CSV file
	private static final String COMMA_DELIMITER = ",";
	private static final String NEW_LINE_SEPARATOR = "\n";
	// File header for CSV file
	private static final String FILE_HEADER = "name,photo1,photo2,photo3";

	private static Firebase firebaseRef = new Firebase(
			"https://torrid-heat-4382.firebaseio.com/");
	private static JTable table;
	private static JTextField nameTxt;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FaceTrainFrame window = new FaceTrainFrame();

					// Open CSV output file for writing
					try {
						writer = new FileWriter(outputFileName);
						writer.append(FILE_HEADER.toString());
						writer.append(NEW_LINE_SEPARATOR);
					} catch (Exception e) {
						System.out.println("Error writing to output file.");
						e.printStackTrace();
					}

					// TO DO: Query database for all subjects and add them to
					// subjects list

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
		initialize();
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
										currSub.appendPhotoPath(selectPhoto(photoLabel_1));
									}
								});
				
						JList<String> list = new JList<String>();
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
											JOptionPane.showMessageDialog(frmFaceTrainer,
													"Missing name!");
										} else if (currSub.getPhotoPaths().isEmpty()) {
											JOptionPane.showMessageDialog(frmFaceTrainer,
													"Upload at least one photo!");
										} else {
											//TODO: Test if this works...
											subjects.add(currSub);
											currSub.reset();
											
											// TODO: move this to new "Run" button
											/*else {
												// Close crame and start executable...
												System.out
														.println("Closing frame and starting executable.");
												// Clean up file writer
												try {
													writer.flush();
													writer.close();
												} catch (IOException except) {
													System.out
															.println("Error while flushing/closing fileWriter !!!");
													except.printStackTrace();
												}

												// Close frame
												frmFaceTrainer.setVisible(false);
												frmFaceTrainer.dispose();

												// TO DO: Start C++ .exe
											}*/

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
								
								JButton runButton = new JButton("Run");
								runButton.setBackground(Color.YELLOW);
								GridBagConstraints gbc_runButton = new GridBagConstraints();
								gbc_runButton.insets = new Insets(0, 0, 0, 5);
								gbc_runButton.gridx = 1;
								gbc_runButton.gridy = 6;
								panel.add(runButton, gbc_runButton);

		JPanel panel_1 = new JPanel();
		frmFaceTrainer.getContentPane().add(panel_1, BorderLayout.CENTER);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[] { 0, 0 };
		gbl_panel_1.rowHeights = new int[] { 0, 0 };
		gbl_panel_1.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_panel_1.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		panel_1.setLayout(gbl_panel_1);

		table = new JTable();
		GridBagConstraints gbc_table = new GridBagConstraints();
		gbc_table.fill = GridBagConstraints.BOTH;
		gbc_table.gridx = 0;
		gbc_table.gridy = 0;
		panel_1.add(table, gbc_table);
	}

	private static String selectPhoto(JLabel label) {
		File photo;
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		int option = chooser.showOpenDialog(frmFaceTrainer);

		if (option == JFileChooser.APPROVE_OPTION) {
			photo = chooser.getSelectedFile();
			label.setText(photo.getName());
			label.setFont(new Font("Arial Black", Font.PLAIN, 11));
			return photo.getName();
		}

		return null;
	}

	//TODO: this should become "createOutput" file, which loops through "subjects" and adds all subjects and photos to a CSV
	private static boolean appendSubjectToOutputFile(String name,
			String photoPath1, String photoPath2, String photoPath3) {
		try {
			writer.append(name);
			writer.append(COMMA_DELIMITER);
			writer.append(photoPath1);
			writer.append(COMMA_DELIMITER);
			writer.append(photoPath2);
			writer.append(COMMA_DELIMITER);
			writer.append(photoPath3);
			writer.append(NEW_LINE_SEPARATOR);
			System.out.println("Added subject successfully!");

			// Testing adding to database
			Firebase subjectRef = firebaseRef.child("subjects");

			Map<String, String> subject1 = new HashMap<String, String>();
			subject1.put("name", name);
			subject1.put("timesAttended", "0");
			subjectRef.push().setValue(subject1);

		} catch (Exception e) {
			System.out.println("Error adding subject!");
			return false;
		}
		return true;
	}

	private static void resetValues() {
		// Reset all values
		photoLabel_1.setText("Upload photo...");
		photoLabel_1
				.setFont(new Font("Arial Rounded MT Bold", Font.ITALIC, 11));
		nameTxt.setText("");
		currSub.reset();
	}
}
