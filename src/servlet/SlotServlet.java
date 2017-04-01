package servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.DAOException;
import org.Slot;
import org.TimeTable;

/**
 * Servlet implementation class SlotServlet
 */
@WebServlet("/SlotServlet")
public class SlotServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SlotServlet() {
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
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		System.out.println("doGet");
		doPost(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		String action = request.getParameter("action");
		Slot s = (Slot) request.getSession().getAttribute(request.getParameter("slot"));
		try {
			if (action.equals("delete"))
				s.deleteCourseFromDB(request.getParameter("course"));
			else if (action.equals("add"))
				s.addCourseToDB(request.getParameter("course"));
			else if (action.equals("generateAndDownloadTT")) {
				System.out.println("conc");
				//TimeTable.main(null);
//				String fileName = "C:\\Users\\ashwani tanwar\\workspace\\Exam_TimeTable_Scheduler_bySlots_Web_Project\\workbook.xlsx";
//		         String fileType = "xlsx";
//		         response.setContentType(fileType);
//		         response.setHeader("Content-disposition","attachment; filename=workbook.xlsx");
//		         File my_file = new File(fileName);
//		         OutputStream out = response.getOutputStream();
//		         FileInputStream in = new FileInputStream(my_file);
//		         byte[] buffer = new byte[4096];
//		         int length;
//		         while ((length = in.read(buffer)) > 0){
//		            out.write(buffer, 0, length);
//		         }
//		         in.close();
//		         out.flush();
				response.getWriter().write("http://localhost:8080/Exam_TimeTable_Scheduler_bySlots_Web_Project/workbook.xlsx");
			}
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
