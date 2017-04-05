<%@include file="coreHeader.jsp"%>
<div class="container">
	<div class="login-container">
            <div id="output"></div>
            <div class="avatar"></div>
            <div class="form-box">
                <form action="LoginServlet" method="post">
                    <input name="user" type="text" placeholder="username">
                    <input type="password" name="password" placeholder="password">
                    <button class="btn btn-info btn-block login" type="submit">Login</button>
                </form>
            </div>
        </div>
        
</div>
<%@include file="footer.jsp"%>