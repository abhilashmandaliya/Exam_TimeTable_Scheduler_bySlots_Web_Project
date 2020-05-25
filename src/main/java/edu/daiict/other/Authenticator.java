package edu.daiict.other;

import javax.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.Set;

public class Authenticator {
	private static Set<String> protectedURLs = null;

	private static void initProtectedURLs() {
		protectedURLs = new HashSet<>();
		// servlet
		protectedURLs.add("edu.daiict.resource.CourseServlet");
		protectedURLs.add("edu.daiict.resource.FileDownloadServlet");
		protectedURLs.add("edu.daiict.resource.FileUploadServlet");
		protectedURLs.add("edu.daiict.resource.SlotServlet");
		protectedURLs.add("edu.daiict.resource.RoomServlet");
		protectedURLs.add("edu.daiict.resource.UserServlet");
		protectedURLs.add("edu.daiict.resource.BatchProgramServlet");

		// jsp
		protectedURLs.add("org.apache.jsp.Course_jsp");
		protectedURLs.add("org.apache.jsp.Home_jsp");
		protectedURLs.add("org.apache.jsp.Room_jsp");
		protectedURLs.add("org.apache.jsp.Slot_jsp");
		protectedURLs.add("org.apache.jsp.TimeTable_jsp");
		protectedURLs.add("org.apache.jsp.UserRegistration_jsp");
		protectedURLs.add("org.apache.jsp.PasswordReset_jsp");
		protectedURLs.add("org.apache.jsp.BatchProgram_jsp");
	}

	private static boolean isProtected(String URL) {
		return protectedURLs.contains(URL);
	}

	public static boolean isAuthorized(HttpSession session, String URL) {
		if (protectedURLs == null) {
			initProtectedURLs();
		}
		if (isProtected(URL) && session.getAttribute("user") == null) {
			return false;
		}
		return true;
	}
}
