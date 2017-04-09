<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="org.GeneralDAO"%>
<%@page import="java.util.ArrayList"%>
<%@page import="org.Course"%>
<%
	/*@page import="java.sql.*"*/
%>
<%@include file="header.jsp"%>
<div class="row">
	<div class="col-sm-7 availableCourse">
		<div class="alert alert-info">
			<strong>Available Courses</strong>
		</div>
		<table class="table">
			<tr>
				<th>Course Id</th>
				<th>Course Name</th>
				<th>Batch</th>
				<th>Student Strength</th>
				<th>Faculty Code</th>
				<th>Edit</th>
				<th>Delete</th>
			</tr>

			<%
				Map<Integer, String> batch_program = GeneralDAO.getBatchProgram();
				ArrayList<Course> al = GeneralDAO.getCourses();
				for (int i = 0; i < al.size(); i++) {
					Course course = al.get(i);
					out.write("<tr id='row" + i + "'><td>" + course.getCourse_id() + "</td> <td>" + course.getCourse_name()
							+ "</td>");
					out.write("<td batchNo='" + course.getBatch() + "'>"
							+ batch_program.get(Integer.parseInt(course.getBatch())) + "</td> <td>"
							+ course.getNo_Of_Students() + "</td>");
					out.write("<td>" + course.getFaculty() + "</td>");
					out.write("<td><button course_id='" + course.getCourse_id()
							+ "' class='btn btn-warning' type='button' data-toggle='modal' data-target='#myModal' id='editCourse"
							+ (i + 1) + "'>Edit</button> </td>");
					out.write("<td><button class='btn btn-danger' course_id='" + course.getCourse_id()
							+ "' id='deleteCourse" + (i + 1) + "'>Delete</button> </td></tr>");
				}
			%>
		</table>
	</div>
	<div class="col-sm-5">
		<fieldset>
			<legend>Register New Course</legend>
			<form class="form-horizontal" role="form">
				<div class="form-group">
					<label class="control-label col-sm-4">Course Id</label>
					<div class="col-sm-6">
						<input id="course_id" type="text" class="form-control" name="msg"
							placeholder="Course id">
					</div>
				</div>
				<div class="form-group">
					<label class="control-label col-sm-4">Course Name</label>
					<div class="col-sm-6">
						<input id="course_name" type="text" class="form-control"
							name="msg" placeholder="Course name">
					</div>
				</div>
				<div class="form-group">
					<label class="control-label col-sm-4">Batch</label>
					<div class="col-sm-6">
						<select id="batch" class="form-control">
							<%
								if (batch_program != null) {
									Iterator<Integer> batch_program_Iterator = batch_program.keySet().iterator();
									while (batch_program_Iterator.hasNext()) {
										int temp = batch_program_Iterator.next();
										out.write("<option value='" + temp + "'>" + batch_program.get(temp) + "</option>");
									}
								}
							%>
						</select>
					</div>
				</div>
				<div class="form-group">
					<label class="control-label col-sm-4">No of students</label>
					<div class="col-sm-6">
						<input id="no_of_students" type="text" class="form-control"
							name="msg" placeholder="Number of students">
					</div>
				</div>
				<div class="form-group">
					<label class="control-label col-sm-4">Faculty Code</label>
					<div class="col-sm-6">
						<input id="faculty" type="text" class="form-control" name="msg"
							placeholder="Faculty Code">
					</div>
				</div>
				<div class="form-group">
					<div class="col-sm-offset-4 col-sm-8">
						<button class='btn btn-success' type='button' id="registerCourse">Add
							Course</button>
						<button class='btn btn-danger' type='button' id="removeAllCourses">Delete
							all Courses</button>
					</div>
				</div>
			</form>
		</fieldset>
	</div>
</div>
<div class="modal fade" id="myModal" role="dialog">
	<div class="modal-dialog">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">&times;</button>
				<h4 class="modal-title">
					Edit Course <span id="editCourseModalSpan"></span>
				</h4>
			</div>
			<div class="modal-body">
				<form class="form-horizontal" role="form">
					<div class="form-group">
						<label class="control-label col-sm-4">Course Id</label>
						<div class="col-sm-6">
							<input id="edit_course_id" type="text" class="form-control"
								name="msg" placeholder="Course id" readonly>
						</div>
					</div>
					<div class="form-group">
						<label class="control-label col-sm-4">Course Name</label>
						<div class="col-sm-6">
							<input id="edit_course_name" type="text" class="form-control"
								name="msg" placeholder="Course name">
						</div>
					</div>
					<div class="form-group">
						<label class="control-label col-sm-4">Batch</label>
						<div class="col-sm-6">
							<select id="edit_batch" class="form-control">
								<%
									if (batch_program != null) {
										Iterator<Integer> batch_program_Iterator = batch_program.keySet().iterator();
										while (batch_program_Iterator.hasNext()) {
											int temp = batch_program_Iterator.next();
											out.write("<option value='" + temp + "'>" + batch_program.get(temp) + "</option>");
										}
									}
								%>
							</select>
						</div>
					</div>
					<div class="form-group">
						<label class="control-label col-sm-4">No of students</label>
						<div class="col-sm-6">
							<input id="edit_no_of_students" type="text" class="form-control"
								name="msg" placeholder="Number of students">
						</div>
					</div>
					<div class="form-group">
						<label class="control-label col-sm-4">Faculty Code</label>
						<div class="col-sm-6">
							<input id="edit_faculty" type="text" class="form-control"
								name="msg" placeholder="Faculty Code">
						</div>
					</div>
				</form>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-success"
					id="updateCourseDetail" data-dismiss="modal">Update</button>
				<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
			</div>
		</div>
	</div>
</div>
<%@include file="footer.jsp"%>