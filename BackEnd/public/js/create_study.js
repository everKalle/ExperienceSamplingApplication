/*var lang = {
		'question-type' : '<?php echo $this->lang->line('question-type'); ?>',
		'question-type-freetext' : '<?php echo $this->lang->line('question-type-freetext'); ?>',
		'question-type-multichoice' : '<?php echo $this->lang->line('question-type-multichoice'); ?>',
		'multichoice-can-choose' : '<?php echo $this->lang->line('multichoice-can-choose'); ?>',
		'multichoice-one-choice' : '<?php echo $this->lang->line('multichoice-one-choice'); ?>',
		'multichoice-multi-choice' : '<?php echo $this->lang->line('multichoice-multi-choice'); ?>',
		'multichoice-title' : '<?php echo $this->lang->line('multichoice-title'); ?>',
		'multichoice-title-placeholder' : '<?php echo $this->lang->line('multichoice-title-placeholder'); ?>',
		'freetext-question' : '<?php echo $this->lang->line('freetext-question'); ?>'
	}*/

var questionCount = 0;

function add_question(){
	$("#questionHolder").append('<div class="panel panel-default" id="question-panel-' + questionCount + '">' + 
		  '<div class="panel-body">' +
		  	'<div class="form-horizontal">' +
		      '<div class="form-group">' +
		        '<label class="control-label col-sm-3" for="question-type-' + questionCount + '">' + lang['question-type'] +': </label>' +
		        '<div class="col-sm-7">' + 
				    '<select class="form-control input-sm" name="question[' + questionCount + '][question-type]" id="question-type-' + questionCount + '" onChange="modifyType(' + questionCount + ');">' +
				      '<option value="freetext" selected>' + lang['question-type-freetext'] +'</option>' + 
				      '<option value="multichoice">' + lang['question-type-multichoice'] +'</option>' + 
				    '</select>' + 
			    '</div>' + 
		      '</div>' + 
		      	'<div class="form-group">' + 
			        '<label class="control-label col-sm-3" for="question-title-' + questionCount + '">' + lang['freetext-question'] +': </label>' + 
			        '<div class="col-sm-7">' + 
			          '<input class="form-control" type="text" id="question-title-' + questionCount + '" name="question[' + questionCount + '][question-title]" size="100" placeholder="' + lang['freetext-question'] +'" required/>' + 
			        '</div>' + 
			      '</div>' + 
			  '<div id="question-data-holder-' + questionCount + '">' + 
		      '</div>' + 
		    '</div>' + 
		  '</div>' + 
		'</div>');
	modifyType(questionCount);
	$('#question-panel-' + questionCount).hide();
	$('#question-panel-' + questionCount).slideDown(200);
	questionCount++;
	$("#study-question-count").val(questionCount);
	$("#question-remove-button").show();
	$("#submit-button").prop('disabled', false);
	$('#alert-question-add').slideUp(200);
}

function remove_question(){
	var r = confirm(lang['remove-question-confirm']);
	if (r) {
		if (questionCount > 0){
			$("#question-panel-" + (questionCount-1)).remove();
			questionCount--;
			$("#study-question-count").val(questionCount);
			if (questionCount == 0){
				$("#question-remove-button").hide();
				$("#submit-button").prop('disabled', true);
				$('#alert-question-add').slideDown(200);
			}
		}
	}
}

