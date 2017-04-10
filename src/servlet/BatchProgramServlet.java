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
 * Servlet implementation class BatchProgramServlet
 */
@WebServlet("/BatchProgramServlet")
public class BatchProgramServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public BatchProgramServlet() {
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
		String action = request.getParameter("action");
		if (action != null) {
			action = action.toLowerCase();
			try {
				if (action.equals("mapbatchprogram")) {
					GeneralDAO.addBatch_Program(Integer.parseInt(request.getParameter("batch_no")),
							request.getParameter("program"));
					TransactionStatus.setStatusMessage("Mapped Successfully!");
				} else if (action.equals("remapbatchprogram")) {
					GeneralDAO.updateBatch_Program(Integer.parseInt(request.getParameter("batch_no")),
							request.getParameter("program"));
					TransactionStatus.setStatusMessage("Re-Mapped Successfully!");
				} else if (action.equals("removebatchprogram")) {
					GeneralDAO.deleteBatch_Program(Integer.parseInt(request.getParameter("batch_no")));
					TransactionStatus.setStatusMessage("Mapping removed Successfully!");
				} else
					TransactionStatus.setStatusMessage("Invalid request !");
			} catch (NumberFormatException e) {
				e.printStackTrace();
				TransactionStatus.setStatusMessage("Invalid Number !");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DAOException e) {
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

}
