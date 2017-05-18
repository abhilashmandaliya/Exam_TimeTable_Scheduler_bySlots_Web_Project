package servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.Authenticator;
import org.Course;
import org.DAOException;
import org.FileConfig;
import org.GenerateTT;
import org.TimeTable;
import org.GenerateTTEndSem;
import org.TimeTableEndSem;
import org.TransactionStatus;

/**
 * Servlet implementation class FileDownloadServlet
 */
@WebServlet("/FileDownloadServlet")
public class FileDownloadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FileDownloadServlet() {
		super();
		
		// TODO Auto-generated constructor stub
		
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		//FileConfig.PROJECT_NAME = this.getServletContext().getContextPath();
		//System.out.println("FDS constructed:"+request.getServletContext().getRealPath("/"));
		//FileConfig.OUTPUT_FILES_PATH = this.getServletContext().getContextPath();
		//System.out.println("Servlet:"+getServletContext().getRealPath(FileConfig.PROJECT_NAME));
		if (!Authenticator.isAuthorized(request.getSession(), this.getClass().getName()))
			response.sendRedirect("login.jsp");
		String action = request.getParameter("action");
		if (action != null) {
			try {
				PrintWriter out = response.getWriter();
				if (action.toLowerCase().equals("generatett")) {
					if (request.getParameter("semester") != null) {
						if (request.getParameter("semester").equals("insem")) {
							GenerateTT.main(null);
							if (GenerateTT.getFailedCourses().size() != 0) {
								System.out.println("came here ");
								Iterator<Course> failedCourseIterator = GenerateTT.getFailedCourses().iterator();
								String message = "Following courses could not be allocated :\n";
								while (failedCourseIterator.hasNext())
									message += (failedCourseIterator.next().toString() + "\n");
								message += "\nAdd more rooms to Allocate these course.\n\nPartial Timetable will get downloaded.";
								TransactionStatus.setStatusMessage(message);
							}
						} else if (request.getParameter("semester").equals("endsem")) {
							GenerateTTEndSem.main(null);
							if (GenerateTTEndSem.getFailedCourses().size() != 0) {
								Iterator<Course> failedCourseIterator = GenerateTTEndSem.getFailedCourses().iterator();
								String message = "Following courses could not be allocated :\n";
								while (failedCourseIterator.hasNext())
									message += (failedCourseIterator.next().toString());
								message += "\nAdd more rooms to Allocate these course.\n\nPartial Timetable will get downloaded.";
								TransactionStatus.setStatusMessage(message);
							}
						}
						if (TransactionStatus.getStatusMessage() == null)
							TransactionStatus.setStatusMessage("Timetable is ready to be downloaded !");
					}
				} else if (action.toLowerCase().equals("downloadtt")) {
					String fileName = "workbook.xlsx";
					String filePath = "";//FileConfig.OUTPUT_FILES_PATH;
					FileInputStream fis = new FileInputStream(filePath + fileName);
					response.setContentType("text/html");					
					response.setContentType("APPLICATION/OCTET-STREAM");
					response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");					
					int i;
					while ((i = fis.read()) != -1)
						out.write(i);
					fis.close();
					out.close();
					new File(filePath + fileName).delete();
				}
			} catch(FileNotFoundException e){
				response.sendRedirect("TimeTable.jsp");
			}
			catch (Exception e) {
				e.printStackTrace();
				if (TransactionStatus.getStatusMessage() == null)
					TransactionStatus.setStatusMessage(
							"Some error occured.\nEnsure that all excel files are closed.\nKindly contact the developers.");
			} finally {
				if (TransactionStatus.getStatusMessage() == null)
					TransactionStatus.setDefaultStatusMessage();
				response.getWriter().write(TransactionStatus.getStatusMessage());
				TransactionStatus.setStatusMessage(null);
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		if (!Authenticator.isAuthorized(request.getSession(), this.getClass().getName()))
			response.sendRedirect("login.jsp");
	}

}