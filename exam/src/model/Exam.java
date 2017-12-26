package model;

import java.util.ArrayList;
import java.util.HashMap;

public class Exam extends ModelObject {

	private String title;
	private String data;
	private ArrayList<Question> listOfQuestions;
	private HashMap<Question, String> listOfanswers;
	
	public HashMap<Question, String> getListOfanswers() {
		return listOfanswers;
	}

	public String getTitle() {
		return title;
	}

	public String getData() {
		return data;
	}

	public ArrayList<Question> getListOfQuestions() {
		return listOfQuestions;
	}

	public void setTitle(String title) {
		firePropertyChange("title", this.title, this.title = title);
	}

	public void setData(String data) {
		firePropertyChange("data", this.data, this.data = data);
	}
	
	public void setListOfanswers(HashMap<Question, String> listOfanswers) {
		firePropertyChange("listOfanswers", this.listOfanswers, this.listOfanswers = listOfanswers);
	}

	public void setListOfQuestions(ArrayList<Question> listOfQuestions) {
		firePropertyChange("listOfQuestions", this.listOfQuestions, this.listOfQuestions = listOfQuestions);
	}

}
