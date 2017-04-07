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

import org.Authenticator;
import org.DAOException;
import org.Slot;
import org.TimeTableEndSem;

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
		// response.getWriter().append("Served at:
		// ").append(request.getContextPath());
		if(!Authenticator.isAuthorized(request.getSession(), this.getClass().getName()))
			response.sendRedirect("login.jsp");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		if(!Authenticator.isAuthorized(request.getSession(), this.getClass().getName()))
			response.sendRedirect("login.jsp");
		String action = request.getParameter("action");
		Slot s = (Slot) request.getSession().getAttribute(request.getParameter("slot"));
		try {
			if (action.equals("delete"))
				response.getWriter().write(s.deleteCourseFromDB(request.getParameter("course")));
			else if (action.equals("add"))
				s.addCourseToDB(request.getParameter("course"));
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
