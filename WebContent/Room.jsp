<%@page import="org.RoomUI"%>
<%@page import="java.util.Comparator"%>
<%@page import="java.util.Collections"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.Map"%>
<%@page import="org.GeneralDAO"%>
<%@page import="java.util.ArrayList"%>
<%@page import="org.Room"%>
<%
	/*@page import="java.sql.*"*/
%>
<%@include file="header.jsp"%>
<div class="row">
	<div class="col-sm-6">
		<div class="alert alert-info">
			<strong>Available Rooms</strong>
		</div>
		<div class="availableRoom">
			<table class="table">
				<tr>
					<th>Room No</th>
					<th>Capacity</th>
					<!-- <th>Use Room</th>-->
					<th>Room Priority</th>
					<th>Edit</th>
					<th>Delete</th>
				</tr>

				<%
					ArrayList<RoomUI> al = GeneralDAO.getRoomsForUI();
					int i = 0;
					int size = al.size();
					for (RoomUI room : al) {
						Boolean status = room.is_active;
						int priority = room.priority;
						out.write("<tr id='row" + i + "'><td>" + room.room.getRoom_no() + "</td> <td>" + room.room.getCapacity()
								+ "</td>");
						/*
						if (!status)
							out.write("<td><div class='checkbox'><label><input type='checkbox' id='activateRoom" + i
									+ "' room_no='" + room.room.getRoom_no() + "'></label></div>");
						else
							out.write("<td><div class='checkbox'><label><input type='checkbox' id='activateRoom" + i
									+ "' room_no='" + room.room.getRoom_no() + "' checked='true'></label></div>");
						*/
						out.write("<td><select room_no='" + room.room.getRoom_no() + "' class='form-control' id='priority" + i
								+ "'><option value='0'>-</option>");
						for (int j = 1; j <= size; j++) {
							if (priority == j)
								out.write("<option selected value='" + j + "'>" + j + "</option>");
							else
								out.write("<option value='" + j + "'>" + j + "</option>");
						}
						out.write("</select></td>");
						out.write("<td><button room_no='" + room.room.getRoom_no()
								+ "' class='btn btn-warning' type='button' data-toggle='modal' data-target='#myModal' id='editRoom"
								+ (i + 1) + "'>Edit</button> </td>");
						out.write("<td><button room_no='" + room.room.getRoom_no()
								+ "' class='btn btn-danger' type='button' id='deleteRoom" + i + "'>Delete</button> </td></tr>");
						i++;
					}
				%>
			</table>
		</div>
	</div>
	<div class="col-sm-6">
		<fieldset>
			<legend>Register New Room</legend>
			<form class="form-horizontal" role="form">
				<div class="form-group">
					<label class="control-label col-sm-2">Room No</label>
					<div class="col-sm-5">
						<input id="room_no" type="text" class="form-control" name="msg"
							placeholder="Please enter room number">
					</div>
				</div>
				<div class="form-group">
					<label class="control-label col-sm-2">Capacity</label>
					<div class="col-sm-5">
						<input id="capacity" type="text" class="form-control" name="msg"
							placeholder="Please enter room capacity">
					</div>
				</div>
				<div class="form-group">
					<div class="col-sm-offset-2 col-sm-5">
						<button type="button" id="addRoom" class="btn btn-success">Add
							Room</button>
					</div>
				</div>
			</form>
		</fieldset>
	</div>
</div>
<div class="modal fade" id="myModal" role="dialog">
	<div class="modal-dialog">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">&times;</button>
				<h4 class="modal-title">
					Edit Room <span id="editRoomModalSpan"></span>
				</h4>
			</div>
			<div class="modal-body">
				<form class="form-horizontal" role="form">
					<div class="form-group">
						<label class="control-label col-sm-2">Room No</label>
						<div class="col-sm-5">
							<input id="edit_room_no" type="text" class="form-control"
								name="msg" placeholder="Please enter room number" readonly>
						</div>
					</div>
					<div class="form-group">
						<label class="control-label col-sm-2">Capacity</label>
						<div class="col-sm-5">
							<input id="edit_capacity" type="text" class="form-control"
								name="msg" placeholder="Please enter room capacity">
						</div>
					</div>
				</form>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-success" id="updateRoomDetail"
					data-dismiss="modal">Update</button>
				<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
			</div>
		</div>
	</div>
</div>
<%@include file="footer.jsp"%>