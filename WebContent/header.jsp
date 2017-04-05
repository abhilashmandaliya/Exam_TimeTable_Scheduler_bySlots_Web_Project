<%@page import="org.Authenticator"%>
<%@include file="coreHeader.jsp"%>
<%
	if (!Authenticator.isAuthorized(request.getSession(), this.getClass().getName()))
		response.sendRedirect("login.jsp");
	System.out.println(this.getClass().getName());
%>
<nav class="navbar navbar-default">
	<div class="container-fluid">
		<div class="navbar-header">
			<a class="navbar-brand" href="Home.jsp">DA-IICT</a>
		</div>
		<ul class="nav navbar-nav">
			<li class="dropdown"><a class="dropdown-toggle"
				data-toggle="dropdown" href="#">Data Management <span
					class="caret"></span></a>
				<ul class="dropdown-menu">
					<!--<li><a href="Home.jsp">Home</a></li>-->
					<li><a href="Slot.jsp">Slot Details</a></li>
					<li><a href="Course.jsp">Course Details</a></li>
					<li><a href="Room.jsp">Room Details</a></li>
				</ul></li>
			<li><a href="TimeTable.jsp">TimeTable Management</a></li>
		</ul>
		<ul class="nav navbar-nav navbar-right">
			<li><a href="LoginServlet?action=logout"><span class="glyphicon glyphicon-log-in"></span> Logout</a></li>
		</ul>
	</div>
</nav>

<div class="container">