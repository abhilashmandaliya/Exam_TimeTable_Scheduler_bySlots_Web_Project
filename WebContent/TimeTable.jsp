<%@page import="org.TimeTable"%>
<%@include file="header.jsp"%>
<div class="panel panel-primary">
	<div class="panel-heading">Upload Course Details</div>
	<div class="panel-body">
		<form role="form" class="form-inline">
			<div class="form-group">
				<label>Select File:</label> <label
					class="btn btn-info control-label"> Browse <input
					type="file" style="display: none;" name="courseDetails"
					id="courseDetails" />
				</label>
			</div>
			<div class="form-group">
				<button class='btn btn-warning' type="button"
					id='uploadCourseDetail'>Upload Course Detail</button>
			</div>
			<input type="hidden" name="file" value="examdetails" />
		</form>
	</div>
</div>

<div class="panel panel-primary">
	<div class="panel-heading">Upload Slot Details</div>
	<div class="panel-body">
		<form role="form" class="form-inline">
			<div class="form-group">
				<label>Slot No:</label> <select id="slot_no" class="form-control">
					<option value="1">1</option>
					<option value="2">2</option>
					<option value="3">3</option>
					<option value="4">4</option>
					<option value="5">5</option>
					<option value="6">6</option>
					<option value="7">7</option>
					<option value="8">8</option>
				</select>
			</div>
			<div class="form-group">
				<label>Select File:</label> <label
					class="btn btn-info control-label"> Browse <input
					type="file" style="display: none;" id="slotDetails" />
				</label>
			</div>
			<div class="form-group">
				<button class='btn btn-warning' type="button" id='uploadSlotDetail'>Upload
					Slot Detail</button>
			</div>
		</form>
	</div>
</div>

<div class="panel panel-primary">
	<div class="panel-heading">Generate and Download Exam Timetable</div>
	<div class="panel-body">
		<label>Exam Type:</label> 
		<select id="exam_type" class="form-control">
			<option value="insem">In Semester</option>
			<option value="endsem">End Semester</option>
		</select>
		<button class='btn btn-warning' type="button" id='generateTT'>Generate
			TimeTable</button>
		<button class='btn btn-warning' type="button" id='downloadTT'>Download
			TimeTable</button>
	</div>
</div>
<%@include file="footer.jsp"%>