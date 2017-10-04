<%@page import="org.Authenticator"%>
<%@include file="coreHeader.jsp"%>
<%
	if (!Authenticator.isAuthorized(request.getSession(), this.getClass().getName()))
		response.sendRedirect("login.jsp");
%>
<nav class="navbar navbar-default navbar-font">
	<div class="container-fluid">
		<div class="navbar-header">
			<a class="navbar-brand" href="Home.jsp">
				<img style="margin-top:-8px" width="40px" height="40px" src= "images/da_logo.jpg" />
			</a>
		</div>
		<ul class="nav navbar-nav">
			<li class="dropdown"><a class="dropdown-toggle"
				data-toggle="dropdown" href="#">Data Management <span
					class="caret"></span></a>
				<ul class="dropdown-menu">
					<li><a href="Slot.jsp">Slot Details</a></li>
					<li><a href="Course.jsp">Course Details</a></li>
					<li><a href="Room.jsp">Room Details</a></li>
					<li><a href="BatchProgram.jsp">Batch-Program Details</a></li>
				</ul></li>
			<li><a href="TimeTable.jsp">TimeTable Management</a></li>
			<li class="dropdown"><a class="dropdown-toggle"
				data-toggle="dropdown" href="#">User Management <span
					class="caret"></span></a>
				<ul class="dropdown-menu">
					<li><a href="UserRegistration.jsp">User Registration</a></li>
					<li><a href="PasswordReset.jsp">Password Reset</a></li>
				</ul></li>
		</ul>
		<ul class="nav navbar-nav navbar-right">
			<li><a href="LoginServlet?action=logout"><span class="glyphicon glyphicon-log-in"></span> Logout</a></li>
		</ul>
	</div>
</nav>

<div class="container">