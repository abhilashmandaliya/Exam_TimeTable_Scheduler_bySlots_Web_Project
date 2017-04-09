package org;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpSession;

public class Authenticator {
	private static Set<String> protectedURLs = null;

	private static void initProtectedURLs() {
		protectedURLs = new HashSet<>();
		// servlet
		protectedURLs.add("servlet.CourseServlet");
		protectedURLs.add("servlet.FileDownloadServlet");
		protectedURLs.add("servlet.FileUploadServlet");
		protectedURLs.add("servlet.SlotServlet");
		protectedURLs.add("servlet.RoomServlet");
		protectedURLs.add("servlet.UserServlet");
		protectedURLs.add("servlet.BatchProgramServlet");

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
		if (protectedURLs == null)
			initProtectedURLs();
		if (isProtected(URL) && session.getAttribute("user") == null)
			return false;
		return true;
	}
}