function modifyType(index){
	var type = $("#question-type-" + index).val();
	if (type == "freetext"){
		$("#question-data-holder-" + index).html('');
	} else if (type == "multichoice") {
		$("#question-data-holder-" + index).html('<hr>'+
						'<div class="form-group">'+
					    '<label class="control-label col-sm-3" for="question-multichoice-single-choice-' + index + '">' + lang['multichoice-can-choose'] +': </label>'+
					      '<div class="col-sm-7">'+
					      	'<input type="radio" name="question[' + index + '][question-multichoice-single-choice]" value="1" checked> ' + lang['multichoice-one-choice'] +'<br>' +
          					'<input type="radio" name="question[' + index + '][question-multichoice-single-choice]" value="0"> ' + lang['multichoice-multi-choice'] +
					    '</div>'+
					  '</div>' +
						'<hr>'+
			  		  '<div class="form-group">'+
					    '<label class="control-label col-sm-3" for="question-multichoice-' + index + '-0">' + lang['multichoice-title'] +': </label>'+
					      '<div class="col-sm-7">'+
					      '<input class="form-control" type="text" id="question-multichoice-' + index + '-0" name="question[' + index + '][question-multichoice-0]" size="100" placeholder="' + lang['multichoice-title-placeholder'] +' 1" required/>'+
					    '</div>'+
					  '</div>'+
					  '<div class="form-group">'+
					    '<label class="control-label col-sm-3" for="question-multichoice-' + index + '-1">&nbsp;</label>'+
					      '<div class="col-sm-7">'+
					      '<input class="form-control" type="text" id="question-multichoice-' + index + '-1" name="question[' + index + '][question-multichoice-1]" size="100" placeholder="' + lang['multichoice-title-placeholder'] +' 2" required/>'+
					    '</div>'+
					  '</div>' +
					  '<div class="form-group" id="multichoice-add-group-' + index + '">'+
					  	'<label class="control-label col-sm-3" for="no-target">&nbsp;</label>'+
					  	'<div class="col-sm-7">'+
					  		'<button id="multichoice-add-button-' + index + '" type="button" class="btn btn-default" onClick="addMultichoice(' + index + ',2);">' + lang['multichoice-add'] +'</button>'+
					  	'</div>'+
					  '</div>')
	}
}

function addMultichoice(index, choice_index){
	$("#multichoice-add-group-" + index).remove();
	$("#question-data-holder-" + index).append('<div class="form-group" id="multichoice-answer-group-' + index + '-' + choice_index + '">'+
					    '<label class="control-label col-sm-3" for="question-multichoice-' + index + '-' + choice_index + '">&nbsp;</label>'+
					      '<div class="col-sm-7">'+
					      '<input class="form-control" type="text" id="question-multichoice-' + index + '-' + choice_index + '" name="question[' + index + '][question-multichoice-' + choice_index + ']" size="100"  placeholder="' + lang['multichoice-title-placeholder'] +' ' + (choice_index + 1) + '" required/>'+
					    '</div>'+
					  '</div>');
	$("#question-data-holder-" + index).append('<div class="form-group" id="multichoice-add-group-' + index + '">'+
					  	'<label class="control-label col-sm-3" for="no-target">&nbsp;</label>'+
					  	'<div class="col-sm-7">'+
					  		'<button id="multichoice-add-button-' + index + '" type="button" class="btn btn-default" onClick="addMultichoice(' + index + ',' + (choice_index + 1) + ');">' + lang['multichoice-add'] +'</button>&nbsp;&nbsp;'+
					  		'<button id="multichoice-remove-button-' + index + '" type="button" class="btn btn-danger" onClick="removeMultichoice(' + index + ',' + choice_index + ');">' + lang['multichoice-remove'] +'</button>' +
					  	'</div>'+
					  '</div>');
}

function removeMultichoice(index, choice_index){
	$("#multichoice-add-group-" + index).remove();
	$("#multichoice-answer-group-" + index + "-" + choice_index).remove();
	var removeButtonString = '<button id="multichoice-remove-button-' + index + '" type="button" class="btn btn-danger" onClick="removeMultichoice(' + index + ',' + (choice_index - 1) + ');">' + lang['multichoice-remove'] +'</button>';
	if (choice_index == 2) {
		removeButtonString = '';
	}
	$("#question-data-holder-" + index).append('<div class="form-group" id="multichoice-add-group-' + index + '">'+
					  	'<label class="control-label col-sm-3" for="no-target">&nbsp;</label>'+
					  	'<div class="col-sm-7">'+
					  		'<button id="multichoice-add-button-' + index + '" type="button" class="btn btn-default" onClick="addMultichoice(' + index + ',' + choice_index + ');">' + lang['multichoice-add'] +'</button>&nbsp;&nbsp;'+
					  		removeButtonString +
					  	'</div>'+
					  '</div>');
}

