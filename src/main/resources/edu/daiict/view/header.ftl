<#import "core-header.ftl" as coreHeader>
<#macro header>
<@coreHeader.coreHeader />
<nav class="navbar navbar-default navbar-font">
<div class="container-fluid">
<div class="navbar-header">
<a class="navbar-brand" href="/home">
<img style="margin-top:-8px" width="40px" height="40px" src= "/static/images/b3.png" />
</a>
</div>
<ul class="nav navbar-nav">
<li class="dropdown">
<a class="dropdown-toggle" data-toggle="dropdown" href="#">Data Management <span class="caret"></span></a>
<ul class="dropdown-menu">
<li><a href="/slot">Slot Details</a></li>
<li><a href="/course">Course Details</a></li>
<li><a href="/room">Room Details</a></li>
<li><a href="/batch-program">Batch-Program Details</a></li>
</ul>
</li>
<li><a href="/timeTable">TimeTable Management</a></li>
<li class="dropdown">
<a class="dropdown-toggle" data-toggle="dropdown" href="#">User Management <span class="caret"></span></a>
<ul class="dropdown-menu">
<li><a href="/user-registration">User Registration</a></li>
<li><a href="/password-reset">Password Reset</a></li>
</ul>
</li>
</ul>
<ul class="nav navbar-nav navbar-right">
<li>
<a href="/logout"><span class="glyphicon glyphicon-log-in"></span> Logout</a>
</li>
</ul>
</div>
</nav>

<div class="container">
</#macro>