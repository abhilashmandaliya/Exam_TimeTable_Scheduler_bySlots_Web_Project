$(document).ready(function() {
	$(".nav li").on("click", function() {
		$(".nav li").removeClass("active");
		$(this).addClass("active");
	});
	$("[id^=deleteRow]").click(function() {
		$.ajax({
			url : 'Exam_TimeTable_Scheduler_bySlots_Web_Project/SlotManagement',
			type : 'post',
			data : {
				'action' : 'delete',
				'course' : $(this).attr('course'),
				'slot' : $(this).attr('slot')
			},
			success : function() {
				//$(("#row" +($(this).attr('id').substring(9)))).remove();
			}
		});
	});
	$("[id^=addCourse]").click(function(e) {
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
			},
			error : function() {
//				alert($(this));
//				$(("#row" +($(this).attr('id').substring(10)))).remove();
			}
		});
	});
	$("#generateAndDownloadTT").click(function(e) {
		$.ajax({
			url : 'Exam_TimeTable_Scheduler_bySlots_Web_Project/FileDownloadServlet',
			type : 'get',
			data : {},
			success : function(res) {
				window.location.href = res;
			},
			error : function() {
//				alert($(this));
//				$(("#row" +($(this).attr('id').substring(10)))).remove();
			}
		});
	});
});