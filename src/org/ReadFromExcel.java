package org;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReadFromExcel {

	public static void read_excel() throws IOException, ClassNotFoundException, DAOException {
		// String excelFilePath = "ExamData.xlsx";
		String fileName = "ExamData.xlsx";
		String filePath = "F:\\exam_timetable\\src\\data\\input\\";
		FileInputStream inputStream = new FileInputStream(new File(filePath + fileName));

		Workbook workbook = new XSSFWorkbook(inputStream);
		Sheet firstSheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = firstSheet.iterator();
		iterator.next();

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

			int slot_no = Integer.parseInt(queue.removeFirst());
			String course_id = queue.removeFirst();
			String course_name = queue.removeFirst();
			String batch = queue.removeFirst();
			int no_of_students = Integer.parseInt(queue.removeFirst());
			GeneralDAO.addCourse(course_id, course_name, batch, no_of_students);
			GeneralDAO.addSlotEntry(slot_no, course_id);
		}

		workbook.close();
		inputStream.close();
	}

}
