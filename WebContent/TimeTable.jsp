<%@page import="org.TimeTable"%>
<%@include file="header.jsp"%>

<div class="panel panel-primary">
	<div class="panel-heading">Upload Course Details</div>
	<div class="panel-body">
		<form role="form">
			<div class="form-group">
				<div class="col-sm-2">
					<label class="btn btn-info control-label"> Browse <input
						type="file" style="display: none;" id="courseDetails"
						name="resume" />
					</label>
				</div>
				<div class="col-sm-2">
					<button class='btn btn-warning' type="button"
						id='uploadCourseDetail'>Upload Course Detail</button>
				</div>
			</div>
		</form>
	</div>
</div>

<div class="panel panel-primary">
	<div class="panel-heading">Download Exam Timetable</div>
	<div class="panel-body">
		<button class='btn btn-warning' id='generateAndDownloadTT'>Generate
			And Download TimeTable</button>
	</div>
</div>
<%@include file="footer.jsp"%>