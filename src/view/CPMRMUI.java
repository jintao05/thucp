package view;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.openrdf.query.algebra.evaluation.function.geosparql.SRID;

//import org.deckfour.xes.in.XParser;
//import org.deckfour.xes.in.XesXmlParser;
//import org.deckfour.xes.model.XLog;
//import org.processmining.models.heuristics.HeuristicsNet;
//import org.processmining.plugins.heuristicsnet.miner.heuristics.miner.FlexibleHeuristicsMinerPlugin;
//import org.processmining.plugins.heuristicsnet.visualizer.HeuristicsNetAnnotatedVisualization;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
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
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.CPMRM.ClinicalOrderReduction;
import model.CPMRM.DataTranslateToLDA;
import model.CPMRM.ExcelUtil;
import model.CPMRM.LDACluster;
import model.CPMRM.MileStoneMiner;


public class CPMRMUI {
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
	String clinicalOrder[][];
	ArrayList<HashSet<String>> milestone;
	MileStoneMiner mStoneMiner;
	public CPMRMUI(Stage stage, Scene scene, MenuItem menuItem, ToolBar toolBar, TabPane tabPane, Label stateLabel) {
		this.stage = stage;
		this.scene = scene;
		this.menuItem = menuItem;
		this.toolBar = toolBar;
		this.tabPane = tabPane;
		this.stateLabel = stateLabel;


		menuItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				toolBar.getItems().clear();

				Button b1 = new Button("导入数据");
				Button b2 = new Button("消解同义医嘱");
				Button b3 = new Button("挖掘关键路径");
				Button b4 = new Button("医嘱主题聚类");

				toolBar.getItems().add(b1);
				toolBar.getItems().add(b2);
				toolBar.getItems().add(b3);
				toolBar.getItems().add(b4);

				importData(b1);
				orderReduction(b2);
				orderAlignment(b3);
				orderClustering(b4);
			}
		});
	}


/*	public void importId2Item(Button button) {
		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {


			}
		});
	}*/


	public void importData(Button button)  {
		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				ExcelUtil rd = new ExcelUtil();
				try {
				    clinicalOrder = rd.csvRead("./data/CPMRM/test.csv");
				    final String[] itemHeader = { "GRBM", "F_MC","DL","F_RQ"};

				    ObservableList<ObservableList<String>> itemData = FXCollections.observableArrayList();
					for (String[] row : clinicalOrder) {
						itemData.add(FXCollections.observableArrayList(row));
					}
					TableView<ObservableList<String>> itemTV = new TableView<>();
					itemTV.setItems(itemData);
					for (int i = 0; i < itemHeader.length; i++) {
						final int curCol = i;
						final TableColumn<ObservableList<String>, String> column = new TableColumn<>(itemHeader[curCol]);
						column.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(curCol)));
						itemTV.getColumns().add(column);
					}

					Tab tab1 = null;
					for (int i = 0; i < tabPane.getTabs().size(); i++) {
						String title = tabPane.getTabs().get(i).getText();
						if (title.equals("inputData")) {
							tab1 = tabPane.getTabs().get(i);
						}
					}

					if (tab1 == null) {
						tab1 = new Tab("inputData");
					}
					tab1.setContent(itemTV);
					tabPane.getTabs().add(tab1);
					tabPane.getSelectionModel().select(tab1);
					// --
					stateLabel.setText("类别总数"+":"+rd.showDL()+"  医嘱总数"+":"+rd.showMC());

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	public void orderReduction(Button button) {
		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {



				ClinicalOrderReduction crd = new ClinicalOrderReduction();
				clinicalOrder = crd.textReduction(clinicalOrder,0.80);
				clinicalOrder = crd.semanticReduction(clinicalOrder);

				Tab tab2 = null;
				for (int i = 0; i < tabPane.getTabs().size(); i++) {
					String title = tabPane.getTabs().get(i).getText();
					if (title.equals("医嘱消解结果")) {
						tab2 = tabPane.getTabs().get(i);
					}
				}

				if (tab2 == null) {
					tab2 = new Tab("医嘱消解结果");
				}
				Text resulText = new Text(1000,1000,"\n"+crd.getResultBuffer());

				tab2.setContent(resulText);
				tabPane.getTabs().add(tab2);
				tabPane.getSelectionModel().select(tab2);


			}
		});
	}

	public void orderAlignment(Button button) {
		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {


				try {
					mStoneMiner = new MileStoneMiner(clinicalOrder);
					milestone = new ArrayList<HashSet<String>>();
				    int stepNumber = 2;
				    StringBuffer result = new StringBuffer();
				    milestone = mStoneMiner.getMileStone(stepNumber,result);

				    Tab tab3 = null;
					for (int i = 0; i < tabPane.getTabs().size(); i++) {
						String title = tabPane.getTabs().get(i).getText();
						if (title.equals("关键路径挖掘结果")) {
							tab3 = tabPane.getTabs().get(i);
						}
					}

					if (tab3 == null) {
						tab3 = new Tab("关键路径挖掘结果");
					}

					Text resulText = new Text(1000,1000,result.toString());
					tab3.setContent(resulText);
					tabPane.getTabs().add(tab3);
					tabPane.getSelectionModel().select(tab3);


				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
	}

	public void orderClustering(Button button) {
		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Tab tab4 = null;
				for (int i = 0; i < tabPane.getTabs().size(); i++) {
					String title = tabPane.getTabs().get(i).getText();
					if (title.equals("主题聚类结果")) {
						tab4 = tabPane.getTabs().get(i);
					}
				}

				if (tab4 == null) {
					tab4 = new Tab("主题聚类结果");
				}

				Text resulText=null ;
				if (milestone.size() == 0) {
					resulText = new Text(1000,1000,"没有找到关键路径，CPMRM已结束！");
					System.out.println("没有找到关键路径，CPMRM已结束！");
				}
				else {
					HashMap<String, Integer> c2iHashMap = mStoneMiner.getc2iHashMap();
					HashMap<Integer, String> i2cHashMap = mStoneMiner.geti2cHashMap();
					HashMap<Integer, String> i2categHashMap = mStoneMiner.geti2categHashMap();
					String mileStoneInputFile = mStoneMiner.getOutFileName();
					DataTranslateToLDA dToLDA;
					try {
						dToLDA = new DataTranslateToLDA(mileStoneInputFile,milestone);
						LDACluster lda = new LDACluster(dToLDA.getStepNumber(),"./data/CPMRM/orders/eventsInEachStep.txt");
					    lda.cluster(i2cHashMap);

					    resulText = new Text(1000,1000, lda.getResult().toString());



						stateLabel.setText("CPMRM 已完成!");

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}



				ScrollPane s1 = new ScrollPane();
				s1.setContent(resulText);
				tab4.setContent(s1);


				tabPane.getTabs().add(tab4);
				tabPane.getSelectionModel().select(tab4);

			}
		});
	}

}
