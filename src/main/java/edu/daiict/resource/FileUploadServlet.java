package edu.daiict.resource;

import edu.daiict.other.Authenticator;
import edu.daiict.other.CustomException;
import edu.daiict.other.FileConfig;
import edu.daiict.other.ReadFromExcel;
import edu.daiict.other.TransactionStatus;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Servlet implementation class FileUploadServlet
 */
@WebServlet("/FileUploadServlet")
public class FileUploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String filePath;
	private File file;
	boolean isMultiPart;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FileUploadServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 * response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		if (!Authenticator.isAuthorized(request.getSession(), getClass().getName())) {
			response.sendRedirect("login.jsp");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 * response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		if (!Authenticator.isAuthorized(request.getSession(), getClass().getName())) {
			response.sendRedirect("login.jsp");
		}
		FileConfig.PROJECT_NAME = getServletContext().getContextPath();
		isMultiPart = ServletFileUpload.isMultipartContent(request);
		if (isMultiPart) {
			String fileFor = "";
			String slotParam = "";
			DiskFileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			String fileName = "";
			try {
				List<FileItem> fileItems = upload.parseRequest(request);
				Iterator<FileItem> i = fileItems.iterator();

				while (i.hasNext()) {
					FileItem fi = (FileItem) i.next();
					if (!fi.isFormField()) {
						if (!fi.getName().endsWith(".xlsx")) {
							throw new CustomException("Not .xlsx File !");
						}
						fileName = "TYPE_MISMATCH";
						filePath = FileConfig.INPUT_FILES_PATH;
						if (fileFor.toLowerCase().equals("slotdetails")) {
							//filePath += "slotData\\";
							fileName = "slot" + slotParam + "course.xlsx";
						} else if (fileFor.toLowerCase().equals("examdetails")) {
							//filePath += "examData\\";
							fileName = "ExamData.xlsx";
						}
						if (fileName.lastIndexOf("\\") >= 0) {
							file = new File(filePath + fileName.substring(fileName.lastIndexOf("\\")));
						} else {
							file = new File(filePath + fileName.substring(fileName.lastIndexOf("\\") + 1));
						}
						fi.write(file);
						if (slotParam == "") {
							ReadFromExcel.read_excel();
							//new File(filePath+fileName).delete();
						} else {
							ReadFromExcel.read_excel(Integer.parseInt(slotParam));
							//	new File(filePath+fileName).delete();
						}
						if (TransactionStatus.getStatusMessage() == null) {
							TransactionStatus.setStatusMessage("File Uploaded Succesfully !");
						}
					} else {
						if (fi.getFieldName().equals("file")) {
							fileFor = fi.getString();
						} else if (fi.getFieldName().equals("slot_no")) {
							slotParam = fi.getString();
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				//response.getWriter().write(TransactionStatus.getStatusMessage());
			} finally {
				if (TransactionStatus.getStatusMessage() == null) {
					TransactionStatus.setDefaultStatusMessage();
				}
				response.getWriter().write(TransactionStatus.getStatusMessage());
				TransactionStatus.setStatusMessage(null);
				new File(filePath + fileName).delete();

			}
		}
	}

}
