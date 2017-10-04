<%@page import="java.util.ArrayList"%>
<%@page import="org.Slot"%>
<%@page import="org.Course"%>
<%@include file="header.jsp"  %>
<h2>Welcome to DA-IICT Exam TimeTable Scheduler</h2>
<h3>Scheduling Rules</h3>
<h4>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1. In InSem, students should not have consecutive exams if they are from different slots.</h4>
<h4>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2. In InSem ,students should not have exams in the morning if they are giving exam in last time interval of previous day.</h4>
<h4>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;3. In End Sem, students who are giving exam in the evening should not have exams in the next day morning. </h4>
<h4>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;4. In End Sem, maximum number of exams should be scheduled in the morning.</h4>
<h4>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;5. There should be optimum invigilations by engaging minimum number of rooms.</h4>
<h4>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;6. Rooms are allocated in ascending order of room priority values set in 'Room Details' section</h4>
<h4>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;7. To use second floor rooms, least priority can be given to them.</h4>
<h4>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;8. Courses are selected in descending order of students strength except when smaller ones have more priority.</h4>
<h4>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;9. In above rule, courses in same time slots are reallocated to minimize no of rooms.</h4>

<h4><BR> Data Entry Format:</h4>
<h4>Course File:(course name,course id,number of students,batch code)</h4>
<h4>Slot File: (course id,faculty)</h4>
<h4>*These files are required to be in .xlsx format only</h4>
<%@include file="footer.jsp"  %>