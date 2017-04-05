package org;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.BorderExtent;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.PropertyTemplate;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

//this class prints Excel file
public class PrintExcelEndSem extends PrintExcel{
	
	public int dostuff(int i, int j, Sheet sheet1, ArrayList<Room> rooms, Workbook wb, int flag2, Row row1, int flag) 
	{
		merge(i, j, sheet1, rooms);
		Font font = wb.createFont();
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		CellStyle temp_style = wb.createCellStyle();
		temp_style.setFont(font);
		temp_style.setAlignment(CellStyle.ALIGN_CENTER_SELECTION);

		printRooms(i, j, sheet1, rooms);
		flag2 = 1;
		if (flag == 1) {
			row1.createCell(j + 1).setCellValue(" 09:00 - 12:00 ");

		} else if (flag == 2) {
			row1.createCell(j + 1).setCellValue(" 14:00 - 17:00 ");
		}
			//
//		} else if (flag == 3) {
//			row1.createCell(j + 1).setCellValue(" 14:00 - 16:00 ");
//
//		} else if (flag == 4) {
//			row1.createCell(j + 1).setCellValue(" 16:30 - 18:30 ");
//
//		}
		row1.createCell(j + 4).setCellValue(" CEP Rooms ");
		row1.getCell(j + 4).setCellStyle(temp_style);

		// creating another style to enlarge font
		CellStyle temp_style_2 = wb.createCellStyle();
		temp_style_2.cloneStyleFrom(temp_style);// copying similar style
												// parameters from first style
		Font font1 = wb.createFont();
		font1.setFontHeightInPoints((short) 18);// size-18
		temp_style_2.setFont(font1);
		row1.getCell(j + 1).setCellStyle(temp_style_2);
		return flag2;// can't modify flag2 in function as Java is pass by
						// value,so returning
	}
	
