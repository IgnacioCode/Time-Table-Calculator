package timetable;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.awt.Desktop;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.FileChooser;

public class Controller implements Initializable{
	
	private static final int ROW_DAYS = 6;
	private static final double SUBJECT_LABEL_WIDTH = 250;
	private static final double SUBJECT_LABEL_HEIGHT = 47;
	private static final double NAME_FONT_SIZE = 19;
	private static final double HBOX_WIDTH = 404;
	private static final double HBOX_HEIGHT = 28;
	private static final double EDIT_BUTTON_HEIGHT = 41;
	private static final double EDIT_BUTTON_WIDTH = 62;
	private static String SUBJECT_SAVE_FILE = "subjectFile.json";
	private static final String SUBJECT_CREATION_WINDOW = "subjectCreationWindow.fxml";
	private static final String MAIN_WINDOW = "mainWindow.fxml";
	private static final String RESULTS_WINDOW = "resultsWindow.fxml";
	private static final String ABOUT_WINDOW = "infoWindow.fxml";
	private Stage currentStage;
	private Scene currentScene;
	
	@FXML
	TextField newSubjectName = new TextField();
	@FXML
	Button selectNameButton = new Button();
	@FXML
	Button completeButton = new Button();
	@FXML
	Button calculateButton = new Button();
	@FXML
	Label subjectNameTitle = new Label();
	@FXML
	Label pageNumberLabel = new Label();
	@FXML
	Label combNumberLabel = new Label();
	@FXML
	TableView<WeekRow> daysTable = new TableView<>();
	@FXML
	AnchorPane leftMainPanel = new AnchorPane();
	@FXML
	AnchorPane rightMainPanel = new AnchorPane();
	@FXML
	VBox leftVBox = new VBox();
	@FXML
	VBox rightVBox = new VBox();
	@FXML
	MenuItem changeFileButton = new MenuItem();
	@FXML
	MenuItem saveFileButton = new MenuItem();
	@FXML
	MenuItem aboutButton = new MenuItem();
	
	@FXML
	TableColumn<WeekRow,String> monday = new TableColumn<WeekRow,String>();
	@FXML
	TableColumn<WeekRow,String> tuesday = new TableColumn<WeekRow,String>();
	@FXML
	TableColumn<WeekRow,String> wednesday = new TableColumn<WeekRow,String>();
	@FXML
	TableColumn<WeekRow,String> thursday = new TableColumn<WeekRow,String>();
	@FXML
	TableColumn<WeekRow,String> friday = new TableColumn<WeekRow,String>();
	@FXML
	TableColumn<WeekRow,String> saturday = new TableColumn<WeekRow,String>();
	
	@FXML
	TableView<WeekRow> weekTable0 = new TableView<WeekRow>();
	@FXML
	TableView<WeekRow> weekTable1 = new TableView<WeekRow>();
	@FXML
	TableView<WeekRow> weekTable2 = new TableView<WeekRow>();
	@FXML
	TableView<WeekRow> weekTable3 = new TableView<WeekRow>();
	
	FXMLLoader loader;
	String newNameString;
	
	List<Timetable> resultsList;
	
	int pageNumber = 1;
	int totalPages;
	
	Stage resultsStage;
	Scene resultsScene;
	Stage helpStage;
	Scene helpScene;
	
	ObservableList<WeekRow> data = FXCollections.observableArrayList(
    		new WeekRow("Morning"),
    		new WeekRow("Afternoon"),
    		new WeekRow("Evening")
    		);
	
	
	public Controller() {
		loader = new FXMLLoader();
		loader.setController(this);
	}
	
	// Called when clicked the "add" button to add a subject
	@FXML
	public void addSubject(ActionEvent e) throws IOException {
		changeScene(e, "nameSubjectWindow.fxml", "Subject Name"); // Will only ask for the subject's name
	}
	
