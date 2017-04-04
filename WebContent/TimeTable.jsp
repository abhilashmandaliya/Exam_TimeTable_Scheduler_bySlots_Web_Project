<%@page import="org.TimeTable"%>
<%@include file="header.jsp"%>

<div>
	<div class="well">
		Upload Course Details
		<form role="form">
			<div class="form-group">
				<div class="col-sm-2">
					<label class="btn btn-info control-label"> Browse <input
						type="file" style="display: none;" id="courseDetails" name="resume" />
					</label>
				</div>
				<div class="col-sm-2">
					<button class='btn btn-warning' type="button" id='uploadCourseDetail'>Upload
						Course Detail</button>
				</div>
			</div>
		</form>
	</div>
</div>
<div>
	<div class="well">
		Download Exam Timetable
		<button class='btn btn-warning' id='generateAndDownloadTT'>Generate
			And Download TimeTable</button>
	</div>
</div>
<%@include file="footer.jsp"%>