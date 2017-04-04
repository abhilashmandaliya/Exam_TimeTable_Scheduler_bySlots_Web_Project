package servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.GeneralDAO;
import org.ReadFromExcel;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.output.*;

/**
 * Servlet implementation class FileUploadServlet
 */
@WebServlet("/FileUploadServlet")
public class FileUploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private boolean isMultipart;
	private String filePath;
	private File file;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FileUploadServlet() {
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
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	public void init() {
		// Get the file location where it would be stored.
		filePath = getServletContext().getInitParameter("file-upload");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		isMultipart = ServletFileUpload.isMultipartContent(request);
		PrintWriter out = response.getWriter();
		DiskFileItemFactory factory = new DiskFileItemFactory();
	      // Create a new file upload handler
	      ServletFileUpload upload = new ServletFileUpload(factory);
	      try{ 
	          // Parse the request to get file items.
	          List fileItems = upload.parseRequest(request);	    	
	          // Process the uploaded file items
	          Iterator i = fileItems.iterator();
	          while ( i.hasNext () ) 
	          {
	             FileItem fi = (FileItem)i.next();
	             if ( !fi.isFormField () )	
	             {
	                String fieldName = fi.getFieldName();
	                String fileName = fi.getName();
	                String contentType = fi.getContentType();
	                boolean isInMemory = fi.isInMemory();
	                long sizeInBytes = fi.getSize();
	                // Write the file
	                if( fileName.lastIndexOf("\\") >= 0 ){
	                   file = new File( filePath + 
	                   fileName.substring( fileName.lastIndexOf("\\"))) ;
	                }else{
	                   file = new File( filePath + 
	                   fileName.substring(fileName.lastIndexOf("\\")+1)) ;
	                }
	                fi.write( file ) ;
	                GeneralDAO.deleteAllCourses();
	                ReadFromExcel.read_excel();
	             }
	          }
	       }catch(Exception ex) {
	           System.out.println(ex);
	       }
	}

}
