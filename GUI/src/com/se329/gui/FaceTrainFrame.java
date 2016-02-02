package com.se329.gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

public class FaceTrainFrame {

	public static final String outputFileName = "trainedSubjects.csv";

	private static FileWriter writer = null;

	private static JFrame frmFaceTrainer;

	private static JLabel photoLabel_1;
	private static JLabel photoLabel_2;
	private static JLabel photoLabel_3;
	private static JTextArea nameTxt;

	/* Current subject's properties */
	private static File photo1;
	private static File photo2;
	private static File photo3;

	// Delimiter used in CSV file
	private static final String COMMA_DELIMITER = ",";
	private static final String NEW_LINE_SEPARATOR = "\n";
	// File header for CSV file
	private static final String FILE_HEADER = "name,photo1,photo2,photo3";
	
	private static Firebase firebaseRef = new Firebase("https://torrid-heat-4382.firebaseio.com/");

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
		frmFaceTrainer.setResizable(false);
		frmFaceTrainer.getContentPane().setBackground(
				UIManager.getColor("FormattedTextField.selectionBackground"));
		frmFaceTrainer.setTitle("Face Recognition - Trainer");
		frmFaceTrainer.setBounds(100, 100, 413, 302);
		frmFaceTrainer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmFaceTrainer.getContentPane().setLayout(null);
		frmFaceTrainer.setVisible(true);
		
		JLabel descLabel = new JLabel("Add a new subject to be recognized.");
		descLabel.setFont(new Font("Arial Rounded MT Bold", Font.PLAIN, 11));
		descLabel.setBounds(10, 11, 249, 23);
		frmFaceTrainer.getContentPane().add(descLabel);

		JLabel nameLabel = new JLabel("Name");
		nameLabel.setFont(new Font("Arial Black", Font.PLAIN, 11));
		nameLabel.setBounds(10, 44, 101, 14);
		frmFaceTrainer.getContentPane().add(nameLabel);

		nameTxt = new JTextArea();
		nameTxt.setBounds(59, 39, 208, 24);
		frmFaceTrainer.getContentPane().add(nameTxt);

		JButton photoButton_1 = new JButton("Select Photo");
		photoButton_1.setFont(new Font("Arial Black", Font.PLAIN, 11));
		photoButton_1.setBackground(Color.CYAN);
		photoButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				photo1 = selectPhoto(photoLabel_1);
			}
		});
		photoButton_1.setBounds(10, 83, 115, 31);
		frmFaceTrainer.getContentPane().add(photoButton_1);

		JButton photoButton_2 = new JButton("Select Photo");
		photoButton_2.setFont(new Font("Arial Black", Font.PLAIN, 11));
		photoButton_2.setBackground(Color.CYAN);
		photoButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				photo2 = selectPhoto(photoLabel_2);
			}
		});
		photoButton_2.setBounds(10, 123, 115, 31);
		frmFaceTrainer.getContentPane().add(photoButton_2);

		JButton photoButton_3 = new JButton("Select Photo");
		photoButton_3.setFont(new Font("Arial Black", Font.PLAIN, 11));
		photoButton_3.setBackground(Color.CYAN);
		photoButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				photo3 = selectPhoto(photoLabel_3);
			}
		});
		photoButton_3.setBounds(10, 165, 115, 31);
		frmFaceTrainer.getContentPane().add(photoButton_3);

		photoLabel_1 = new JLabel("Upload photo...");
		photoLabel_1
				.setFont(new Font("Arial Rounded MT Bold", Font.ITALIC, 11));
		photoLabel_1.setBounds(135, 92, 262, 14);
		frmFaceTrainer.getContentPane().add(photoLabel_1);

		photoLabel_2 = new JLabel("Upload photo...");
		photoLabel_2
				.setFont(new Font("Arial Rounded MT Bold", Font.ITALIC, 11));
		photoLabel_2.setBounds(135, 132, 262, 14);
		frmFaceTrainer.getContentPane().add(photoLabel_2);

		photoLabel_3 = new JLabel("Upload photo...");
		photoLabel_3
				.setFont(new Font("Arial Rounded MT Bold", Font.ITALIC, 11));
		photoLabel_3.setBounds(135, 174, 262, 14);
		frmFaceTrainer.getContentPane().add(photoLabel_3);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.setBackground(Color.RED);
		cancelButton.setBounds(36, 227, 89, 23);
		frmFaceTrainer.getContentPane().add(cancelButton);

		JButton addButton = new JButton("Add");
		addButton.setBackground(Color.GREEN);
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (nameTxt.getText().isEmpty()) {
					JOptionPane.showMessageDialog(frmFaceTrainer,
							"Missing name!");
				} else if (photo1 == null && photo2 == null && photo3 == null) {
					JOptionPane.showMessageDialog(frmFaceTrainer,
							"Upload at least one photo!");
				} else {
					// Append subject's name and photo paths to the output CSV
					// file
					FaceTrainFrame.appendSubjectToOutputFile(nameTxt.getText(),
							photo1 != null ? photo1.getAbsolutePath() : "",
							photo2 != null ? photo2.getAbsolutePath() : "",
							photo2 != null ? photo2.getAbsolutePath() : "");

					// Prompt choice of adding another subject or closing GUI
					// and launching the face recognition tool
					// dialog = new CompleteDialog(frmFaceTrainer);
					Object[] options = { "Yes", "No I'm Done" };
					int choice = JOptionPane
							.showOptionDialog(
									frmFaceTrainer,
									"Successfully added subject to be recognized. Add another?",
									"Subject Added",
									JOptionPane.DEFAULT_OPTION,
									JOptionPane.INFORMATION_MESSAGE, null,
									options, options[0]);
					// User chose "Yes"
					if (choice == 0) {
						// Clear values of all forms and variables. Ready for
						// new subject.
						resetValues();
					}

					// User chose "No, I'm Done!"
					else {
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
					}

				}
			}
		});
		addButton.setBounds(278, 227, 89, 23);
		frmFaceTrainer.getContentPane().add(addButton);
	}

	private static File selectPhoto(JLabel label) {
		File photo;
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		int option = chooser.showOpenDialog(frmFaceTrainer);

		if (option == JFileChooser.APPROVE_OPTION) {
			photo = chooser.getSelectedFile();
			label.setText(photo.getName());
			label.setFont(new Font("Arial Black", Font.PLAIN, 11));
			return photo;
		}

		return null;
	}

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
			
			//Testing adding to database
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
		photoLabel_2.setText("Upload photo...");
		photoLabel_2
				.setFont(new Font("Arial Rounded MT Bold", Font.ITALIC, 11));
		photoLabel_3.setText("Upload photo...");
		photoLabel_3
				.setFont(new Font("Arial Rounded MT Bold", Font.ITALIC, 11));
		nameTxt.setText("");

		photo1 = null;
		photo2 = null;
		photo3 = null;
	}
}
