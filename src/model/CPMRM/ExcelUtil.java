package model.CPMRM;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.plaf.basic.BasicTreeUI.TreeCancelEditingAction;

import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import cn.edu.thu.thss.iise.pojo.Sfmx;
import cn.edu.thu.thss.iise.pojo.SfmxCSVParser;


public class ExcelUtil {
	private String clinicalOrder[][];
	private ArrayList<String> listsName;
	private HashSet<String> dlNamesHashSet;
	private HashSet<String> orderNamesHashSet;
	private HashSet<String> bmNamesHashSet;
	private List<Sfmx> sfmxs;

	public ExcelUtil() {
		listsName = new ArrayList<String>();
		listsName.add("GRBM");//病人编码
		listsName.add("F_MC");//名称
		listsName.add("DL");//类型
		listsName.add("F_RQ");//日期
		dlNamesHashSet = new HashSet<String>();
		orderNamesHashSet = new HashSet<String>();
		bmNamesHashSet = new HashSet<String>();
	}

	public List<Sfmx> getSfmxs() {
		return sfmxs;
	}
	public int showDL(){
	/*	System.out.println("类别总数"+":"+ dlNamesHashSet.size());
		for (String name : dlNamesHashSet) {
//			System.out.println(name);
		}*/
		return dlNamesHashSet.size();
	}

	public int showMC(){
//		System.out.println("医嘱总数"+":"+ orderNamesHashSet.size());
//		for (String name : orderNamesHashSet) {
////			System.out.println(name);
//		}
		return orderNamesHashSet.size();
	}

	public void showGRBM(){
		System.out.println("病人总数"+":"+ bmNamesHashSet.size());
		for (String name : bmNamesHashSet) {
			System.out.println(name);
		}
	}
	public String[][] csvRead(String inputname) throws IOException, ParseException {
		sfmxs = SfmxCSVParser.readSfmxs(inputname);
		int i = 0;
		String item = "";
		clinicalOrder = new String[sfmxs.size()][listsName.size()];
		for(Sfmx record : sfmxs){
			for(int j = 0; j < listsName.size(); j++){
				if(listsName.get(j) == "GRBM") {
					item = record.getGrbm();
					clinicalOrder[i][j] = item;
					if(!bmNamesHashSet.contains(item)) bmNamesHashSet.add(item);
				}
				else if(listsName.get(j) == "F_MC") {
					item = record.getMc();
					clinicalOrder[i][j] = item;
					if(!orderNamesHashSet.contains(item)) orderNamesHashSet.add(item);
				}
				else if(listsName.get(j) == "DL") {
					item = record.getDl();
					clinicalOrder[i][j] = item;
					if(!dlNamesHashSet.contains(item)) dlNamesHashSet.add(item);
				}
				else {
					clinicalOrder[i][j] = (new SimpleDateFormat("yyyy-MM-dd")).format(record.getRq());
				}
			}
			i++;
		}
		return clinicalOrder;
	}

	public String[][] xlsRead(String inputname) {
		jxl.Workbook readwb = null;
		try {
			InputStream instream = new FileInputStream(inputname);
			readwb = Workbook.getWorkbook(instream);
	        Sheet readsheet = readwb.getSheet(0);
	        int rsColumns = readsheet.getColumns();
//	        System.out.println(rsColumns);
	        int rsRows = readsheet.getRows();
//	        System.out.println(rsRows);
	        clinicalOrder = new String[rsRows-1][6];
	        int colNum[] = new int[7];
	        int index = 0;
	        for (int i = 0; i < rsColumns; i++){
	        	String str = null;
	        	str = readsheet.getCell(i,0).getContents();
	        	if (str.equals("OEORD_Adm_DR")||str.equals("ARCIM_Desc")||str.equals("OECPR_Desc")
	        			||str.equals("OEORI_SttDat")||str.equals("OEORI_SttTim")||str.equals("OEORI_FinDate")||str.equals("OEORI_Categ_DR")){
//	        	if (str.equals("GRBM")||str.equals("F_MC")||str.equals("DL")||str.equals("F_RQ")){
	        		colNum[index++] = i;
//	        		System.out.println(i);
//	        		System.out.println(str);
	        	}
	        }
	        ArrayList<String> resArrayList = new ArrayList<String>();
	        ArrayList<String> resWordArrayList = new ArrayList<String>();
	        for (int i = 0; i < rsRows-1; i++){
	        	for (int j = 0; j < 6; j++){
	        		String str = null;
	        		if (j < 3)
	        			str = readsheet.getCell(colNum[j], i+1).getContents();
	        		else if (j == 3){
						StringBuffer s = new StringBuffer("");
						s.append( readsheet.getCell(colNum[j], i+1).getContents());
						s.append(" ");
						str =  readsheet.getCell(colNum[j+1], i+1).getContents();
						String s1[] = str.split(" ");
						if (s1.length == 2) s.append(s1[1]);
						str = s.toString();
					}
	        		else {
	        			str = readsheet.getCell(colNum[j+1], i+1).getContents();
	        		}
	        		clinicalOrder[i][j] = str;
	        	}
	        	if(!resArrayList.contains(clinicalOrder[i][5])) {
	        		resArrayList.add(clinicalOrder[i][5]);
	        		resWordArrayList.add(clinicalOrder[i][1]);
	        	}
	        }
//	        System.out.println(resArrayList.toString());
//	        System.out.println(resWordArrayList.toString());

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		} finally {
			readwb.close();
			return clinicalOrder;
		}
	}

	public void csvWrite(String res[][], String filename) throws IOException {
		File outFile = new File(filename);
		BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
		writer.write("GRBM,F_MC,DL,F_RQ");
		writer.newLine();
		for (int i = 0; i < res.length; i++) {
			StringBuffer seq = new StringBuffer(res[i][0]);

			for(int j = 1; j < res[i].length; j++) {
				seq.append(",");
				seq.append(res[i][j]);
			}
			writer.write(seq.toString());
			writer.newLine();
		}
		writer.close();
	}

	public void xlsWrite(String res[][], String filename) throws IOException,RowsExceededException,WriteException{
		WritableWorkbook wBook = Workbook.createWorkbook(new File(filename));
		WritableSheet sheet1 = wBook.createSheet("Clinic_Orders", 0);
		for (int i = 0; i < res.length; i++){
			for (int j = 0; j < res[i].length; j++){
				Label s = new Label(j,i,res[i][j]);
				sheet1.addCell(s);
			}
		}
		wBook.write();
		wBook.close();
	}

}