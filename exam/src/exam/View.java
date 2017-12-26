package exam;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.SelectObservableValue;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.ui.part.ViewPart;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import model.Exam;
import model.Question;

public class View extends ViewPart {
	public static final String ID = "exam.view";

	private Exam exam;
	private ScrolledComposite sc;

	@SuppressWarnings("unchecked")
	@Override
	public void createPartControl(Composite parent) {
		parent.getShell().setText("Test");
		configurateDisplay(parent);

		Button open = new Button(parent, SWT.PUSH);
		open.setText("Izaberi Test");

		open.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				String selectedFile = openFile(parent);

				if (selectedFile != null && !selectedFile.isEmpty()) {
					open.setVisible(false);					
					exam = createExamByJSON(selectedFile);

					sc = new ScrolledComposite(parent, SWT.V_SCROLL);
					Composite content = new Composite(sc, SWT.NONE);
					content.setLayout(new GridLayout(1,false));
					
					Label titleLabel = new Label(content, SWT.BORDER | SWT.WRAP);
					titleLabel.setText(exam.getTitle() + ",\nDatum: " + exam.getData());
					titleLabel.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false));
					titleLabel.getParent().layout();

					for (Question question : exam.getListOfQuestions()) {
						Group group = new Group(content, SWT.SHADOW_ETCHED_IN);
						group.setText(question.getQuestionTitle());
						group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
						group.setLayout(new RowLayout(SWT.VERTICAL));
						
						SelectObservableValue<String> observableRadioButtons = new SelectObservableValue<String>();
						for (String name : question.getAnswers()) {
							Button button = new Button(group, SWT.RADIO);
							button.setText(name);
							observableRadioButtons.addOption("Izabrano: " + name,
									WidgetProperties.selection().observe(button));

							button.addSelectionListener(new SelectionAdapter() {
								@Override
								public void widgetSelected(SelectionEvent e) {
									if (button.getSelection()) {
										exam.getListOfanswers().put(question, name);
									}
								}
							});
						}
						Label label = new Label(content, SWT.BORDER | SWT.WRAP);
						label.setText("");
						label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
						label.getParent().layout();

						ISWTObservableValue labelTextObservable = WidgetProperties.text().observe(label);

						DataBindingContext dbc = new DataBindingContext();
						dbc.bindValue(observableRadioButtons, labelTextObservable);
					}

					Button submit = new Button(content, SWT.PUSH);
					submit.setText("Potvrdi");
					submit.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, false));
					submit.getParent().layout();
					
					submit.addSelectionListener(new SelectionAdapter() {

						@Override
						public void widgetSelected(SelectionEvent e) {
							if(exam.getListOfQuestions().size() != exam.getListOfanswers().size())
							{
								MessageDialog.openWarning(parent.getShell(), "Warning", 
										"Niste odgovorili na sva pitanja!!!");
							}
							else {
								ArrayList<String> correctAnswers = new ArrayList<>();
								HashMap<Question, String> mapOfAnswerts = exam.getListOfanswers();
								for (Map.Entry<Question, String> ans : mapOfAnswerts.entrySet()) {
									if (ans.getKey().getCorrectAnswer().equals(ans.getValue())) {
										correctAnswers.add(ans.getKey().getQuestionTitle());
									}
								}
								if (correctAnswers.isEmpty()) {
									MessageDialog.openInformation(parent.getShell(), "Info",
											"Netacni su vam svi odgovori!");
								} else {
									MessageDialog.openInformation(parent.getShell(), "Info", "Broj tacnih odgovora: "
											+ correctAnswers.size() + "/" + mapOfAnswerts.entrySet().size());
								}
							}
						}						
					});
					
					sc.setContent(content);
					sc.setExpandHorizontal(true);
					sc.setExpandVertical(true);
					sc.setMinSize(content.computeSize(SWT.DEFAULT, SWT.DEFAULT));
				}
			}

		});
		
		
	}

	private String openFile(Composite parent) {
		FileDialog fileDialog = new FileDialog(parent.getShell(), SWT.OPEN);
		fileDialog.setText("Open");
		fileDialog.setFilterPath("C:/");
		fileDialog.setFilterExtensions(new String[] { "*.json", "*.txt" });
		return fileDialog.open();
	}

	private Composite configurateDisplay(Composite parent) {
		parent.getShell().setSize(600, 750);
		Monitor primary = parent.getShell().getDisplay().getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = parent.getShell().getBounds();

		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;

		parent.getShell().setLocation(x, y);
		parent.setLayout(new GridLayout(1, false));
		parent.getParent().layout();
		return parent;
	}

	private Exam createExamByJSON(String fileName) {
		Exam exam = new Exam();
		exam.setListOfanswers(new HashMap<Question, String>());
		ArrayList<Question> questions = new ArrayList<Question>();

		JSONParser parser = new JSONParser();
		try {
			Object object = parser.parse(new FileReader(fileName));
			JSONObject jsonObject = (JSONObject) object;
			if ((String) jsonObject.get("Title") != null && !((String) jsonObject.get("Title")).isEmpty()) {
				exam.setTitle((String) jsonObject.get("Title"));
			}
			if ((String) jsonObject.get("Data") != null && !((String) jsonObject.get("Data")).isEmpty()) {
				exam.setData((String) jsonObject.get("Data"));
			}

			JSONArray listOfQuestions = null;
			if ((JSONArray) jsonObject.get("List Of Questions") != null
					&& !((JSONArray) jsonObject.get("List Of Questions")).isEmpty()) {
				listOfQuestions = (JSONArray) jsonObject.get("List Of Questions");

				for (Object questionsObj : listOfQuestions.toArray()) {
					JSONObject questionData = (JSONObject) questionsObj;
					Question question = new Question();
					question.setQuestionTitle((String) questionData.get("Question Title"));

					JSONArray answers = (JSONArray) questionData.get("answers");
					ArrayList<String> listOfAnswers = new ArrayList<String>();
					for (Object answerObj : answers.toArray()) {
						listOfAnswers.add(answerObj.toString());
					}
					question.setAnswers(listOfAnswers);
					question.setCorrectAnswer((String) questionData.get("Correct Answer"));
					questions.add(question);
				}
			}
			exam.setListOfQuestions(questions);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return exam;
	}

	@Override
	public void setFocus() {

	}
}