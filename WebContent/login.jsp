<%@include file="coreHeader.jsp"%>
<div class="container">
	<div class="login-container">
            <div id="output"></div>
            <div class="avatar"></div>
            <div class="form-box">
                <form action="LoginServlet" method="post">
                    <input name="user" id="user" type="text" placeholder="username">
                    <input type="password" id="password" name="password" placeholder="password">
                    <button class="btn btn-info btn-block login" type="button" id="login">Login</button>
                </form>
            </div>
        </div>
        
</div>
<%@include file="footer.jsp"%>