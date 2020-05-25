package edu.daiict.resource;

import edu.daiict.other.Authenticator;
import edu.daiict.other.DAOException;
import edu.daiict.other.GeneralDAO;
import edu.daiict.other.TransactionStatus;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet implementation class CourseServlet
 */
@WebServlet("/CourseServlet")
public class CourseServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CourseServlet() {
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
		try {
			String action = request.getParameter("action").toLowerCase();
			if (action.equals("register")) {
				GeneralDAO.addCourse(request.getParameter("course_id"), request.getParameter("course_name"),
						request.getParameter("batch"), Integer.parseInt(request.getParameter("no_of_students")),
						request.getParameter("faculty"));
				TransactionStatus.setStatusMessage("Course Registration Successful !");
			} else if (action.equals("update")) {
				GeneralDAO.updateCourse(request.getParameter("course_id"), request.getParameter("course_name"),
						request.getParameter("batch"), Integer.parseInt(request.getParameter("no_of_students")),
						request.getParameter("faculty"));
				TransactionStatus.setStatusMessage("Course Updated Successfully !");
			} else if (action.equals("delete")) {
				GeneralDAO.deleteCourse(request.getParameter("course_id"));
				TransactionStatus.setStatusMessage("Course deleted Successfully !");
			} else if (action.equals("deleteallcourses")) {
				GeneralDAO.deleteAllCourses();
				TransactionStatus.setStatusMessage("All Courses deleted Successfully !");
			} else {
				TransactionStatus.setStatusMessage("Invalid request !");
			}
		} catch (NumberFormatException | ClassNotFoundException | DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (TransactionStatus.getStatusMessage() == null) {
				TransactionStatus.setDefaultStatusMessage();
			}
			response.getWriter().write(TransactionStatus.getStatusMessage());
			TransactionStatus.setStatusMessage(null);
		}
	}

}
