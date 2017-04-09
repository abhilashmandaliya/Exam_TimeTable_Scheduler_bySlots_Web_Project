package org;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReadFromExcel {

	// flag to indicate course already exists
	private static boolean errorFlag = false;
	private static String errorCourse = "";

	public static boolean isErrorFlag() {
		return errorFlag;
	}

	public static String getErrorCourse() {
		return errorCourse;
	}

	public static void setErrorCourse() {
		ReadFromExcel.errorCourse = "";
	}

	public static void setErrorFlag() {
		ReadFromExcel.errorFlag = false;
	}

	public static void read_excel() throws IOException, ClassNotFoundException, DAOException, SQLException, CustomException {
		// String excelFilePath = "ExamData.xlsx";
		String fileName = "ExamData.xlsx";
		String filePath = FileConfig.INPUT_FILES_PATH + "examData\\";
		FileInputStream inputStream = new FileInputStream(new File(filePath + fileName));

		Workbook workbook = new XSSFWorkbook(inputStream);
		Sheet firstSheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = firstSheet.iterator();
		iterator.next();
		String course_id = "", course_name = "", batch = "", faculty = "";
		int no_of_students;
		System.out.println("Working");
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
					cell.setCellType(Cell.CELL_TYPE_STRING);
					queue.addLast(cell.getStringCellValue());
					// System.out.println("Working");
				}

				// int slot_no = Integer.parseInt(queue.removeFirst());

				course_name = queue.removeFirst();
				course_id = queue.removeFirst();
				faculty = queue.removeFirst();
				no_of_students = Integer.parseInt(queue.removeFirst());
				batch = queue.removeFirst();

				GeneralDAO.addCourse(course_id, course_name, batch, no_of_students, faculty);
				// GeneralDAO.addSlotEntry(slot_no, course_id);

				GeneralDAO.getCon().commit();

			}
		} catch (SQLException e) {
			GeneralDAO.getCon().rollback();
			// e.printStackTrace();
			errorFlag = true;
			errorCourse = course_id + ":" + course_name + ":" + batch;
			System.out.println("Course already exists.");
		}

		finally {
			System.out.println("Working");
			GeneralDAO.getCon().setAutoCommit(true);
			workbook.close();
			inputStream.close();
		}
	}

	public static void read_excel(int slot_no) throws IOException, ClassNotFoundException, DAOException, SQLException {
		// String excelFilePath = "ExamData.xlsx";
		String fileName = "slot" + (slot_no) + "course.xlsx";
		String filePath = FileConfig.INPUT_FILES_PATH + "slotData\\";
		FileInputStream inputStream = new FileInputStream(new File(filePath + fileName));

		Workbook workbook = new XSSFWorkbook(inputStream);
		Sheet firstSheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = firstSheet.iterator();
		iterator.next();
		String course_id = "";
		System.out.println("Working");
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
					cell.setCellType(Cell.CELL_TYPE_STRING);
					queue.addLast(cell.getStringCellValue());
					// System.out.println("Working");
				}

				course_id = queue.removeFirst();

				GeneralDAO.addSlotEntry(slot_no, course_id);

				GeneralDAO.getCon().commit();

			}
		} catch (Exception e) {
			GeneralDAO.getCon().rollback();
			// e.printStackTrace();
			errorFlag = true;
			errorCourse = course_id;
			System.out.println("Course already exists in the given slot.");
		}

		finally {
			GeneralDAO.getCon().setAutoCommit(true);
		}

		workbook.close();
		inputStream.close();
	}

}
