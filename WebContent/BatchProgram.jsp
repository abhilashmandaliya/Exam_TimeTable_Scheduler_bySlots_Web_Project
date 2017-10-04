<%@page import="java.util.Set"%>
<%@page import="java.util.Map"%>
<%@page import="org.GeneralDAO"%>
<%
	/*@page import="java.sql.*"*/
%>
<%@include file="header.jsp"%>
<div class="row">
	<div class="col-sm-6">
		<div class="alert alert-info">
			<strong>Batch-Program Mapping</strong>
		</div>
		<div class="availableBatchProgram">
			<table class="table">
				<tr>
					<th>Batch No</th>
					<th>Program</th>
					<th>Edit</th>
					<th>Delete</th>
				</tr>

				<%
					Map<Integer, String> batch_program = GeneralDAO.getBatch_Program();
					Set<Integer> keys = batch_program.keySet();
					int i = 1;
					for (int key : keys) {
						out.write("<tr id='row" + (i++) + "'><td>" + key + "</td> <td>" + batch_program.get(key) + "</td>");
						out.write("<td><button batch_program_no='" + key
								+ "' class='btn btn-warning' type='button' data-toggle='modal' data-target='#myModal' id='editBatchProgram"
								+ (i) + "'>Edit</button> </td>");
						out.write("<td><button batch_program_no='" + key
								+ "' class='btn btn-danger' type='button' id='deleteBatchProgram" + i
								+ "'>Delete</button> </td></tr>");
					}
				%>
			</table>
		</div>
	</div>
	<div class="col-sm-6">
		<fieldset>
			<legend>Map Batch-Program</legend>
			<form class="form-horizontal" role="form">
				<div class="form-group">
					<label class="control-label col-sm-2">Batch No</label>
					<div class="col-sm-5">
						<input id="batch_no" type="text" class="form-control" name="msg"
							placeholder="Please enter batch no">
					</div>
				</div>
				<div class="form-group">
					<label class="control-label col-sm-2">Program</label>
					<div class="col-sm-5">
						<input id="program" type="text" class="form-control" name="msg"
							placeholder="Please enter program">
					</div>
				</div>
				<div class="form-group">
					<div class="col-sm-offset-2 col-sm-5">
						<button type="button" id="mapbatchProgram" class="btn btn-success">Map
							Batch-Program</button>
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
					Re-Map Batch-Program <span id="editBatchProgramSpan"></span>
				</h4>
			</div>
			<div class="modal-body">
				<form class="form-horizontal" role="form">
					<div class="form-group">
						<label class="control-label col-sm-2">Batch No</label>
						<div class="col-sm-5">
							<input id="edit_batch_no" type="text" class="form-control"
								name="msg" placeholder="Please enter room number" readonly>
						</div>
					</div>
					<div class="form-group">
						<label class="control-label col-sm-2">Program</label>
						<div class="col-sm-5">
							<input id="edit_program" type="text" class="form-control"
								name="msg" placeholder="Please enter room capacity">
						</div>
					</div>
				</form>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-success"
					id="updateBatchProgram" data-dismiss="modal">Update</button>
				<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
			</div>
		</div>
	</div>
</div>
<%@include file="footer.jsp"%>