var eventCount = 0;
function add_event(){
	$("#eventHolder").append('<div class="panel panel-default" id="event-panel-' + eventCount + '">' +
			'<div class="panel-body">' +
		  	'<div class="form-horizontal">' +
		      	'<div class="form-group">' +
			        '<label class="control-label col-sm-3" for="event-title-' + eventCount + '">' + lang['event-name'] +': </label>' +
			        '<div class="col-sm-7">' +
			          '<input class="form-control" type="text" id="event-title-' + eventCount + '" name="event[' + eventCount +'][event-title]" size="100" placeholder="' + lang['event-name'] +'" required/>' +
			        '</div>' +
			    '</div>' +
			    '<div class="form-group">' +
			        '<label class="control-label col-sm-3" for="event-control-time-' + eventCount + '">' + lang['event-control-time'] +': </label>' +
			        '<div class="col-sm-7 form-inline">' +
			          	'<input class="form-control input-sm" type="number" id="event-control-time-' + eventCount + '" name="event[' + eventCount +'][event-control-time]" size="5" min="1" placeholder="0" required/>&nbsp;&nbsp;' +
          				'<select class="form-control input-sm" name="event[' + eventCount + '][event-control-time-unit]">' +
			            '<option value="m">' + lang['minutes'] +'</option>' +
			            '<option value="h" selected>' + lang['days'] +'</option>' +
			            '<option value="d">' + lang['hours'] +'</option>' +
			          '</select>' +
			        '</div>' +
			    '</div>' +
		    '</div>' +
		  '</div>' +
		'</div>');
	$('#event-panel-' + eventCount).hide();
	$('#event-panel-' + eventCount).slideDown(200);
	eventCount++;
	$("#study-event-count").val(eventCount);
	$("#event-remove-button").show();
}

function remove_event(){
	var r = confirm(lang['remove-event-confirm']);
	if (r) {
		if (eventCount > 0){
			$("#event-panel-" + (eventCount-1)).remove();
			eventCount--;
			$("#study-event-count").val(eventCount);
			if (eventCount == 0){
				$("#event-remove-button").hide();
			}
		}
	}
}

/*

	'invalid-date' : '<?php echo $this->lang->line('invalid-date'); ?>',
		'invalid-time' : '<?php echo $this->lang->line('invalid-time'); ?>',
		'begin-must-be-before-end' : '<?php echo $this->lang->line('begin-must-be-before-end'); ?>',
		'begin-must-be-after-today' : '<?php echo $this->lang->line('begin-must-be-after-today'); ?>',
		'duration-must-be-shorter' : '<?php echo $this->lang->line('duration-must-be-shorter'); ?>',
		'beeps-dont-fit' : '<?php echo $this->lang->line('beeps-dont-fit'); ?>',
		'beep-start-time-before-end-time' : '<?php echo $this->lang->line('beep-start-time-before-end-time'); ?>'
*/

$("#study-start-date").on("propertychange keyup paste input", function(){
	if (($("#study-start-date").val().length) == 10){
	    var date = new Date($("#study-start-date").val());
	    if (date instanceof Date){
	    	if (date.toString() != "Invalid Date"){
		    	$("#study-start-date-group").removeClass("has-error");
	    		$("#study-start-date").get(0).setCustomValidity("");
		    	comparedates();
		    }
	    } else {
	    	$("#study-start-date-group").addClass("has-error");
	    	$("#study-start-date").get(0).setCustomValidity(lang['invalid-date']);
	    }
    } else {
    	$("#study-start-date-group").addClass("has-error");
	    $("#study-start-date").get(0).setCustomValidity(lang['invalid-date']);
    }
});

