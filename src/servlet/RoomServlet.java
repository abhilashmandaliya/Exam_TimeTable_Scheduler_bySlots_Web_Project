package servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.Authenticator;
import org.DAOException;
import org.GeneralDAO;
import org.TransactionStatus;

/**
 * Servlet implementation class RoomServlet
 */
@WebServlet("/RoomServlet")
public class RoomServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RoomServlet() {
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
		try {
			if (action.equals("register")) {
				GeneralDAO.addRoom(Integer.parseInt(request.getParameter("room_no")),
						Integer.parseInt(request.getParameter("capacity")));
				TransactionStatus.setStatusMessage("Room added Successfully !");
			} else if (action.equals("update")) {
				GeneralDAO.updateRoom(Integer.parseInt(request.getParameter("room_no")),
						Integer.parseInt(request.getParameter("capacity")));
				TransactionStatus.setStatusMessage("Room updated Successfully !");
			} else if (action.equals("delete")) {
				GeneralDAO.deleteRoom(Integer.parseInt(request.getParameter("room_no")));
				TransactionStatus.setStatusMessage("Room deleted Successfully !");
			} else
				TransactionStatus.setStatusMessage("Invalid request !");
		} catch (NumberFormatException | ClassNotFoundException | DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (TransactionStatus.getStatusMessage() == null)
				TransactionStatus.setDefaultStatusMessage();
			response.getWriter().write(TransactionStatus.getStatusMessage());
			TransactionStatus.setStatusMessage(null);
		}
	}

}
