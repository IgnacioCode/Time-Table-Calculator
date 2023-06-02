package timetable;

import javafx.stage.Stage;

public class ProgramWindow extends javafx.application.Application{
	
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		Controller controller = new Controller();
        
		controller.changeScene("mainWindow.fxml", "Time Table Calculator");

    }
		
	public static void main(String[] args) throws Exception {

		launch();

	}
	
	
	
}