$("#study-end-date").on("propertychange keyup paste input", function(){
	if (($("#study-end-date").val().length) == 10){
	    var date = new Date($("#study-end-date").val());
	    if (date instanceof Date){
	    	if (date.toString() != "Invalid Date"){
		    	$("#study-end-date-group").removeClass("has-error");
		    	$("#study-end-date").get(0).setCustomValidity("");
		    	comparedates();
	    	}
	    } else {
	    	$("#study-end-date-group").addClass("has-error");
	    	$("#study-end-date").get(0).setCustomValidity(lang['invalid-date']);
	    }
    } else {
    	$("#study-end-date-group").addClass("has-error");
	    $("#study-end-date").get(0).setCustomValidity(lang['invalid-date']);
    }
});

function comparedates(){
	if (($("#study-end-date").val().length) == 10 && ($("#study-start-date").val().length) == 10){
	    var endDate = new Date($("#study-end-date").val());
	    var startDate = new Date($("#study-start-date").val());
	    if (endDate instanceof Date && startDate instanceof Date && startDate.toString() != "Invalid Date" && endDate.toString() != "Invalid Date"){
		    if (startDate < endDate){
		    	var currentDate = new Date();
		    	if (currentDate <= startDate){
			    	$("#study-end-date").get(0).setCustomValidity("");
			    	$("#study-start-date-group").removeClass("has-error");
			    	$("#study-end-date-group").removeClass("has-error");
			    	$("#study-date-help").hide();	
			    	$("#study-date-help-too-early").hide();
					checkDuration();
				} else {
					$("#study-end-date").get(0).setCustomValidity(lang['begin-must-be-after-today']);
			    	$("#study-start-date-group").addClass("has-error");
			    	$("#study-end-date-group").addClass("has-error");
			    	$("#study-date-help").hide();
			    	$("#study-date-help-too-early").show();
				}
		    } else {
		    	$("#study-end-date").get(0).setCustomValidity(lang['begin-must-be-before-end']);
		    	$("#study-start-date-group").addClass("has-error");
		    	$("#study-end-date-group").addClass("has-error");
		    	$("#study-date-help").show();
			    $("#study-date-help-too-early").hide();
		    }
	    }
	}
}

$("#study-beep-start-time").on("propertychange keyup paste input", function(){
	if (validTime($("#study-beep-start-time").val())){
		$("#study-beep-start-time").get(0).setCustomValidity("");
		comparetimes();
	} else {
		$("#study-beep-start-time").get(0).setCustomValidity(lang['invalid-time']);
	}
});

$("#study-beep-end-time").on("propertychange keyup paste input", function(){
	if (validTime($("#study-beep-end-time").val())){
		$("#study-beep-end-time").get(0).setCustomValidity("");
		comparetimes();
	} else {
		$("#study-beep-end-time").get(0).setCustomValidity(lang['invalid-time']);
	}
});

$("#study-beeps-per-day").on("propertychange keyup paste input", function(){
	checkBeepPeriod();
});

$("#study-min-time-between-beeps").on("propertychange keyup paste input", function(){
	checkBeepPeriod();
});

function validTime(time){
	return /^([0-1]?[0-9]|2[0-3]):([0-5][0-9])(:[0-5][0-9])?$/.test(time);
}