	public void calculateTimeTable(ActionEvent e) {
		
		System.out.println("-------------------------------");
		
		List<Subject> subjects = loadSaveFile();
		for (Iterator<Subject> it = subjects.iterator(); it.hasNext();) {
			Subject subject = (Subject) it.next();
			if(subject.waiting)
				it.remove();
		}
		
		if(subjects.isEmpty())
			return;
		
		//resultsList = Timetable.calculateTimeTables(subjects);
		
		System.out.println(resultsList.size()+" Options");
		
		loader = new FXMLLoader(getClass().getResource(RESULTS_WINDOW));
		loader.setController(this);
		Pane root = null;
		try {
			root = loader.load();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		resultsStage = new Stage();
		resultsScene = new Scene(root);
		resultsStage.setScene(resultsScene);
		resultsStage.setTitle("Results");
		
		
		List<TableView<WeekRow>> resultTables = new LinkedList<TableView<WeekRow>>();
		
		resultTables.add(weekTable0);
		resultTables.add(weekTable1);
		resultTables.add(weekTable2);
		resultTables.add(weekTable3);
		
		pageNumber = 1;
		totalPages = (int) Math.ceil((float)resultsList.size()/4);
		
		pageNumberLabel.setText("Page "+ pageNumber + " of " + totalPages );
		
		for (int i = (4-pageNumber*4); i < resultsList.size() && i<(8-pageNumber*4); i++) {
			
			setUpTable(resultsList.get(i),resultTables.get(i%4));
			
		}
		
		
		
		resultsStage.setResizable(false);
		resultsStage.show();
	}
	

	public void addSubjectToCalculateList(ActionEvent e) {
		String subjectToMove = ((Node)e.getSource()).getId();
		HBox HBoxToMove = (HBox)((Node)e.getSource()).getParent();
		
		List<Subject> subjects = loadSaveFile();
		
		Label label = (Label)HBoxToMove.getChildren().get(0);
		Button addButton = (Button) (HBoxToMove.getChildren().get(1));
		Button deleteButton = (Button) (HBoxToMove.getChildren().get(3));
		
		label.setPrefWidth(SUBJECT_LABEL_WIDTH-50);
		addButton.setText("Remove");
		addButton.prefWidth(EDIT_BUTTON_WIDTH+50);
		addButton.setOnAction(event ->{removeSubject(event);});
		deleteButton.setOnAction(event->{deleteSubject(event);});
		
		for (Subject s : subjects) {
			if(s.name.equals(subjectToMove))
				s.waiting = false;
		}
		
		writeSaveFile(subjects);
		
		rightVBox.getChildren().add(HBoxToMove);
		leftVBox.getChildren().remove(HBoxToMove);
		updateCombinationsText();
		currentStage.show();
	}
	
	public void deleteSubject(ActionEvent e){
		String subjectToDelete = ((Node)e.getSource()).getId();
		File subjectSaveFile = new File(SUBJECT_SAVE_FILE);
		ObjectMapper objectMapper = new ObjectMapper();
		List<Subject> subjects = new LinkedList<Subject>();
		boolean leftDelete = false;
		
		try {
			subjects = objectMapper.readValue(subjectSaveFile, new TypeReference<List<Subject>>(){});
		}catch(MismatchedInputException ex){
			
		}catch (StreamReadException e1) {
			e1.printStackTrace();
		}catch (DatabindException e1) {
			e1.printStackTrace();
		}catch (IOException e1) {
			e1.printStackTrace();
		}
		
		Node HBoxToDelete = ((Node)e.getSource()).getParent();
		
		for (Iterator<Subject> iterator = subjects.iterator(); iterator.hasNext();) {
			Subject subject = (Subject) iterator.next();
			
			if(subject.waiting)
				leftDelete = true;
			
			if(subject.name.equals(subjectToDelete))
				iterator.remove();
			
		}
		
		if(leftDelete)
			leftVBox.getChildren().remove(HBoxToDelete);
		else
			rightVBox.getChildren().remove(HBoxToDelete);
		
		try {
			objectMapper.writeValue(subjectSaveFile, subjects);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		try {
			loadRightSubjectVBox();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		currentStage.show();
	}
	
	//Will show up the subject's section table so the user can edit and save it again
	public void editSubject(ActionEvent e) {
		String subjectNameToEdit = ((Node)e.getSource()).getId();
		List<Subject> subjects = loadSaveFile();
		Subject subjectToEdit = null;
		
		for (Subject subject : subjects) {
			if(subject.name.equals(subjectNameToEdit))
				subjectToEdit = subject;
		}
		
		try {
			changeScene(e,SUBJECT_CREATION_WINDOW,"Edit Subject");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
        subjectNameTitle.setText(subjectNameToEdit);
        
        prepareWeekTable();
		
		updateWeekRowCollumns(subjectToEdit);
		
		completeButton.setOnAction(event ->{saveEditedSubject(event,subjectNameToEdit);});
		
		currentStage.show();
	}
	
	public void removeSubject(ActionEvent e) {
		String subjectToMove = ((Node)e.getSource()).getId();
		HBox HBoxToMove = (HBox)((Node)e.getSource()).getParent();
		
		List<Subject> subjects = loadSaveFile();
		
		Label label = (Label)HBoxToMove.getChildren().get(0);
		Button addButton = (Button) (HBoxToMove.getChildren().get(1));
		
		label.setPrefWidth(SUBJECT_LABEL_WIDTH-50);
		addButton.setText("Add");
		addButton.prefWidth(EDIT_BUTTON_WIDTH+50);
		addButton.setOnAction(event ->{addSubjectToCalculateList(event);});
		
		for (Subject s : subjects) {
			if(s.name.equals(subjectToMove))
				s.waiting = true;
		}
		
		writeSaveFile(subjects);
		
		leftVBox.getChildren().add(HBoxToMove);
		rightVBox.getChildren().remove(HBoxToMove);
		
		updateCombinationsText();
		currentStage.show();
	}
	
	@FXML
	public void changeResultPageUp(ActionEvent e) {
		
		pageNumber--;
		
		if(pageNumber<1) {
			pageNumber++;
			return;
		}
		
		pageNumberLabel.setText("Page " + pageNumber + " of " + totalPages);
		
		List<TableView<WeekRow>> resultTables = new LinkedList<TableView<WeekRow>>();
		
		resultTables.add(weekTable0);
		resultTables.add(weekTable1);
		resultTables.add(weekTable2);
		resultTables.add(weekTable3);
		
		for (int i = (pageNumber*4 - 4); i < resultsList.size() && i<pageNumber*4; i++) {
			
			setUpTable(resultsList.get(i),resultTables.get(i%4));
			
		}
		resultsStage.setResizable(false);
		resultsStage.show();
		
	}
	
	@FXML
	public void changeResultPageDown(ActionEvent e) {
		
		pageNumber++;
		
		if(pageNumber>totalPages) {
			pageNumber--;
			return;
		}
		
		pageNumberLabel.setText("Page " + pageNumber + " of " + totalPages);
		
		List<TableView<WeekRow>> resultTables = new LinkedList<TableView<WeekRow>>();
		
		resultTables.add(weekTable0);
		resultTables.add(weekTable1);
		resultTables.add(weekTable2);
		resultTables.add(weekTable3);
		
		for (int i = 0; i < 4; i++) {
			setUpCleanTable(new Timetable(),resultTables.get(i));
		}
		
		for (int i = (pageNumber*4 - 4); i < resultsList.size() && i<pageNumber*4; i++) {
			
			setUpTable(resultsList.get(i),resultTables.get(i%4));
			
		}
		resultsStage.setResizable(false);
		resultsStage.show();
		
	}

	//Called after writting and selecting the new subject name in the little window
	@FXML
	public void selectName(ActionEvent e) throws IOException {

		newNameString = newSubjectName.getText();
		
        changeScene(e, SUBJECT_CREATION_WINDOW, "Create new Subject");
        
        subjectNameTitle.setText(newNameString);
        
		//Here we set the events for when clicked on the cell, a text field appears for updating its value
		//Repeated for each collumn of the table
        prepareWeekTable();
        
        currentStage.show();
        
	}

	
	@FXML
	public void saveNewSubject(ActionEvent e) throws IOException {
		File subjectSaveFile = new File(SUBJECT_SAVE_FILE);
		ObjectMapper objectMapper = new ObjectMapper();
		List<Subject> subjects = new LinkedList<Subject>();
		List<Shift> newShifts = new LinkedList<Shift>();
		List<Section> newSections = new LinkedList<Section>();
		int sectionCount = 1;
		
		try {
			subjects = objectMapper.readValue(subjectSaveFile, new TypeReference<List<Subject>>(){});
		}
		catch(MismatchedInputException ex){
			
		}
		
		for (int i = 0; i < sectionCount; i++) {
			for (WeekRow row : data) {
				String[] cells = new String[ROW_DAYS];
				
				//Copys the fields values to the String array for easy access
				row.extractValues(cells);
				
				for (int j = 0;j<ROW_DAYS;j++) {
					for (String value : cells[j].split(",")) { //Separates the cell value into the sections numbers
						if(value=="")
							continue;
						int val = Integer.parseInt(value);
						if(val == i+1)
							newShifts.add(new Shift(dayNumber(j),row.time));
						else if(val > sectionCount) //When it finds a new max value then the hole loop will continue until that value
							sectionCount = val;
					}
					
				}
			}
			
			newSections.add(new Section(newShifts));
			newShifts = new LinkedList<Shift>();
		}
		
		//Subject gets saved using a JSON file that has a List of all the Subject objects
		subjects.add(new Subject(newSections,newNameString));
		objectMapper.writeValue(subjectSaveFile, subjects);
		
		changeScene(e, MAIN_WINDOW, "Time Table Calculator");
		
	}
	
	
	public void saveEditedSubject(ActionEvent e, String subjectNameToEdit){
		File subjectSaveFile = new File(SUBJECT_SAVE_FILE);
		List<Subject> subjects = loadSaveFile();
		ObjectMapper mapper = new ObjectMapper();
		List<Shift> newShifts = new LinkedList<Shift>();
		List<Section> newSections = new LinkedList<Section>();
		
		
		int sectionCount = 1;
		
		for (Subject subject : subjects) {
			if(subject.name.equals(subjectNameToEdit)) {
				
				for (int i = 0; i < sectionCount; i++) {
					for (WeekRow row : data) {
						String[] cells = new String[ROW_DAYS];
						
						//Copys the fields values to the String array for easy access
						row.extractValues(cells);
						
						for (int j = 0;j<ROW_DAYS;j++) {
							for (String value : cells[j].split(",")) { //Separates the cell value into the sections numbers
								if(value=="")
									continue;
								int val = Integer.parseInt(value);
								if(val == i+1)
									newShifts.add(new Shift(dayNumber(j),row.time));
								else if(val > sectionCount) //When it finds a new max value then the hole loop will continue until that value
									sectionCount = val;
							}
							
						}
					}
					
					newSections.add(new Section(newShifts));
					newShifts = new LinkedList<Shift>();
				}
				
				subjects.set(subjects.indexOf(subject), new Subject(newSections,subjectNameToEdit,subject.waiting));
			}
		}
		
		try {
			mapper.writeValue(subjectSaveFile, subjects);
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
		try {
			changeScene(e,MAIN_WINDOW, "Time Table Calculator");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}
	
	public void changeScene(ActionEvent e, String fileName, String windowTitle) throws IOException {
		
		loader = new FXMLLoader(getClass().getResource(fileName));
		loader.setController(this);
		Pane root = loader.load();
        currentScene = new Scene(root);
        currentStage.setScene(currentScene);
        currentStage.setTitle(windowTitle);
        
        loadRightSubjectVBox();
	}

	public void changeScene(String fileName, String windowTitle) throws IOException {
		
		loader = new FXMLLoader(getClass().getResource(fileName));
		loader.setController(this);
		Pane root = loader.load();
		currentStage = new Stage();
		currentScene = new Scene(root);
		currentStage.setScene(currentScene);
		currentStage.setTitle(windowTitle);
		currentStage.setResizable(false);
		
		loadRightSubjectVBox();
		
	}
	
	@Override
	//Method call for every loader.load() 
	public void initialize(URL arg0, ResourceBundle arg1) {
		
	}
	
	@FXML
	private void changeSaveFile(ActionEvent e) throws StreamReadException, DatabindException, IOException {
		FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON File (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);

        File selectedFile = fileChooser.showOpenDialog(currentStage);
        
        if (selectedFile != null) {
            // Accede al archivo seleccionado aquí
            SUBJECT_SAVE_FILE = selectedFile.getAbsolutePath();
            loadRightSubjectVBox();
            updateCombinationsText();
        }
	}
	
	@FXML
	private void createNewSaveFile(ActionEvent e){
		FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON File (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);
        
        File selectedFile = fileChooser.showSaveDialog(currentStage);

        if (selectedFile != null) {
            // Accede al archivo seleccionado aquí
        	System.out.println("Archivo guardado en: " + selectedFile.getAbsolutePath());
            SUBJECT_SAVE_FILE = selectedFile.getAbsolutePath();
        }
	}
	
	@FXML
	private void showAboutScreen(ActionEvent e) {
		
		loader = new FXMLLoader(getClass().getResource(ABOUT_WINDOW));
		loader.setController(this);
		Pane root = null;
		try {
			root = loader.load();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		helpStage = new Stage();
		helpScene = new Scene(root);
		helpStage.setScene(helpScene);
		
		
		helpStage.setTitle("About Time Table Calculator");
		helpStage.setResizable(false);
		helpStage.show();
		
	}
	
	@FXML
	private void openBrowser(ActionEvent ev) {
		String url = "https://github.com/IgnacioCode"; // Reemplaza con el enlace que desees abrir
		System.out.println("ENTRE");
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
	}
	
	private void updateCombinationsText() {
		List<Subject> subjects = loadSaveFile();
		for (Iterator<Subject> it = subjects.iterator(); it.hasNext();) {
			Subject subject = (Subject) it.next();
			if(subject.waiting)
				it.remove();
		}
		
		if(subjects.isEmpty()){
			combNumberLabel.setText("No combinations");
			return;
		}
		
		resultsList = Timetable.calculateTimeTables(subjects);
		int cantResults = resultsList.size();
		if(cantResults!=0)
			combNumberLabel.setText(cantResults + " Combinations");
		else
			combNumberLabel.setText("No combinations");
		currentStage.show();
	} 

	private void setUpTable(Timetable timetable, TableView<WeekRow> tableView) {
		
		ObservableList<WeekRow> weeksToShow = FXCollections.observableArrayList(
	    		new WeekRow("Morning"),
	    		new WeekRow("Afternoon"),
	    		new WeekRow("Evening")
	    		);
		
		ObservableList<TableColumn<WeekRow,?>> collumns = tableView.getColumns();
		
		collumns.get(0).setCellValueFactory(new PropertyValueFactory<>("monday"));
		collumns.get(1).setCellValueFactory(new PropertyValueFactory<>("tuesday"));
		collumns.get(2).setCellValueFactory(new PropertyValueFactory<>("wednesday"));
		collumns.get(3).setCellValueFactory(new PropertyValueFactory<>("thursday"));
		collumns.get(4).setCellValueFactory(new PropertyValueFactory<>("friday"));
		collumns.get(5).setCellValueFactory(new PropertyValueFactory<>("saturday"));
		
		int timeIndex = 0;
		
		for (Section section : timetable.getSectionsList()) {
			
			int sectionIndex = timetable.getSectionsList().indexOf(section);
			
			for (Shift shift : section.getShifts()) {
				
				switch (shift.time) {
				case "Morning":
					timeIndex = 0;
					break;
				case "Afternoon":
					timeIndex = 1;
					break;
				case "Evening":
					timeIndex = 2;
					break;
				}
				
				switch (shift.day) {
				case "Monday":
					weeksToShow.get(timeIndex).monday = timetable.getNamesList().get(sectionIndex);
					break;
				case "Tuesday":
					weeksToShow.get(timeIndex).tuesday = timetable.getNamesList().get(sectionIndex);
					break;
				case "Wednesday":
					weeksToShow.get(timeIndex).wednesday = timetable.getNamesList().get(sectionIndex);
					break;
				case "Thursday":
					weeksToShow.get(timeIndex).thursday = timetable.getNamesList().get(sectionIndex);
					break;
				case "Friday":
					weeksToShow.get(timeIndex).friday = timetable.getNamesList().get(sectionIndex);
					break;
				case "Saturday":
					weeksToShow.get(timeIndex).saturday = timetable.getNamesList().get(sectionIndex);
					break;
				}
				
			}
			
		}
		
		
		tableView.setFixedCellSize(58);
		
		tableView.setItems(weeksToShow);
		
	}
	

	private void setUpCleanTable(Timetable timetable, TableView<WeekRow> tableView) {
		
		ObservableList<WeekRow> weeksToShow = FXCollections.observableArrayList(
	    		new WeekRow("Morning"),
	    		new WeekRow("Afternoon"),
	    		new WeekRow("Evening")
	    		);
		
		tableView.setFixedCellSize(58);
		
		tableView.setItems(weeksToShow);
	}
	
	private void loadRightSubjectVBox() throws IOException, StreamReadException, DatabindException {
		
		List<Subject> subjects = loadSaveFile();
		
		leftVBox.getChildren().clear();
		rightVBox.getChildren().clear();
		
		
		for (Subject subject : subjects) {
			
			HBox newSubjectHBox = new HBox();
			Label subjectNameHBox = new Label();
			Button addOrganizeButton = new Button();
			Button editSubjectButton = new Button();
			Button deleteSubjectButton = new Button();
			
			newSubjectHBox.setPrefHeight(HBOX_HEIGHT);
			newSubjectHBox.setPrefWidth(HBOX_WIDTH-15);
			
			subjectNameHBox.setPrefWidth(SUBJECT_LABEL_WIDTH-50);
			subjectNameHBox.setPrefHeight(SUBJECT_LABEL_HEIGHT);
			subjectNameHBox.setFont(Font.font("Arial",FontWeight.BOLD,NAME_FONT_SIZE));
			subjectNameHBox.setAlignment(Pos.CENTER);
			
			addOrganizeButton.setPrefHeight(EDIT_BUTTON_HEIGHT);
			addOrganizeButton.setPrefWidth(EDIT_BUTTON_WIDTH);
			addOrganizeButton.setOnAction(event->{addSubjectToCalculateList(event);});
			addOrganizeButton.setId(subject.name);
			editSubjectButton.setPrefHeight(EDIT_BUTTON_HEIGHT);
			editSubjectButton.setPrefWidth(EDIT_BUTTON_WIDTH);
			editSubjectButton.setOnAction(event->{editSubject(event);});
			editSubjectButton.setId(subject.name);
			deleteSubjectButton.setPrefHeight(EDIT_BUTTON_HEIGHT);
			deleteSubjectButton.setPrefWidth(EDIT_BUTTON_WIDTH);
			deleteSubjectButton.setOnAction(event ->{deleteSubject(event);});
			deleteSubjectButton.setId(subject.name);
			
			subjectNameHBox.setId(subject.name);
			subjectNameHBox.setText(subject.name);
			addOrganizeButton.setText("Add");
			editSubjectButton.setText("Edit");
			deleteSubjectButton.setText("Delete");
			
			newSubjectHBox.getChildren().add(subjectNameHBox);
			newSubjectHBox.getChildren().add(addOrganizeButton);
			newSubjectHBox.getChildren().add(editSubjectButton);
			newSubjectHBox.getChildren().add(deleteSubjectButton);
			
			
			
			if(subject.waiting) {
				leftVBox.getChildren().add(newSubjectHBox);
			}
			else {
				addOrganizeButton.setText("Remove");
				addOrganizeButton.setOnAction(event->{removeSubject(event);});
				rightVBox.getChildren().add(newSubjectHBox);
			}
			
		}
		
		updateCombinationsText();
		
		currentStage.show();
	}
	
	private void prepareWeekTable() {
		data = FXCollections.observableArrayList(
        		new WeekRow("Morning"),
        		new WeekRow("Afternoon"),
        		new WeekRow("Evening")
        		);
        
		daysTable.setEditable(true);
		
		monday.setCellFactory(TextFieldTableCell.forTableColumn());
		monday.setOnEditCommit(event -> {
            String newValue = event.getNewValue();
            WeekRow weekRow = event.getRowValue();
            weekRow.setMonday(newValue);
        });
		tuesday.setCellFactory(TextFieldTableCell.forTableColumn());
		tuesday.setOnEditCommit(event -> {
            String newValue = event.getNewValue();
            WeekRow weekRow = event.getRowValue();
            weekRow.setTuesday(newValue);
        });
		wednesday.setCellFactory(TextFieldTableCell.forTableColumn());
		wednesday.setOnEditCommit(event -> {
            String newValue = event.getNewValue();
            WeekRow weekRow = event.getRowValue();
            weekRow.setWednesday(newValue);
        });
		thursday.setCellFactory(TextFieldTableCell.forTableColumn());
		thursday.setOnEditCommit(event -> {
            String newValue = event.getNewValue();
            WeekRow weekRow = event.getRowValue();
            weekRow.setThursday(newValue);
        });
		friday.setCellFactory(TextFieldTableCell.forTableColumn());
		friday.setOnEditCommit(event -> {
            String newValue = event.getNewValue();
            WeekRow weekRow = event.getRowValue();
            weekRow.setFriday(newValue);
        });
		saturday.setCellFactory(TextFieldTableCell.forTableColumn());
		saturday.setOnEditCommit(event -> {
            String newValue = event.getNewValue();
            WeekRow weekRow = event.getRowValue();
            weekRow.setSaturday(newValue);
        });
		
		daysTable.setFixedCellSize(67);
		
		monday.setCellValueFactory(new PropertyValueFactory<WeekRow,String>("monday"));
        tuesday.setCellValueFactory(new PropertyValueFactory<WeekRow,String>("tuesday"));
        wednesday.setCellValueFactory(new PropertyValueFactory<WeekRow,String>("wednesday"));
        thursday.setCellValueFactory(new PropertyValueFactory<WeekRow,String>("thursday"));
        friday.setCellValueFactory(new PropertyValueFactory<WeekRow,String>("friday"));
        saturday.setCellValueFactory(new PropertyValueFactory<WeekRow,String>("saturday"));
        
        daysTable.setItems(data);
	}

	private void updateWeekRowCollumns(Subject subjectToEdit) {
		
		int timeIndex = 0;
		int sectionNumber = 1;
		
		for (Section section : subjectToEdit.getSections()) {

			for (Shift shift : section.getShifts()) {
				
				switch (shift.time) {
				case "Morning":
					timeIndex = 0;
					break;
				case "Afternoon":
					timeIndex = 1;
					break;
				case "Evening":
					timeIndex = 2;
					break;
				}
					
				switch (shift.day) {
				case "Monday":
					if(!data.get(timeIndex).monday.isEmpty())
						data.get(timeIndex).monday+=","+Integer.toString(sectionNumber);
					else
						data.get(timeIndex).monday = Integer.toString(sectionNumber);
					break;
				case "Tuesday":
					if(!data.get(timeIndex).tuesday.isEmpty())
						data.get(timeIndex).tuesday+=","+Integer.toString(sectionNumber);
					else
						data.get(timeIndex).tuesday = Integer.toString(sectionNumber);
					break;
				case "Wednesday":
					if(!data.get(timeIndex).wednesday.isEmpty())
						data.get(timeIndex).wednesday+=","+Integer.toString(sectionNumber);
					else
						data.get(timeIndex).wednesday = Integer.toString(sectionNumber);
					break;
				case "Thursday":
					if(!data.get(timeIndex).thursday.isEmpty())
						data.get(timeIndex).thursday+=","+Integer.toString(sectionNumber);
					else
						data.get(timeIndex).thursday=Integer.toString(sectionNumber);
					break;
				case "Friday":
					if(!data.get(timeIndex).friday.isEmpty())
						data.get(timeIndex).friday+=","+Integer.toString(sectionNumber);
					else
						data.get(timeIndex).friday = Integer.toString(sectionNumber);
					break;
				case "Saturday":
					if(!data.get(timeIndex).saturday.isEmpty())
						data.get(timeIndex).saturday+=","+Integer.toString(sectionNumber);
					else
						data.get(timeIndex).saturday = Integer.toString(sectionNumber);
					break;
				}
				
			}
			sectionNumber++;
		}
		
	}
	
	private List<Subject> loadSaveFile() {
		File subjectSaveFile = new File(SUBJECT_SAVE_FILE);
		ObjectMapper mapper = new ObjectMapper();
		List<Subject> subjects = new LinkedList<Subject>();
		
		try {
			subjects = mapper.readValue(subjectSaveFile, new TypeReference<List<Subject>>(){});
		}catch(MismatchedInputException ex){
			
		}catch (StreamReadException e1) {
			e1.printStackTrace();
		}catch (DatabindException e1) {
			e1.printStackTrace();
		}catch (IOException e1) {
			e1.printStackTrace();
		}
		
		return subjects;
	}
	
	private void writeSaveFile(List<Subject> subjects) {
		File subjectSaveFile = new File(SUBJECT_SAVE_FILE);
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			mapper.writeValue(subjectSaveFile, subjects);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String dayNumber(int j) {
		String[] diasSemana = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

	    if (j >= 0 && j <= 5) {
	        return diasSemana[j];
	    } else {
	        throw new IllegalArgumentException("Day number is invalid");
	    }
	}
}
