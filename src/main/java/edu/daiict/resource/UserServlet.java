package edu.daiict.resource;

import edu.daiict.other.Authenticator;
import edu.daiict.other.GeneralDAO;
import edu.daiict.other.TransactionStatus;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
		String action = request.getParameter("action").toLowerCase();
		if (action != null) {
			try {
				if (action.equals("userregistration")) {
					boolean result = GeneralDAO.registerUser(request.getParameter("uname"),
							request.getParameter("password"));
					if (result) {
						TransactionStatus.setStatusMessage("User registration successful !");
					} else {
						TransactionStatus.setStatusMessage("User already exists !");
					}
				} else if (action.equals("resetpassword")) {
					boolean result = GeneralDAO.resetPassword(request.getParameter("uname"),
							request.getParameter("password"));
					if (result) {
						TransactionStatus.setStatusMessage("Password reset successful !");
					} else {
						TransactionStatus.setStatusMessage("Couldn't reset the password !");
					}
				}
			} catch (Exception e) {

			} finally {
				if (TransactionStatus.getStatusMessage() == null) {
					TransactionStatus.setDefaultStatusMessage();
				}
				response.getWriter().write(TransactionStatus.getStatusMessage());
				TransactionStatus.setStatusMessage(null);
			}
		}
	}

}
