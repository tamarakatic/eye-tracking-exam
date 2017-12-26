package model;

import java.util.ArrayList;

public class Question extends ModelObject {

	private String questionTitle;
	private ArrayList<String> answers;
	private String correctAnswer;
	
	public String getQuestionTitle() {
		return questionTitle;
	}

	public ArrayList<String> getAnswers() {
		return answers;
	}

	public String getCorrectAnswer() {
		return correctAnswer;
	}

	public void setQuestionTitle(String question) {
		firePropertyChange("question", this.questionTitle, this.questionTitle = question);
	}

	public void setAnswers(ArrayList<String> answers) {
		firePropertyChange("answers", this.answers, this.answers = answers);
	}

	public void setCorrectAnswer(String correctAnswer) {
		firePropertyChange("correctAnswer", this.correctAnswer,
				this.correctAnswer = correctAnswer);
	}

}
