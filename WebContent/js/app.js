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
	$("#generateTT").click(function(e) {
		$.ajax({
			url : 'http://localhost:8080/Exam_TimeTable_Scheduler_bySlots_Web_Project/FileDownloadServlet',
			type : 'get',
			data : {'action':'generatett'},
			success : function(data) {
				alert("Server Response : "+data);
			}, error : function(data) {
				alert("Error : "+data);
			}
		});
	});
	$("#downloadTT").click(function(e) {
		window.location.href='http://localhost:8080/Exam_TimeTable_Scheduler_bySlots_Web_Project/FileDownloadServlet?action=downloadtt';
	});
	$('#uploadCourseDetail').click(function(){
			var form_data = new FormData();
			if(! ($('#courseDetails').prop('files')[0]==undefined) ) {
				form_data.append('file','examdetails');
				form_data.append('courseDetails',$('#courseDetails').prop('files')[0]);				
			}
			$.ajax({
				url : 'Exam_TimeTable_Scheduler_bySlots_Web_Project/FileUploadServlet',
				type : 'POST',
				contentType : false,
				processData : false,
				cache : false,
				data : form_data,
				success : function(data){
					alert("File Uploaded Successfully !");
				}
			});
	});
	$('#uploadSlotDetail').click(function(){
		var form_data = new FormData();
		if(! ($('#slotDetails').prop('files')[0]==undefined) ) {
			form_data.append('slot_no',$('#slot_no').val());
			form_data.append('file','slotdetails');
			form_data.append('slotDetails',$('#slotDetails').prop('files')[0]);			
		}
		$.ajax({
			url : 'Exam_TimeTable_Scheduler_bySlots_Web_Project/FileUploadServlet',
			type : 'POST',
			contentType : false,
			processData : false,
			cache : false,
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
	$(document).on("click","[id^=editCourse]",function(e){
		var clicked = e.target.id || this.id;
		var course_id = $('#'+clicked).parent().parent().find('td:first').text();
		var course_name = $('#'+clicked).parent().parent().find('td:first').next().text();
		var batch = $('#'+clicked).parent().parent().find('td:first').next().next().attr('batchNo');
		var no_of_students = $('#'+clicked).parent().parent().find('td:first').next().next().next().text();
		$('#editCourseModalSpan').html(course_id+"-"+course_name);
		$('#edit_course_id').val(course_id);
		$('#edit_course_name').val(course_name);
		$('#edit_batch').val(batch);
		$('#edit_no_of_students').val(no_of_students);
	});
	$(document).on("click","[id^=editRoom]",function(e){
		var clicked = e.target.id || this.id;
		var room_no = $('#'+clicked).parent().parent().find('td:first').text();
		var capacity = $('#'+clicked).parent().parent().find('td:first').next().text();
		$('#editRoomModalSpan').html(room_no);
		$('#edit_room_no').val(room_no);
		$('#edit_capacity').val(capacity);
	});
});