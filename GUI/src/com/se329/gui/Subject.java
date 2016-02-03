package com.se329.gui;

import java.util.ArrayList;

public class Subject {

	private String name;
	private String id;
	private int timesAttended;
	private ArrayList<String> photoPaths = new ArrayList<String>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getTimesAttended() {
		return timesAttended;
	}
	public void setTimesAttended(int timesAttended) {
		this.timesAttended = timesAttended;
	}
	public ArrayList<String> getPhotoPaths() {
		return photoPaths;
	}
	public void setPhotoPaths(ArrayList<String> photoPaths) {
		this.photoPaths = photoPaths;
	}
	
	public void appendPhotoPath(String p){
		if (photoPaths != null){
			photoPaths.add(p);
		}
	}
	
	public void reset(){
		name = "";
		id = "";
		timesAttended = 0;
		photoPaths = new ArrayList<String>();
	}
}
