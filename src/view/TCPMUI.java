package view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Optional;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToolBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.MalletLDA;
import model.TCPMParameters;

public class TCPMUI {
	Stage stage;
	Scene scene;
	MenuItem menuItem;
	ToolBar toolBar;
	TabPane tabPane;
	Label stateLabel;

	File id2itemFile;
	File pditemFile;
	ArrayList<String[]> id2itemArray;
	ArrayList<String[]> pditemArray;

	public TCPMParameters params = new TCPMParameters();

	public TCPMUI(Stage stage, Scene scene, MenuItem menuItem, ToolBar toolBar, TabPane tabPane, Label stateLabel) {
		this.stage = stage;
		this.scene = scene;
		this.menuItem = menuItem;
		this.toolBar = toolBar;
		this.tabPane = tabPane;
		this.stateLabel = stateLabel;

		menuItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (toolBar.getItems().size() != 0) {
					return;
				}

				Button b1 = new Button("import id2item");
				Button b2 = new Button("import pditem");
				Button b3 = new Button("set params");
				Button b4 = new Button("Run LDA!");
				Button b5 = new Button("Run PM!");
				toolBar.getItems().add(b1);
				toolBar.getItems().add(b2);
				toolBar.getItems().add(b3);
				toolBar.getItems().add(b4);
				toolBar.getItems().add(b5);

				importId2Item(b1);
				importPdItem(b2);
				setParams(b3);
				runLDA(b4);
			}
		});
	}

	public void importId2Item(Button button) {
		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FileChooser id2itemFC = new FileChooser();
				id2itemFC.setTitle("Choose id2item data");
				id2itemFC.setInitialDirectory(new File("./data/"));
				id2itemFile = id2itemFC.showOpenDialog(stage);

				final String[] id2itemHeader = { "id", "item" };

				id2itemArray = new ArrayList<String[]>();
				try {
					id2itemArray = readId2ItemFile(id2itemFile);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				ObservableList<ObservableList<String>> id2itemData = FXCollections.observableArrayList();
				for (String[] row : id2itemArray) {
					id2itemData.add(FXCollections.observableArrayList(row));
				}

				TableView<ObservableList<String>> id2itemTV = new TableView<>();
				id2itemTV.setItems(id2itemData);
				for (int i = 0; i < id2itemHeader.length; i++) {
					final int curCol = i;
					final TableColumn<ObservableList<String>, String> column = new TableColumn<>(id2itemHeader[curCol]);
					column.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(curCol)));
					id2itemTV.getColumns().add(column);
				}

				Tab tab1 = null;
				for (int i = 0; i < tabPane.getTabs().size(); i++) {
					String title = tabPane.getTabs().get(i).getText();
					if (title.equals("id2item")) {
						tab1 = tabPane.getTabs().get(i);
					}
				}

				if (tab1 == null) {
					tab1 = new Tab("id2item");
				}
				tab1.setContent(id2itemTV);
				tabPane.getTabs().add(tab1);
				tabPane.getSelectionModel().select(tab1);

				// --
				stateLabel.setText("id2item row size: " + id2itemData.size());
			}
		});
	}

	public void importPdItem(Button button) {
		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FileChooser pditemFC = new FileChooser();
				pditemFC.setTitle("Choose pditem file");
				pditemFC.setInitialDirectory(new File("./data/"));
				pditemFile = pditemFC.showOpenDialog(stage);

				final String[] pditemHeader = { "traceID", "date", "activities" };

				pditemArray = new ArrayList<String[]>();
				try {
					pditemArray = readPdItemFile(pditemFile);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				ObservableList<ObservableList<String>> pditemData = FXCollections.observableArrayList();
				for (String[] row : pditemArray) {
					pditemData.add(FXCollections.observableArrayList(row));
				}

				TableView<ObservableList<String>> pditemTV = new TableView<>();
				pditemTV.setItems(pditemData);
				for (int i = 0; i < pditemHeader.length; i++) {
					final int curCol = i;
					final TableColumn<ObservableList<String>, String> column = new TableColumn<>(pditemHeader[curCol]);
					column.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(curCol)));
					pditemTV.getColumns().add(column);
				}

				Tab tab2 = null;
				for (int i = 0; i < tabPane.getTabs().size(); i++) {
					String title = tabPane.getTabs().get(i).getText();
					if (title.equals("pditem")) {
						tab2 = tabPane.getTabs().get(i);
					}
				}

				if (tab2 == null) {
					tab2 = new Tab("pditem");
				}
				tab2.setContent(pditemTV);
				tabPane.getTabs().add(tab2);
				tabPane.getSelectionModel().select(tab2);

				// --
				stateLabel.setText(stateLabel.getText() + "\t" + "pditem row size: " + pditemData.size());
			}
		});
	}

	public void setParams(Button button) {
		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				TextInputDialog dialog = new TextInputDialog("");
				dialog.setTitle("Set the parameters");
				GridPane grid = new GridPane();
				grid.setHgap(10);
				grid.setVgap(10);
				grid.setPadding(new Insets(20, 150, 10, 10));

				TextField KTF = new TextField("3");
				//KTF.setPromptText("K");
				KTF.setPrefColumnCount(5);
				TextField alphaTF = new TextField("1.0");
				//alphaTF.setPromptText("1.0");
				alphaTF.setPrefColumnCount(5);
				TextField betaTF = new TextField("0.01");
				//betaTF.setPromptText("0.01");
				betaTF.setPrefColumnCount(5);

				grid.add(new Label("Parameters for LDA:"), 0, 0, 5, 1);		//row 0
				grid.add(new Label("K: "), 0, 1);		//row 1
				grid.add(KTF, 1, 1);
				grid.add(new Label("alpha:"), 2, 1);
				grid.add(alphaTF, 3, 1);
				grid.add(new Label("beta:"), 4, 1);
				grid.add(betaTF, 5, 1);

				TextField iterationTF = new TextField("2000");
				//iterationTF.setPromptText("2000");
				iterationTF.setPrefColumnCount(5);
				TextField topKTF = new TextField("50");
				//topKTF.setPromptText("50");
				topKTF.setPrefColumnCount(5);
				TextField threadNumTF = new TextField("2");
				//threadNumTF.setPromptText("2");
				threadNumTF.setPrefColumnCount(5);

				grid.add(new Label("iteration: "), 0, 2);	//row 2
				grid.add(iterationTF, 1, 2);
				grid.add(new Label("topK:"), 2, 2);
				grid.add(topKTF, 3, 2);
				grid.add(new Label("threadNum:"), 4, 2);
				grid.add(threadNumTF, 5, 2);

				Separator sepa = new Separator();		//row 3
				sepa.setOrientation(Orientation.HORIZONTAL);
				//sepa.setHalignment(HPos.CENTER);
				grid.add(sepa, 0, 3, 7, 1);

				grid.add(new Label("Parameters for PM:"), 0, 4, 5, 1);		//row 4

				TextField thrTF = new TextField("0.5");
				//thrTF.setPromptText("0.5");
				thrTF.setPrefColumnCount(5);
				int mf = 0;
				if(pditemArray != null && pditemArray.size() > 0) {
					mf = (int) (pditemArray.size() * 0.01);
				}
				TextField minFreqTF = new TextField(mf + "");
				//minFreqTF.setPromptText(mf + "");
				minFreqTF.setPrefColumnCount(5);

				grid.add(new Label("thr: "), 0, 5);	//row 5
				grid.add(thrTF, 1, 5);
				grid.add(new Label("minFeq:"), 2, 5);
				grid.add(minFreqTF, 3, 5);

				dialog.getDialogPane().setContent(grid);

				// Request focus on the username field by default.
				Platform.runLater(() -> KTF.requestFocus());

				dialog.showAndWait().ifPresent(response -> {
					params.K = Integer.parseInt(KTF.getText());
					params.alpha = Double.parseDouble(alphaTF.getText());
					params.beta = Double.parseDouble(betaTF.getText());
					params.topK = Integer.parseInt(topKTF.getText());
					params.iteration = Integer.parseInt(iterationTF.getText());
					params.threadNum = Integer.parseInt(threadNumTF.getText());
					params.thr = Double.parseDouble(thrTF.getText());
					params.minFreq = Integer.parseInt(minFreqTF.getText());
				});
			}
		});
	}

	public void runLDA(Button button) {
		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				MalletLDA mlda = new MalletLDA(params);
				try {
					mlda.runLDA(id2itemFile, pditemFile);
					String[][] topWords = mlda.topWords;
					int rowNum = topWords.length; // topic num
					int colNum = topWords[0].length;
					// transpose for UI, and insert an index column into the
					// UI table
					String[][] topWords4UI = new String[colNum][rowNum + 1];
					for (int i = 0; i < colNum; i++) {
						for (int j = 0; j < rowNum + 1; j++) {
							if (j == 0) {
								topWords4UI[i][0] = i + "";
							} else {
								topWords4UI[i][j] = topWords[j - 1][i];
							}
						}
					}

					String[] topWordsHeader = new String[rowNum + 1];
					topWordsHeader[0] = "";
					for (int i = 0; i < rowNum; i++) {
						topWordsHeader[i + 1] = "Topic " + i;
					}

					ObservableList<ObservableList<String>> topWordsData = FXCollections.observableArrayList();
					for (String[] row : topWords4UI) {
						topWordsData.add(FXCollections.observableArrayList(row));
					}

					TableView<ObservableList<String>> topWordsTV = new TableView<>();
					topWordsTV.setItems(topWordsData);
					for (int i = 0; i < topWordsHeader.length; i++) {
						final int curCol = i;
						final TableColumn<ObservableList<String>, String> column = new TableColumn<>(
								topWordsHeader[curCol]);
						column.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(curCol)));
						topWordsTV.getColumns().add(column);
					}

					Tab tab3 = null;
					for (int i = 0; i < tabPane.getTabs().size(); i++) {
						String title = tabPane.getTabs().get(i).getText();
						if (title.equals("topwords")) {
							tab3 = tabPane.getTabs().get(i);
						}
					}

					if (tab3 == null) {
						tab3 = new Tab("topwords");
					}
					tab3.setContent(topWordsTV);
					tabPane.getTabs().add(tab3);
					tabPane.getSelectionModel().select(tab3);

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	public ArrayList<String[]> readId2ItemFile(File file) throws Exception {
		ArrayList<String[]> data = new ArrayList<String[]>();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		String currentLine;
		while ((currentLine = br.readLine()) != null) {
			String[] strs = currentLine.split("\t");
			data.add(strs);
		}
		br.close();

		return data;
	}

	public ArrayList<String[]> readPdItemFile(File file) throws Exception {
		ArrayList<String[]> data = new ArrayList<String[]>();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		String currentLine;
		while ((currentLine = br.readLine()) != null) {
			String[] strs = currentLine.split(",");
			if (strs.length < 3) {
				continue;
			}
			String traceID = strs[0].split("@")[0];
			String date = strs[0].split("@")[1];
			String[] ss = new String[] { traceID, date, strs[2] };
			data.add(ss);
		}
		br.close();

		return data;
	}
}