	public void createExcelSheet(TimeTable TT) throws ClassNotFoundException, DAOException, SQLException, IOException {
		// mapping numeric code to Batches
		Map<Integer, String> batch_id_name = GeneralDAO.getBatchProgram();
		
		// export to Excel Format
		// making workbook and sheet
		XSSFWorkbook wb = new XSSFWorkbook();
		// String safeName = WorkbookUtil.createSafeSheetName("Main Result"); //
		// returns " O'Brien's sales "
		XSSFSheet sheet1 = wb.createSheet("a");

		XSSFCellStyle[] style = new XSSFCellStyle[12];
		// styles

		// creating cell styles
		for (int i = 0; i < style.length; i++)
			style[i] = wb.createCellStyle();

		// BTech Ist
		style[0].setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
		style[0].setFillBackgroundColor(IndexedColors.PALE_BLUE.getIndex());

		// B.Tech IInd
		style[1].setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
		style[1].setFillBackgroundColor(IndexedColors.LIGHT_YELLOW.getIndex());

		// B.Tech IIIrd
		style[2].setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
		style[2].setFillBackgroundColor(IndexedColors.SKY_BLUE.getIndex());

		// B.Tech IVth
		style[3].setFillForegroundColor(IndexedColors.TAN.getIndex());
		style[3].setFillBackgroundColor(IndexedColors.TAN.getIndex());

		// M.Sc(IT) Ist
		style[4].setFillForegroundColor(IndexedColors.ORANGE.getIndex());
		style[4].setFillBackgroundColor(IndexedColors.ORANGE.getIndex());

		// M.Sc(IT) IInd
		style[5].setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
		style[5].setFillBackgroundColor(IndexedColors.LIGHT_GREEN.getIndex());

		// M.Sc(IT) ARD Ist
		style[6].setFillForegroundColor(IndexedColors.YELLOW.getIndex());
		style[6].setFillBackgroundColor(IndexedColors.YELLOW.getIndex());

		// M.Sc(IT) ARD IInd
		style[7].setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
		style[7].setFillBackgroundColor(IndexedColors.LIGHT_BLUE.getIndex());

		// M.Tech Ist
		style[8].setFillForegroundColor(IndexedColors.ROSE.getIndex());
		style[8].setFillBackgroundColor(IndexedColors.ROSE.getIndex());

		// M.Tech IInd
		style[9].setFillForegroundColor(IndexedColors.LAVENDER.getIndex());
		style[9].setFillBackgroundColor(IndexedColors.LAVENDER.getIndex());

		// M.Des Ist
		style[10].setFillForegroundColor(IndexedColors.GREEN.getIndex());
		style[10].setFillBackgroundColor(IndexedColors.GREEN.getIndex());

		// M.Des IInd
		style[11].setFillForegroundColor(IndexedColors.RED.getIndex());
		style[11].setFillBackgroundColor(IndexedColors.RED.getIndex());

		for (int i = 0; i < style.length; i++)
			style[i].setFillPattern(CellStyle.BIG_SPOTS);
		// main work
		int i = 7;
		int j = 2;

		int curr_row = i + 2;
		Map<Integer, StoreTT> temp_store = TT.getStore();//slot(1-7)-->StoreTT

		// this loop runs in pair->1 run for (Slot1,Slot2),1 run for
		// (Slot3,Slot4),....
		for (int k = 0; k < temp_store.size();k++)
		{
			Map<Integer, Integer> line = new HashMap<>();// batch_id and row
															// number in Excel
			Map<Integer, Integer> range = new HashMap<>();// batch_id and range
															// in Excel
			curr_row+=2;
			int slot1 = k + 1;
			int slot2 = k + 2;
			int slot2_flag = 0;

			// **********checking for first Slot************
			TimeInterval t1 = temp_store.get(slot1).getT1();
			TimeInterval t2 = temp_store.get(slot1).getT2();

			// check T1
			Set<Course> set1 = new HashSet<>();
			for (ArrayList<OccupationData> od : t1.getMap().values()) {
				for (int hh = 0; hh < od.size(); hh++) {
					set1.add(od.get(hh).getCourse());
				}
			}

			// check T2
			curr_row += 1;
			Set<Course> set2 = new HashSet<>();
			for (ArrayList<OccupationData> od : t2.getMap().values()) {
				for (int hh = 0; hh < od.size(); hh++) {
					set2.add(od.get(hh).getCourse());
				}
			}
//			// **********************checking for Slot2***************
//			Set<Course> set1_2 = new HashSet<>();
//			Set<Course> set2_2 = new HashSet<>();
//			TimeInterval t1_2 = null;
//			TimeInterval t2_2 = null;
//
//			if (slot2 <= temp_store.size()) {
//				slot2_flag = 1;
//				t1_2 = temp_store.get(slot2).getT1();
//				t2_2 = temp_store.get(slot2).getT2();
//				// check T1
//				curr_row += 1;
//				for (ArrayList<OccupationData> od : t1_2.getMap().values()) {
//					for (int hh = 0; hh < od.size(); hh++) {
//						set1_2.add(od.get(hh).getCourse());
//					}
//				}
//
//				// check T2
//				curr_row += 1;
//				for (ArrayList<OccupationData> od : t2_2.getMap().values()) {
//					for (int hh = 0; hh < od.size(); hh++) {
//						set2_2.add(od.get(hh).getCourse());
//					}
//			}
//			}
			// calculating max number of rows required for each batch(Programme)
			for (int batch_id = 1; batch_id < batch_id_name.size(); batch_id++) {
				int max = 0;
				int counter = 0;
				for (Course course : set1) {
					if (Integer.parseInt(course.getBatch()) == batch_id)
						counter++;
				}
				max = Math.max(max, counter);
				counter = 0;
				for (Course course : set2) {
					if (Integer.parseInt(course.getBatch()) == batch_id)
						counter++;
				}
				max = Math.max(max, counter);
//				counter = 0;
//				for (Course course : set1_2) {
//					if (Integer.parseInt(course.getBatch()) == batch_id)
//						counter++;
//				}
//				max = Math.max(max, counter);
//				counter = 0;
//				for (Course course : set2_2) {
//					if (Integer.parseInt(course.getBatch()) == batch_id)
//						counter++;
//				}
//				max = Math.max(max, counter);
				if (max != 0)// to make sure,rows are updated only for those
								// courses which exists in these slots
				{
					line.put(batch_id, curr_row);
					curr_row = curr_row + max;
					range.put(batch_id, max);
				}

			}
			// main allocation in
			// excel######################################################

			// for time interval 1
			printInExcel(batch_id_name, line, range, sheet1, j, style, set1, t1, wb, 1);
			// for time interval 2

			printInExcel(batch_id_name, line, range, sheet1, j + 9, style, set2, t2, wb, 2);
//			if (slot2_flag == 1) {
//				// for time interval 1_2
//				printInExcel(batch_id_name, line, range, sheet1, j, style, set1_2, t1_2, wb, 3);
//				// for time interval 1_2
//				printInExcel(batch_id_name, line, range, sheet1, j + 9, style, set2_2, t2_2, wb, 4);
//			}
		}
		XSSFCellStyle cust_style = wb.createCellStyle();
		cust_style.setFillBackgroundColor(new XSSFColor(new java.awt.Color(0, 255, 0)));
		cust_style.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
		// auto-resizing columns
		for (int ii = -1; ii < 40; ii++)
			sheet1.autoSizeColumn(j + ii);

		// saving output to file
		sheet1.getPrintSetup().setLandscape(true);
		String fileName = "workbook.xlsx";
		//String filePath = "F:\\exam_timetable\\src\\data\\output\\";
		String filePath = "C:\\Users\\ashwani tanwar\\workspace\\Exam_TimeTable_Scheduler_bySlots_Web_Project\\src\\data\\output\\";
		
		FileOutputStream fileOut = new FileOutputStream(filePath + fileName);
		wb.write(fileOut);
		fileOut.close();
		System.out.println("over");
	}

}