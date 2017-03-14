<%@page import="org.GeneralDAO"%>
<%@page import="java.util.ArrayList"%>
<%@page import="org.Room" %>
<%/*@page import="java.sql.*"*/ %>
<%@include file="header.jsp" %>
<div class="row">
	<div class="col-sm-6">
		<div class="alert alert-info">
			<strong>Available Rooms</strong>
		</div>
		<table class="table">
			<tr>	
			<th>Room No</th>
			<th>Capacity</th>			
			</tr>
		
		<%
		ArrayList<Room> al=GeneralDAO.getRooms();
		for(int i=0;i<al.size();i++)
		{
			Room room=al.get(i);
			out.write("<tr id='row" +i+"'><td>"+room.getRoom_no()+"</td> <td>"+room.getCapacity()+"</td>");
			out.write("<td><button room_no='" + room.getRoom_no() + "' room_capacity='" + room.getCapacity()
			+ "' class='btn btn-danger' type='button' id='deleteRow" + i + "'>Edit</button> </td>");
			out.write("<td><button room_no='" + room.getRoom_no() + "' room_capacity='" + room.getCapacity()
					+ "' class='btn btn-danger' type='button' id='deleteRow" + i + "'>Delete</button> </td></tr>");
		}
		
		%>
		</table>
	</div>
	<div class="col-sm-6">
		 <table>
		 <tr>
		   <td>
			   <div class="input-group">
			    <span class="input-group-addon">Room No</span>
			    <input id="msg" type="text" class="form-control" name="msg" placeholder="Please enter room number">
			  </div>
			</td>
			<td>
		   <div class="input-group">
		    <span class="input-group-addon">Capacity</span>
		    <input id="msg" type="text" class="form-control" name="msg" placeholder="Please enter room capacity">
		  </div>
		  </td>
		  <%
		out.write("<td> <button class='btn btn-success' type='button'> Add Room</button> </td></tr>");
			
		%>
		</table>
		
		
		




		
		
		
		
	</div>
	</div>
<%@include file="footer.jsp" %>