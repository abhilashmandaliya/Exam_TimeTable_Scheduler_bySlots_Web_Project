$(document).ready(function() {
	$(".nav li").on("click", function() {
		$(".nav li").removeClass("active");
		$(this).addClass("active");
	});
	$(document).on("click","[id^=deleteRow]",function(e) {
		$.ajax({
			url : 'Exam_TimeTable_Scheduler_bySlots_Web_Project/SlotManagement',
			type : 'post',
			data : {
				'action' : 'delete',
				'course' : $(this).attr('course'),
				'slot' : $(this).attr('slot')
			},
			success : function(data) {
				var clicked = e.target.id || this.id;
				$('#'+clicked).parent().parent().hide();
				var row = $('#'+clicked).parent().parent().html();
				$('#remainingCourses').append("<tr>"+row+"</tr>");
				$('#remainingCourses tr:last td:last').remove();
				$('#remainingCourses tr:last').append("<td>"+data+"</td>");
				$('#remainingCourses tr:last').append($('#remainingCourses tr:last').prev().clone().find("td:last"));
				$('#remainingCourses tr:last td:last button').attr('course',$('#remainingCourses tr:last td:first').text());
				$('#remainingCourses tr:last td:last button').attr('id',"row"+($('#remainingCourses tr').length-1));
			}
		});
	});
	$(document).on("click","[id^=addCourse]",function(e) {
		$.ajax({
			url : 'Exam_TimeTable_Scheduler_bySlots_Web_Project/SlotManagement',
			type : 'post',
			data : {
				'action' : 'add',
				'course' : $(this).attr('course'),
				'slot' : $(this).attr('slot')
			},
			success : function() {
				var clicked = e.target.id || this.id;
				$('#'+clicked).parent().parent().hide();
				var row = $('#'+clicked).parent().parent().html();
				$('#courseIncluded').append("<tr>"+row+"</tr>");
				$('#courseIncluded tr:last td:last').remove();
				$('#courseIncluded tr:last td:last').remove();
				$('#courseIncluded tr:last').append($('#courseIncluded tr:last').prev().clone().find("td:last"));
				$('#courseIncluded tr:last td:last button').attr('course',$('#courseIncluded tr:last td:first').text());
				$('#courseIncluded tr:last td:last button').attr('id',"deleteRow"+($('#courseIncluded tr').length-1));
			}
		});
	});
	$("#generateAndDownloadTT").click(function(e) {
		window.location.href = "http://localhost:8080/Exam_TimeTable_Scheduler_bySlots_Web_Project/FileDownloadServlet";
	});
	
	$('#uploadCourseDetail').click(function(){
			var form_data = new FormData();
			if(! ($('#courseDetails').prop('files')[0]==undefined) ) {
				var file_data = $('#courseDetails').prop('files')[0];				
				form_data.append('courseDetails',file_data);
			}
			$.ajax({
				url : 'Exam_TimeTable_Scheduler_bySlots_Web_Project/FileUploadServlet',
				type : 'post',
				cache : false,
				contentType : false,
				processData : false,
				data : form_data,
				success : function(data){
					alert(data);
				}
			});
	});
	$("#registerCourse").click(function(e){
		var course_id = $('#course_id').val();
		var course_name = $('#course_name').val();
		var batch = $('#batch').val();
		var no_of_students = $('#no_of_students').val();
		$.ajax({
			url : 'Exam_TimeTable_Scheduler_bySlots_Web_Project/CourseServlet',
			type : 'post',
			data : {"course_id":course_id,"course_name":course_name,"batch":batch,"no_of_students":no_of_students},
			success : function(){
				
			}
		});
	});
});