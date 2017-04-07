package org;

public interface FileConfig {
	/*
	 * Excel file paths- JARS web.xml FileDownloadServlet
	 */
	String PROJECT_PATH = "F:\\";
	String PROJECT_NAME = "exam_timetable\\";
	String INPUT_FILES_PATH = PROJECT_PATH + PROJECT_NAME + "src\\data\\input\\";
	String OUTPUT_FILES_PATH = PROJECT_PATH + PROJECT_NAME + "src\\data\\output\\";
	String EXAM_FILE_NAME = "ExamData.xlsx";
}
