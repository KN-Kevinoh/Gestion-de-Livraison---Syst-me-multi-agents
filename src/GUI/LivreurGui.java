package GUI;

import agents.LivreurAgent;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LivreurGui extends Application {
	
	public LivreurAgent livreurAgent;
	protected ObservableList<String> observableList;
	protected AgentContainer agentContainer;
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		startContainer();
		
		primaryStage.setTitle("Livreur");
		HBox hBox = new HBox();
		Label label = new Label("Agent name:");
		TextField textFieldAgentName = new TextField();
		Button buttonDeployer = new Button("Déployer");
		hBox.getChildren().addAll(label, textFieldAgentName, buttonDeployer);
		hBox.setPadding(new Insets(10));
		hBox.setSpacing(10);
		
		hBox.setStyle("-fx-background-color: #CC2213;");
		
		BorderPane borderPane = new BorderPane();
		VBox vBox = new VBox();
		observableList = FXCollections.observableArrayList();
		ListView<String> listView = new ListView<String>(observableList);
		vBox.getChildren().add(listView);
		borderPane.setCenter(vBox);
		borderPane.setTop(hBox);
		Scene scene = new Scene(borderPane, 400, 400);
		primaryStage.setScene(scene);
		primaryStage.show();
		
		buttonDeployer.setOnAction((evt) -> {
			if(!textFieldAgentName.getText().isEmpty()) {
				AgentController agentController;
				String name = textFieldAgentName.getText();
				textFieldAgentName.setText("");
				try {
					agentController = agentContainer
							.createNewAgent(name, "agents.LivreurAgent", new Object[] {this});
					agentController.start();
				} catch (StaleProxyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	private void startContainer() throws Exception {
		Runtime runtime = Runtime.instance();
		ProfileImpl profileImpl = new ProfileImpl();
		profileImpl.setParameter(ProfileImpl.MAIN_HOST, "localhost");
		agentContainer = runtime.createAgentContainer(profileImpl);
		
		agentContainer.start();
	}

	public void logMessage(String message) {
		Platform.runLater(() -> { //Pour éviter les problème de thread
			observableList.add(message);
		});
	}
}
