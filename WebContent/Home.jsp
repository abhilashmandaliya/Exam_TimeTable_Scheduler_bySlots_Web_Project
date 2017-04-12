<%@page import="java.util.ArrayList"%>
<%@page import="org.Slot"%>
<%@page import="org.Course"%>
<%@include file="header.jsp"  %>
<h1>Welcome to DA-IICT Exam TimeTable Scheduler</h1>
<h2>Scheduling Rules</h2>
<h3>1. In InSem, students should not have consecutive exams if they are from different slots</h3>
<h3>2. In InSem ,students should not have exams in the morning if they are giving exam in last time interval of previous day</h3>
<h3>3. In End Sem, students who are giving exam in the evening should not have exams in the next day morning. </h3>
<h3>4. In End Sem, maximum number of exams should be scheduled in the morning </h3>
<h3>5. There should be maximum number of invigilations by ensuring one entire course is allocated in a single room(if possible)</h3>
<h3>6. Rooms are allocated in descending order of capacity</h3>
<h3><BR> Data Entry Format:</h3>
<h3>Course File:(course name,course id,number of students,batch code)</h3>
<h3>Slot File: (course id,faculty)</h3>

<%@include file="footer.jsp"  %>