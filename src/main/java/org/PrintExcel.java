package org;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.PropertyTemplate;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

//this class prints Excel file
public class PrintExcel {

	// this function merges region for time duration and "CEP Rooms tag"
	public void merge(int i, int j, Sheet sheet1, ArrayList<Room> rooms) {
		sheet1.addMergedRegion(new CellRangeAddress(i, // first row (0-based)
				i + 1, // last row (0-based)
				j + 1, // first column (0-based)
				j + 3 // last column (0-based)
		));
		sheet1.addMergedRegion(new CellRangeAddress(i, // first row (0-based)
				i, // last row (0-based)
				j + 4, // first column (0-based)
				j + 4 + rooms.size() - 1 // last column (0-based)
		));
	}

	// this function prints all the room no in excel sheet.
	public void printRooms(int i, int j, Sheet sheet1, ArrayList<Room> rooms) {
		Row row2 = sheet1.getRow(i + 1);
		if (row2 == null)
			row2 = sheet1.createRow(i + 1);
		for (int k = 0; k < rooms.size(); k++) {
			Cell cell = row2.createCell(j + 4 + k);
			cell.setCellValue(rooms.get(k).getRoom_no());
		}
	}

	// this function prints "CEP Rooms" tag and time tag with in bold/enlarged
	// format.
	public int dostuff(int i, int j, Sheet sheet1, ArrayList<Room> rooms, Workbook wb, int flag2, Row row1, int flag) {
		merge(i, j, sheet1, rooms);
		Font font = wb.createFont();
		font.setBold(true);
		CellStyle temp_style = wb.createCellStyle();
		temp_style.setFont(font);
		temp_style.setAlignment(HorizontalAlignment.CENTER_SELECTION);

		printRooms(i, j, sheet1, rooms);
		flag2 = 1;
		if (flag == 1) {
			row1.createCell(j + 1).setCellValue(" 08:30 - 10:30 ");

		} else if (flag == 2) {
			row1.createCell(j + 1).setCellValue(" 11:00 - 13:00 ");

		} else if (flag == 3) {
			row1.createCell(j + 1).setCellValue(" 14:00 - 16:00 ");

		} else if (flag == 4) {
			row1.createCell(j + 1).setCellValue(" 16:30 - 18:30 ");

		}
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

	// this function prints all the data in excel sheet for a particular SLOT
	// and TIME INTERVAL
	public void printInExcel(Map<Integer, String> batch_id_name, Map<Integer, Integer> line,
			Map<Integer, Integer> range, Sheet sheet1, int j, XSSFCellStyle[] style, Set<Course> set, TimeInterval t1,
			Workbook wb, int flag) throws ClassNotFoundException, DAOException, SQLException {
		int flag2 = 0;// to print labels just for first time.
		int border_first = 0;
		int border_last = 0;
		for (int batch_id = 1; batch_id <= batch_id_name.size(); batch_id++) {
			// check if such batch_id exists
			if (!line.containsKey(batch_id))
				continue;

			// start is where first batch starts in a particular time
			// interval,while i starts from
			// very beginning of the block(contains above 2 rows also.)
			int start = line.get(batch_id);
			int end = start + range.get(batch_id) - 1;
			int i = start - 2;
			ArrayList<Room> rooms = GeneralDAO.getRooms();
			
			Row row1 = sheet1.getRow(i);
			if (row1 == null)
				row1 = sheet1.createRow(i);
			// if flag2==0,it means it's first iteration
			if (flag2 == 0) {
				border_first = i;
				flag2 = dostuff(i, j, sheet1, rooms, wb, flag2, row1, flag);
			}

			Row row = sheet1.getRow(start);
			if (row == null)
				row = sheet1.createRow(start);

			// flag can be 1,2,3,4
			// 1:t1 of first slot
			// 2:t2 of first slot
			// 3:t1 of next slot
			// 4:t2 of next slot
			if (flag == 1) {
				if ((end - start) != 0) {
					sheet1.addMergedRegion(new CellRangeAddress(start, // first
																		// row
																		// (0-based)
							end, // last row (0-based)
							j, // first column (0-based)
							j // last column (0-based)
					));
				}
				// Write Batch Name
				CellStyle combined = wb.createCellStyle();
				combined.cloneStyleFrom(style[batch_id - 1]);
				// You can copy other attributes to "combined" here if desired.
				Font font = wb.createFont();
				font.setBold(true);
				combined.setFont(font);
				row.createCell(j).setCellValue(batch_id_name.get(batch_id));
				row.getCell(j).setCellStyle(combined);
			}
			// Start allocating courses
			for (Course course : set) {
				if (Integer.parseInt(course.getBatch()) == batch_id) {
					Row row_temp = sheet1.getRow(start);
					if (row_temp == null) {
						row_temp = sheet1.createRow(start);
					}
					// printing course id and course name
					row_temp.createCell(j + 1).setCellValue(course.getCourse_id());
					row_temp.getCell(j + 1).setCellStyle(style[batch_id - 1]);
					row_temp.createCell(j + 2).setCellValue(course.getCourse_name());
					row_temp.getCell(j + 2).setCellStyle(style[batch_id - 1]);
					row_temp.createCell(j + 3).setCellValue(course.getFaculty());
					row_temp.getCell(j + 3).setCellStyle(style[batch_id - 1]);

					ArrayList<Room> temp_rooms = t1.getRooms();
					// first column for timeinterval1
					int first_col = j + 4;
					for (int p = 0; p < temp_rooms.size(); p++) {
						if (!t1.getMap().containsKey(temp_rooms.get(p).getRoom_no()))
							continue;
						for (OccupationData od : t1.getMap().get(temp_rooms.get(p).getRoom_no())) {
							if (od.getCourse().getCourse_id() == course.getCourse_id()) {
								row_temp.createCell(p + first_col)
										.setCellValue(od.getAllocatedStudents() + "\n[" + od.getSide() + "]");
								row_temp.getCell(p + first_col).setCellStyle(style[batch_id - 1]);
							}
						}
					}
					start++;
				}
			}
			border_last = end;// last iteration will set last end(although it
								// sets this variable multiple times)
		}

		// setting border
		PropertyTemplate pt = new PropertyTemplate();
		int temp_rooms_size=GeneralDAO.getRooms().size();
		// this border is the biggest one covering entire timeinterval
		pt.drawBorders(new CellRangeAddress(border_first, border_last, j + 1, j + 3+temp_rooms_size), BorderStyle.MEDIUM,
				IndexedColors.BLACK.getIndex(), BorderExtent.OUTSIDE);

		// for border of t1
		if (flag == 1) {
			// "Exam day " merging
			sheet1.addMergedRegion(new CellRangeAddress(border_first, // first
																		// row
																		// (0-based)
					border_last, // last row (0-based)
					j - 1, // first column (0-based)
					j - 1)); // last column (0-based)

			Row temp_row = sheet1.getRow(border_first);

			if (temp_row == null)
				temp_row = sheet1.createRow(border_first);
			createCell(wb, temp_row, j - 1, HorizontalAlignment.JUSTIFY.getCode(), VerticalAlignment.CENTER.getCode());

			// drawing border around "Exam day"
			pt.drawBorders(new CellRangeAddress(border_first, border_last, j - 1, j - 1), BorderStyle.MEDIUM,
					BorderExtent.OUTSIDE);

			// drawing borders around Batch column which stores different
			// batches like B.Tech/M.Tech
			pt.drawBorders(new CellRangeAddress(border_first, border_last, j, j), BorderStyle.MEDIUM,
					IndexedColors.BLACK.getIndex(), BorderExtent.OUTSIDE);
		}

		pt.applyBorders(sheet1);
	}

	// creating "Exam Day " cell
	private void createCell(Workbook wb, Row row, int column, short halign, short valign) {
		Cell cell = row.createCell(column);
		cell.setCellValue(" Exam Day ");
		CellStyle cellStyle = wb.createCellStyle();
		cellStyle.setAlignment(HorizontalAlignment.forInt(halign));
		cellStyle.setVerticalAlignment(VerticalAlignment.forInt(valign));
		cellStyle.setRotation((short) 90);
		cell.setCellStyle(cellStyle);
	}

	// this is like main function of this class which actually creates the excel
	// file
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
			style[i].setFillPattern(FillPatternType.BIG_SPOTS);
		// main work
		int i = 7;
		int j = 2;

		int curr_row = i + 2;
		Map<Integer, StoreTT> temp_store = TT.getStore();

		// this loop runs in pair->1 run for (Slot1,Slot2),1 run for
		// (Slot3,Slot4),....
		for (int k = 0; k < temp_store.size(); k = k + 2) {
			Map<Integer, Integer> line = new HashMap<>();// batch_id and row
															// number in Excel
			Map<Integer, Integer> range = new HashMap<>();// batch_id and range
															// in Excel
			curr_row += 3;
			int slot1 = k + 1;
			int slot2 = k + 2;
			int slot2_flag = 0;

			// **********checking for first Slot************
			//curr_row += 1;
			
			TimeInterval t1 = temp_store.get(slot1).getT1();
			TimeInterval t2 = temp_store.get(slot1).getT2();
			Set<String> set1_c = new HashSet<>();
			Set<String> set2_c = new HashSet<>();
			// check T1
			Set<Course> set1 = new HashSet<>();
			for (ArrayList<OccupationData> od : t1.getMap().values()) {

				for (int hh = 0; hh < od.size(); hh++) {
					if (!set1_c.contains(od.get(hh).getCourse().getCourse_id())) {
						set1_c.add(od.get(hh).getCourse().getCourse_id());
						set1.add(od.get(hh).getCourse());
					}
				}
			}

			// check T2
		//	curr_row += 1;
			Set<Course> set2 = new HashSet<>();
			for (ArrayList<OccupationData> od : t2.getMap().values()) {

				for (int hh = 0; hh < od.size(); hh++) {
					if (!set2_c.contains(od.get(hh).getCourse().getCourse_id())) {
						set2_c.add(od.get(hh).getCourse().getCourse_id());
						set2.add(od.get(hh).getCourse());
					}
				}
			}
			// **********************checking for Slot2***************
			Set<Course> set1_2 = new HashSet<>();
			Set<Course> set2_2 = new HashSet<>();
			TimeInterval t1_2 = null;
			TimeInterval t2_2 = null;
			Set<String> set12_c = new HashSet<>();
			Set<String> set22_c = new HashSet<>();

			if (slot2 <= temp_store.size()) {
				slot2_flag = 1;
				t1_2 = temp_store.get(slot2).getT1();
				t2_2 = temp_store.get(slot2).getT2();
				// check T1
//				curr_row += 1;
				for (ArrayList<OccupationData> od : t1_2.getMap().values()) {

					for (int hh = 0; hh < od.size(); hh++) {
						if (!set12_c.contains(od.get(hh).getCourse().getCourse_id())) {
							set12_c.add(od.get(hh).getCourse().getCourse_id());
							set1_2.add(od.get(hh).getCourse());
						}
					}
				}

				// check T2
				//curr_row += 1;
				for (ArrayList<OccupationData> od : t2_2.getMap().values()) {

					for (int hh = 0; hh < od.size(); hh++) {
						if (!set22_c.contains(od.get(hh).getCourse().getCourse_id())) {
							set22_c.add(od.get(hh).getCourse().getCourse_id());
							set2_2.add(od.get(hh).getCourse());
						}
					}
				}
			}
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
				counter = 0;
				for (Course course : set1_2) {
					if (Integer.parseInt(course.getBatch()) == batch_id)
						counter++;
				}
				max = Math.max(max, counter);
				counter = 0;
				for (Course course : set2_2) {
					if (Integer.parseInt(course.getBatch()) == batch_id)
						counter++;
				}
				max = Math.max(max, counter);
				if (max != 0)// to make sure,rows are updated only for those
								// courses which exists in these slots
				{
					line.put(batch_id, curr_row);
					curr_row = curr_row + max;
					range.put(batch_id, max);
				}
				else
					{
						line.put(batch_id, curr_row);
						curr_row = curr_row + 1;
						range.put(batch_id, 1);
					}

			}
			// main allocation in
			// excel######################################################

			// for time interval 1
			printInExcel(batch_id_name, line, range, sheet1, j, style, set1, t1, wb, 1);
			// for time interval 2
			int temp_rooms_size=GeneralDAO.getRooms().size();
			int m=4+temp_rooms_size;
			printInExcel(batch_id_name, line, range, sheet1, j + m, style, set2, t2, wb, 2);
			if (slot2_flag == 1) {
				// for time interval 1_2
				printInExcel(batch_id_name, line, range, sheet1, j + (m*2), style, set1_2, t1_2, wb, 3);
				// for time interval 1_2
				printInExcel(batch_id_name, line, range, sheet1, j + (m*3), style, set2_2, t2_2, wb, 4);
			}
		}
		XSSFCellStyle cust_style = wb.createCellStyle();
		cust_style.setFillBackgroundColor(new XSSFColor(new java.awt.Color(0, 255, 0)));
		cust_style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		// auto-resizing columns
		for (int ii = -1; ii < 100; ii++)
			sheet1.autoSizeColumn(j + ii);

		// saving output to file
		sheet1.getPrintSetup().setLandscape(true);
		
		String fileName = "workbook.xlsx";
		String filePath = FileConfig.OUTPUT_FILES_PATH;
		//System.out.println("Print Excel:"+filePath+fileName);
		FileOutputStream fileOut = new FileOutputStream(filePath+fileName);
		wb.write(fileOut);
		fileOut.close();
		System.out.println("over");
	}
}
