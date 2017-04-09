package servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.Authenticator;
import org.GeneralDAO;

/**
 * Servlet implementation class UserServlet
 */
@WebServlet("/UserServlet")
public class UserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UserServlet() {
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
		String action = request.getParameter("action").toLowerCase();
		if (action != null) {
			try {
				if (action.equals("userregistration")) {
					boolean result = GeneralDAO.registerUser(request.getParameter("uname"),
							request.getParameter("password"));
					if (result)
						response.getWriter().write("User registration successful !");
					else
						response.getWriter().write("User already exists !");
				} else if (action.equals("resetpassword")) {
					boolean result = GeneralDAO.resetPassword(request.getParameter("uname"),
							request.getParameter("password"));
					if (result)
						response.getWriter().write("Password reset successful !");
					else
						response.getWriter().write("Couldn't reset the password !");
				}
			} catch (Exception e) {

			}
		}
	}

}
