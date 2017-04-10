<%@include file="header.jsp"%>
<div class="login-container">
	<h1 class="label label-danger">User Registration</h1>
	<div id="output"></div>
	<div class="avatar"></div>
	<div class="form-box">
		<form action="LoginServlet" method="post">
			<input name="user" id="uname" type="text" placeholder="username">
			<input type="password" id="password" name="password"
				placeholder="password">
			<button class="btn btn-info btn-block login" type="button"
				id="userRegistration">Register</button>
		</form>
	</div>
</div>
<%@include file="footer.jsp"%>