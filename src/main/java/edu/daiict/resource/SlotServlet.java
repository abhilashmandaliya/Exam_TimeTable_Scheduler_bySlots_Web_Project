package edu.daiict.resource;

import edu.daiict.other.Authenticator;
import edu.daiict.other.DAOException;
import edu.daiict.other.Slot;
import edu.daiict.other.TransactionStatus;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
		String action = request.getParameter("action");
		Slot s = (Slot) request.getSession().getAttribute(request.getParameter("slot"));
		try {
			if (action.equals("delete")) {
				s.deleteCourseFromDB(request.getParameter("course"));
				TransactionStatus.setStatusMessage("Course deleted Successfully !");
			} else if (action.equals("add")) {
				s.addCourseToDB(request.getParameter("course"));
				TransactionStatus.setStatusMessage("Course added Successfully !");
			}
		} catch (DAOException e) {
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
