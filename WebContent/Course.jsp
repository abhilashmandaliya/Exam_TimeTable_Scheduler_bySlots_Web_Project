<%@page import="org.GeneralDAO"%>
<%@page import="java.util.ArrayList"%>
<%@page import="org.Course" %>
<%/*@page import="java.sql.*"*/ %>
<%@include file="header.jsp" %>
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
		ArrayList<Course> al=GeneralDAO.getCourses();
		for(int i=0;i<al.size();i++)
		{
			Course course=al.get(i);
			out.write("<tr id='row" +i+"'><td>"+course.getCourse_id()+"</td> <td>"+course.getCourse_name()+"</td>");
			out.write("<td>"+course.getBatch()+"</td> <td>"+course.getNo_Of_Students()+"</td>");
			out.write("<td><button course_id='" + course.getCourse_id() + "' class='btn btn-danger' type='button' id='deleteRow" + i + "'>Edit</button> </td>");
			out.write("<td><button class='btn btn-danger'>Delete</button> </td></tr>");
		}
		
		%>
		</table>
	</div>
	<div class="col-sm-6">
		 <table>
		 <tr>
		   <td>
			   <div class="input-group">
			    <span class="input-group-addon">Course Id</span>
			    <input id="msg" type="text" class="form-control" name="msg" placeholder="Please enter course id">
			  </div>
			</td>
			</tr>
			<tr>
			<td>
		   <div class="input-group">
		    <span class="input-group-addon">Course Name</span>
		    <input id="msg" type="text" class="form-control" name="msg" placeholder="Please enter course name">
		  </div>
		  </td>
		  </tr>
		  <tr>
		  <td>
			   <div class="input-group">
			    <span class="input-group-addon">Batch</span>
			    <input id="msg" type="text" class="form-control" name="msg" placeholder="Please enter batch">
			  </div>
			</td>
			
			</tr>
			<tr>
			<td>
			   <div class="input-group">
			    <span class="input-group-addon">No of students</span>
			    <input id="msg" type="text" class="form-control" name="msg" placeholder="Please enter number of students">
			  </div>
			</td>
			</tr>
			<tr>
		  <%
		out.write("<td> <button class='btn btn-success' type='button'> Add Course</button> </td></tr>");
			
		%>
		</table>
		
		
		




		
		
		
		
	</div>
	</div>
<%@include file="footer.jsp" %>