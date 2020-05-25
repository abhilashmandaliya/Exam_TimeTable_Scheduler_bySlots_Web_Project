package edu.daiict.resource;

import edu.daiict.model.BatchProgram;
import edu.daiict.view.BatchProgramView;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller to handle batch-program mapping
 */
@Path("/batch-program")
public class BatchProgramResource {
	private static final long serialVersionUID = 1L;

	@GET
	@Path("/test")
	public BatchProgramView doGet() {
		// TODO Auto-generated method stub
//		if (!Authenticator.isAuthorized(request.getSession(), getClass().getName())) {
//			response.sendRedirect("login.jsp");
//		}
		List<BatchProgram> list = new ArrayList<>();
		list.add(new BatchProgram(1, "BTech"));
		list.add(new BatchProgram(2, "MTech"));
		return new BatchProgramView(list);
	}

//	@GET
//	public void doPost(HttpServletRequest request, HttpServletResponse response)
//			throws ServletException, IOException {
//		// TODO Auto-generated method stub
//		if (!Authenticator.isAuthorized(request.getSession(), getClass().getName())) {
//			response.sendRedirect("login.jsp");
//		}
//		String action = request.getParameter("action");
//		if (action != null) {
//			action = action.toLowerCase();
//			try {
//				if (action.equals("mapbatchprogram")) {
//					GeneralDAO.addBatch_Program(Integer.parseInt(request.getParameter("batch_no")),
//							request.getParameter("program"));
//					TransactionStatus.setStatusMessage("Mapped Successfully!");
//				} else if (action.equals("remapbatchprogram")) {
//					GeneralDAO.updateBatch_Program(Integer.parseInt(request.getParameter("batch_no")),
//							request.getParameter("program"));
//					TransactionStatus.setStatusMessage("Re-Mapped Successfully!");
//				} else if (action.equals("removebatchprogram")) {
//					GeneralDAO.deleteBatch_Program(Integer.parseInt(request.getParameter("batch_no")));
//					TransactionStatus.setStatusMessage("Mapping removed Successfully!");
//				} else {
//					TransactionStatus.setStatusMessage("Invalid request !");
//				}
//			} catch (NumberFormatException e) {
//				e.printStackTrace();
//				TransactionStatus.setStatusMessage("Invalid Number !");
//			} catch (ClassNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (DAOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} finally {
//				if (TransactionStatus.getStatusMessage() == null) {
//					TransactionStatus.setDefaultStatusMessage();
//				}
//				response.getWriter().write(TransactionStatus.getStatusMessage());
//				TransactionStatus.setStatusMessage(null);
//			}
//		}
//	}

}
