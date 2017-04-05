<%@page import="org.GeneralDAO"%>
<%@page import="java.util.ArrayList"%>
<%@page import="org.Course"%>
<%
	/*@page import="java.sql.*"*/
%>
<%@include file="header.jsp"%>
<div class="row">
	<div class="col-sm-6">
		<div class="alert alert-info">
			<strong>Available Courses</strong>
		</div>
		<table class="table">
			<tr>
				<th>Course Id</th>
				<th>Course Name</th>
				<th>Batch</th>
				<th>Student Strength</th>
			</tr>

			<%
				ArrayList<Course> al = GeneralDAO.getCourses();
				for (int i = 0; i < al.size(); i++) {
					Course course = al.get(i);
					out.write("<tr id='row" + i + "'><td>" + course.getCourse_id() + "</td> <td>" + course.getCourse_name()
							+ "</td>");
					out.write("<td>" + course.getBatch() + "</td> <td>" + course.getNo_Of_Students() + "</td>");
					out.write("<td><button course_id='" + course.getCourse_id()
							+ "' class='btn btn-danger' type='button' id='deleteRow" + i + "'>Edit</button> </td>");
					out.write("<td><button class='btn btn-danger'>Delete</button> </td></tr>");
				}
			%>
		</table>
	</div>
	<div class="col-sm-6">
	<fieldset>
		<legend>Register New Course</legend>
		<form class="form-horizontal" role="form">
			<div class="form-group">
				<label class="control-label col-sm-2">Course Id</label>
				<div class="col-sm-4">
					<input id="course_id" type="text" class="form-control" name="msg"
						placeholder="Course id">
				</div>
			</div>
			<div class="form-group">
				<label class="control-label col-sm-2">Course Name</label>
				<div class="col-sm-4">
					<input id="course_name" type="text" class="form-control" name="msg"
						placeholder="Course name">
				</div>
			</div>
			<div class="form-group">
				<label class="control-label col-sm-2">Batch</label>
				<div class="col-sm-4">
					<input id="batch" type="text" class="form-control" name="msg"
						placeholder="Batch">
				</div>
			</div>
			<div class="form-group">
				<label class="control-label col-sm-2">No of students</label>
				<div class="col-sm-4">
					<input id="no_of_students" type="text" class="form-control" name="msg"
						placeholder="Number of students">
				</div>
			</div>
			<div class="form-group">
				<button class='btn btn-success' type='button' id="registerCourse">Add Course</button>
			</div>
			
			<div class="form-group">
				<button class='btn btn-danger' type='button' id="removeAllCourses">Delete all Courses</button>
			</div>
		</form>
		</fieldset>
	</div>
</div>
<%@include file="footer.jsp"%>