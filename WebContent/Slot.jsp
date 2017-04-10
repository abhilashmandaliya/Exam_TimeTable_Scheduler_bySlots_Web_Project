<%@page import="java.util.ArrayList"%>
<%@page import="java.sql.*"%>
<%@page import="org.Slot"%>
<%@page import="org.Course"%>
<%@include file="header.jsp"%>
<div class="row">
	<div class="col-sm-6">
		<div class="alert alert-info">
			<strong>Current Slot Courses!</strong>
		</div>
		<%
			int slot_num;
			if (request.getParameter("slotNo") == null)
				slot_num = 1;
			else
				slot_num = Integer.parseInt(request.getParameter("slotNo"));
			HttpSession crnt_session = request.getSession();

			Slot s = new Slot(slot_num);
			// some method which will return slot count. store it in cnt
			crnt_session.setAttribute(String.valueOf(slot_num), s);
			int cnt = 8;
		%>
		<table class="table" id="courseIncluded">
			<tr>
				<th>Course Code</th>
				<th>Course Name</th>
				<th>Delete</th>
			</tr>
			<%
				s.refreshCourses();
				ArrayList<Course> courses = s.getCourses();
				for (int i = 0; i < courses.size(); i++) {
					out.write("<tr id='row" + i + "'><td>");
					out.write(courses.get(i).getCourse_id() + "</td><td>" + courses.get(i).getCourse_name() + "</td>");
					out.write("<td><button slot='" + slot_num + "' course='" + courses.get(i).getCourse_id()
							+ "' class='btn btn-danger' type='button' id='deleteRow" + i + "'>Delete</button> </td></tr>");
				}
			%>
		</table>
		<ul class="pagination">
			<%
				for (int i = 1; i <= cnt; i++) {
					if (i == slot_num)
						out.write("<li class='active'><a href='/Exam_TimeTable_Scheduler_bySlots_Web_Project/Slot.jsp?slotNo="+(i)+"'>" + i + "</a></li>");
					else
						out.write("<li><a href='/Exam_TimeTable_Scheduler_bySlots_Web_Project/Slot.jsp?slotNo="+(i)+"'>"+ i + "</a></li>");
				}
			%>
		</ul>

	</div>
	<div class="col-sm-6">
		<div class="alert alert-info">
			<strong>All Courses!</strong> Choose course to add in the current
			slot.
		</div>
		<div class="availableCourse">
			<table class="table" id="remainingCourses">
				<tr>
					<th>Course Code</th>
					<th>Course Name</th>
					<th>Batch</th>
					<th>Add</th>
				</tr>
				<%
					Statement st = s.getCon().createStatement();
					ResultSet rs = st.executeQuery(
							"select c.course_id,c.course_name,b_p.program from course c, batch_program b_p where c.batch=b_p.batch and c.course_id not in (select course_id from slot) order by c.course_name");
					int i = 0;
					while (rs.next()) {
						out.write("<tr id='row" + i + "'><td>");
						out.write(rs.getString(1) + "</td><td>" + rs.getString(2) + "</td><td>" + rs.getString(3) + "</td>");
						out.write("<td><button slot='" + slot_num + "' course='" + rs.getString(1)
								+ "' class='btn btn-success' type='button' id='addCourse" + (i++)
								+ "'>Add</button> </td></tr>");
					}
				%>
			</table>
		</div>
	</div>
</div>
<%@include file="footer.jsp"%>