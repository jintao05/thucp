package view;

import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
	private final Label sysMenuLabel = new Label("Using System Menu");

	public Parent createContent() {
		final String os = System.getProperty("os.name");
		VBox vbox = new VBox(20);
		vbox.setPrefSize(800, 600);
		final Label outputLabel = new Label();
		final MenuBar menuBar = new MenuBar();

		// Options->Submenu 1 submenu
		MenuItem menu11 = new MenuItem("LDA+PM");
		MenuItem menu12 = new MenuItem("SS-LDA+PM");
		MenuItem menu13 = new MenuItem("SSS-LDA+PM+SC");

		// Options menu
		Menu menu1 = new Menu("TCPM");
		menu1.getItems().addAll(menu11, menu12, menu13);
		menuBar.getMenus().addAll(menu1);

		vbox.getChildren().addAll(menuBar);
		return vbox;
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			VBox vbox = new VBox();
			vbox.setPrefSize(800, 600);
			vbox.setSpacing(5);
			vbox.setPadding(new Insets(5, 0, 5, 0));
			final Label outputLabel = new Label();
			final MenuBar menuBar = new MenuBar();

			// Options->Submenu 1 submenu
			MenuItem menu11 = new MenuItem("LDA+PM");
			MenuItem menu12 = new MenuItem("SS-LDA+PM");
			MenuItem menu13 = new MenuItem("SSS-LDA+PM+SC");
			
			MenuItem menuwei1 = new MenuItem("LDA+CLUSTER+PM+REPLAY");

			// Options menu
			Menu menu1 = new Menu("TCPM");
			menu1.getItems().addAll(menu11, menu12, menu13);

			Menu menu2 = new Menu("Haowei");
			Menu menu3 = new Menu("Tianyu");
			Menu menu4 = new Menu("OutlierDetection");
			menu4.getItems().add(menuwei1);
			Menu menu5 = new Menu("Junjie");

			menuBar.getMenus().addAll(menu1, menu2, menu3, menu4, menu5);

			ToolBar toolbar = new ToolBar();

			Label stateLabel = new Label("");

			TabPane tabPane = new TabPane();
			vbox.getChildren().addAll(menuBar);
			vbox.getChildren().addAll(toolbar);
			vbox.getChildren().addAll(tabPane);
			vbox.getChildren().addAll(stateLabel);

			VBox.setVgrow(tabPane, Priority.ALWAYS);

			final Scene scene = new Scene(vbox);

			TCPMUI tcpmUI = new TCPMUI(primaryStage, scene, menu11, toolbar, tabPane, stateLabel);
			OutlierDetectionUI outlierDetectionUI = new OutlierDetectionUI(primaryStage, scene, menuwei1, toolbar, tabPane, stateLabel);

			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
