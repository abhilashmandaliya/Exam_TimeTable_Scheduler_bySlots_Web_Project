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

	public static void read_excel() throws IOException, ClassNotFoundException, DAOException, SQLException {
		// String excelFilePath = "ExamData.xlsx";
		String fileName = "ExamData.xlsx";
		String filePath = "C:\\Users\\ashwani tanwar\\workspace\\Exam_TimeTable_Scheduler_bySlots_Web_Project\\src\\data\\input\\";
		FileInputStream inputStream = new FileInputStream(new File(filePath + fileName));

		Workbook workbook = new XSSFWorkbook(inputStream);
		Sheet firstSheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = firstSheet.iterator();
		iterator.next();
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
				String course_id = queue.removeFirst();
				String course_name = queue.removeFirst();
				String batch = queue.removeFirst();
				int no_of_students = Integer.parseInt(queue.removeFirst());
				String faculty = queue.removeFirst();

				GeneralDAO.addCourse(course_id, course_name, batch, no_of_students, faculty);
				// GeneralDAO.addSlotEntry(slot_no, course_id);

				GeneralDAO.getCon().commit();

			}
		} catch (Exception e) {
			GeneralDAO.getCon().rollback();
			// e.printStackTrace();
			System.out.println("Course already exists.");
		}

		finally {
			System.out.println("Working");
			GeneralDAO.getCon().setAutoCommit(true);
		}

		workbook.close();
		inputStream.close();
	}

	public static void read_excel(int slot_no) throws IOException, ClassNotFoundException, DAOException, SQLException {
		// String excelFilePath = "ExamData.xlsx";
		String fileName = "slot" + slot_no + "course.xlsx";
		String filePath = "C:\\Users\\ashwani tanwar\\workspace\\Exam_TimeTable_Scheduler_bySlots_Web_Project\\src\\data\\input\\";
		FileInputStream inputStream = new FileInputStream(new File(filePath + fileName));

		Workbook workbook = new XSSFWorkbook(inputStream);
		Sheet firstSheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = firstSheet.iterator();
		iterator.next();
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

				String course_id = queue.removeFirst();

				GeneralDAO.addSlotEntry(slot_no, course_id);

				GeneralDAO.getCon().commit();

			}
		} catch (Exception e) {
			GeneralDAO.getCon().rollback();
			// e.printStackTrace();
		}

		finally {
			GeneralDAO.getCon().setAutoCommit(true);
		}

		workbook.close();
		inputStream.close();
	}

}
