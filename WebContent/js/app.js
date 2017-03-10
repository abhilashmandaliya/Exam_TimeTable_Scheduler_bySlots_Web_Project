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
	$("[id^=addCourse]").click(function() {
		$.ajax({
			url : 'Exam_TimeTable_Scheduler_bySlots_Web_Project/SlotManagement',
			type : 'post',
			data : {
				'action' : 'add',
				'course' : $(this).attr('course'),
				'slot' : $(this).attr('slot')
			},
			success : function() {
//				console.log( $(this) );
//				$(("#row" +($(this).attr('id').substring(10)))).remove();
			},
			error : function() {
//				alert($(this));
//				$(("#row" +($(this).attr('id').substring(10)))).remove();
			}
		});
	});
});