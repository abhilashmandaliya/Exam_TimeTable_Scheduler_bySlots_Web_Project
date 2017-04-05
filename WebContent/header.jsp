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
			<a class="navbar-brand" href="#">WebSiteName</a>
		</div>
		<ul class="nav navbar-nav">
			<li><a href="Home.jsp">Home</a></li>
			<li><a href="Slot.jsp">Slot Details</a></li>
			<li><a href="Course.jsp">Course Details</a></li>
			<li><a href="Room.jsp">Room Details</a></li>
			<li><a href="TimeTable.jsp">Generate Time Table</a></li>
		</ul>
	</div>
</nav>

<div class="container">