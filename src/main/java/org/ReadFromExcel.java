package org;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReadFromExcel {

	// flag to indicate course already exists

	public static void read_excel()
			throws IOException, ClassNotFoundException, DAOException, SQLException, CustomException {
		// String excelFilePath = "ExamData.xlsx";
		String fileName = "ExamData.xlsx";
		//String filePath = FileConfig.INPUT_FILES_PATH + "examData\\";
		String filePath = FileConfig.INPUT_FILES_PATH;
		FileInputStream inputStream = new FileInputStream(new File(filePath + fileName));

		Workbook workbook = new XSSFWorkbook(inputStream);
		Sheet firstSheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = firstSheet.iterator();
		iterator.next();
		String course_id = "", course_name = "", batch = "", faculty = "";
		int no_of_students;
	//	System.out.println("Working");
		try {

			if (GeneralDAO.getCon() == null)
				GeneralDAO.makeConnection();
			GeneralDAO.getCon().setAutoCommit(false);

			while (iterator.hasNext()) {
				Row nextRow = iterator.next();
				Iterator<Cell> cellIterator = nextRow.cellIterator();
				LinkedList<String> queue = new LinkedList<>();
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					cell.setCellType(CellType.STRING);
					queue.addLast(cell.getStringCellValue());
					// System.out.println("Working");
				}

				// int slot_no = Integer.parseInt(queue.removeFirst());

				course_name = queue.removeFirst();
				course_id = queue.removeFirst();
				faculty = "N/A";
				no_of_students = Integer.parseInt(queue.removeFirst());
				batch = queue.removeFirst();

				GeneralDAO.addCourse(course_id.trim(), course_name.trim(), batch.trim(), no_of_students, faculty);
				// GeneralDAO.addSlotEntry(slot_no, course_id);

				

			}
			GeneralDAO.getCon().commit();
		} catch (SQLException e) {
			GeneralDAO.getCon().rollback();
			// e.printStackTrace();
			if (TransactionStatus.getStatusMessage() == null)
				TransactionStatus
						.setStatusMessage("Course already exists : " + course_id + " - " + course_name);
		}

		finally {
			//System.out.println("Working");
			GeneralDAO.getCon().setAutoCommit(true);
			workbook.close();
			inputStream.close();
			
		}
	}

	public static void read_excel(int slot_no) throws IOException, ClassNotFoundException, DAOException, SQLException {
		// String excelFilePath = "ExamData.xlsx";
		String fileName = "slot" + (slot_no) + "course.xlsx";
		//String filePath = FileConfig.INPUT_FILES_PATH + "slotData\\";
		String filePath = FileConfig.INPUT_FILES_PATH;
		FileInputStream inputStream = new FileInputStream(new File(filePath + fileName));

		Workbook workbook = new XSSFWorkbook(inputStream);
		Sheet firstSheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = firstSheet.iterator();
		iterator.next();
		String course_id = "";
		String faculty= "";
		//System.out.println("Working");
		try {

			if (GeneralDAO.getCon() == null)
				GeneralDAO.makeConnection();
			GeneralDAO.getCon().setAutoCommit(false);

			while (iterator.hasNext()) {
				Row nextRow = iterator.next();
				Iterator<Cell> cellIterator = nextRow.cellIterator();
				LinkedList<String> queue = new LinkedList<>();
				
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					cell.setCellType(CellType.STRING);
					queue.addLast(cell.getStringCellValue());
					// System.out.println("Cell Value: "+cell.getStringCellValue());
				}

				course_id = queue.removeFirst();
				faculty=queue.removeFirst();
				
				GeneralDAO.addSlotEntry(slot_no, course_id.trim());
				GeneralDAO.updateFaculty(course_id.trim(), faculty.trim());
			//	System.out.println(course_id+"hey"+faculty);
				

			}
			GeneralDAO.getCon().commit();
		} catch (Exception e) {
			GeneralDAO.getCon().rollback();
			e.printStackTrace();
			TransactionStatus.setStatusMessage("There are some issues with " + course_id+"\nPlease try again after resolving the issue");
		} finally {
			GeneralDAO.getCon().setAutoCommit(true);
			workbook.close();
			inputStream.close();
			
		}
		
	}

}
