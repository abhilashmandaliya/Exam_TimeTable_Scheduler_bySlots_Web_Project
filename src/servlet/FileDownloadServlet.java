package servlet;

import java.io.File;
import java.io.FileInputStream;
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
								Iterator<Course> failedCourseIterator = GenerateTT.getFailedCourses().iterator();
								while (failedCourseIterator.hasNext())
									out.write(failedCourseIterator.next().toString()+"\n");
								return;
							}
						} else if (request.getParameter("semester").equals("endsem")) {
							GenerateTTEndSem.main(null);
							if (GenerateTTEndSem.getFailedCourses().size() != 0) {
								Iterator<Course> failedCourseIterator = GenerateTTEndSem.getFailedCourses().iterator();								
								while (failedCourseIterator.hasNext())
									out.write(failedCourseIterator.next().toString());
								return;
							}
						}
						out.write("true");
					}
				} else if (action.toLowerCase().equals("downloadtt")) {
					response.setContentType("text/html");
					String fileName = "workbook.xlsx";
					String filePath = FileConfig.OUTPUT_FILES_PATH;
					response.setContentType("APPLICATION/OCTET-STREAM");
					response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
					FileInputStream fis = new FileInputStream(filePath + fileName);
					int i;
					while ((i = fis.read()) != -1)
						out.write(i);
					fis.close();
					out.close();
				}
				// System.out.println("exiting the program!");
			} catch (Exception e) {
				e.printStackTrace();
				response.getWriter().write("\nSome error occured.\nEnsure that all excel files are closed.\nKindly contact the developers.");
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