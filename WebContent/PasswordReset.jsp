<%@page import="org.GeneralDAO"%>
<%@page import="java.util.ArrayList"%>
<%@include file="header.jsp"%>
<div class="login-container">
	<h1 class="label label-danger">Password Reset</h1>
	<div id="output"></div>
	<div class="avatar"></div>
	<div class="form-box">
		<form action="LoginServlet" method="post">
			<select id="uname">
				<%
					ArrayList<String> users = GeneralDAO.getUsers();
					for (String user : users)
						out.write("<option value='" + user + "'>" + user + "</option>");
				%>
			</select> <input type="password" id="password" name="password"
				placeholder="password">
			<button class="btn btn-info btn-block login" type="button"
				id="resetPassword">Reset Password</button>
		</form>
	</div>
</div>
<%@include file="footer.jsp"%>