function comparetimes(){
	if (validTime($("#study-beep-start-time").val()) && $("#study-beep-end-time").val()){
		var startTime = $("#study-beep-start-time").val().split(":");
		var endTime = $("#study-beep-end-time").val().split(":");

		if (Number(startTime[0]) < Number(endTime[0])){
			$("#study-beep-end-time").get(0).setCustomValidity("");
			$("#study-beep-time-group").removeClass("has-error");
			$("#study-time-help-before").hide();
			checkBeepPeriod();
		} else if (Number(startTime[0]) == Number(endTime[0])) {
			if (Number(startTime[1]) < Number(endTime[1])){
				$("#study-beep-end-time").get(0).setCustomValidity("");
				$("#study-beep-time-group").removeClass("has-error");
				$("#study-time-help-before").hide();
			} else {
				$("#study-beep-end-time").get(0).setCustomValidity(lang['beep-start-time-before-end-time']);
				$("#study-beep-time-group").addClass("has-error");
				$("#study-time-help-before").show();
			}
		} else {
			
			$("#study-beep-time-group").addClass("has-error");
			$("#study-time-help-before").show();
		}
	}
}

function checkBeepPeriod(){
	if ($("#study-beeps-per-day").val() != "" && $("#study-min-time-between-beeps").val() != "" && validTime($("#study-beep-start-time").val()) && $("#study-beep-end-time").val()) {
		var startTime = $("#study-beep-start-time").val().split(":");
		var endTime = $("#study-beep-end-time").val().split(":");

		var startTimeInH = Number(startTime[0]) + (Number(startTime[1]) / 60);
		var endTimeInH = Number(endTime[0]) + (Number(endTime[1]) / 60);

		if (Number($("#study-beeps-per-day").val()) * Number($("#study-min-time-between-beeps").val()) <= Number(endTimeInH) - Number(startTimeInH)) {
			$("#study-beeps-per-day").get(0).setCustomValidity("");
			$("#study-beep-amt-group").removeClass("has-error");
			$("#study-beep-amt-help").hide();
		} else {
			$("#study-beeps-per-day").get(0).setCustomValidity(lang['beeps-dont-fit']);
			$("#study-beep-amt-group").addClass("has-error");
			$("#study-beep-amt-help").show();
		}
	}
}


$("[name='gen[study-duration-for-user]']").change(function() {
    if ($("[name='gen[study-duration-for-user]']:checked").val() == "1"){
    	$("#study-duration-time").prop('required', true);
    	checkDuration();
    } else {
    	$("#study-duration-time").prop('required', false);
    	$("#study-duration-time").get(0).setCustomValidity("");
    	$("#study-duration-group").removeClass("has-error");
		$("#study-duration-help-exceeded").hide();
    }
});

$("#study-allow-postpone").change(function() {
    if ($("study-allow-postpone").prop('checked')){
    	$("#study-allow-postpone").prop('required', true);
    } else {
    	$("#study-allow-postpone").prop('required', false);
    }
});


$("#study-duration-time").on("propertychange keyup paste input", function(){
    checkDuration();
});

$("#study-duration-time-unit").change(function() {
    checkDuration();
});

function checkDuration(){
	if ($("#study-duration-time").prop('required')){
    	if (($("#study-end-date").val().length) == 10 && ($("#study-start-date").val().length) == 10){
		    var endDate = new Date($("#study-end-date").val());
		    var startDate = new Date($("#study-start-date").val());
		    if (endDate instanceof Date && startDate instanceof Date && startDate.toString() != "Invalid Date" && endDate.toString() != "Invalid Date"){
		    	var dateDifInMinutes = ((endDate.getTime() - startDate.getTime()) / (60000));
		    	var durationInMinutes = $("#study-duration-time").val() * $("#study-duration-time-unit").val();
		    	if (durationInMinutes <= dateDifInMinutes) {
		    		$("#study-duration-time").get(0).setCustomValidity("");
			    	$("#study-duration-group").removeClass("has-error");
					$("#study-duration-help-exceeded").hide();
		    	} else {
		    		$("#study-duration-time").get(0).setCustomValidity(lang['duration-must-be-shorter']);
			    	$("#study-duration-group").addClass("has-error");
					$("#study-duration-help-exceeded").show();
		    	}
		    }
		}
    } else {
    	$("#study-duration-time").get(0).setCustomValidity("");
    	$("#study-duration-group").removeClass("has-error");
		$("#study-duration-help-exceeded").hide();
    }
}
