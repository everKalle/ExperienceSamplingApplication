var questionCount = 0;

function add_question(){
	$("#questionHolder").append('<div class="panel panel-default" id="question-panel-' + questionCount + '">' + 
		  '<div class="panel-body">' +
		  	'<div class="form-horizontal">' +
		      '<div class="form-group">' +
		        '<label class="control-label col-sm-3" for="question-type-' + questionCount + '">Küsimuse tüüp: </label>' +
		        '<div class="col-sm-7">' + 
				    '<select class="form-control input-sm" name="question[' + questionCount + '][question-type]" id="question-type-' + questionCount + '" onChange="modifyType(' + questionCount + ');">' +
				      '<option value="freetext" selected>Vabatekst</option>' + 
				      '<option value="multichoice">Valikvastustega</option>' + 
				    '</select>' + 
			    '</div>' + 
		      '</div>' + 
		      	'<div class="form-group">' + 
			        '<label class="control-label col-sm-3" for="question-title-' + questionCount + '">Küsimus: </label>' + 
			        '<div class="col-sm-7">' + 
			          '<input class="form-control" type="text" id="question-title-' + questionCount + '" name="question[' + questionCount + '][question-title]" size="100" placeholder="Küsimus" required/>' + 
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
	var r = confirm("Kustuta viimane küsimus?");
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
					    '<label class="control-label col-sm-3" for="question-multichoice-single-choice-' + index + '">Valida saab: </label>'+
					      '<div class="col-sm-7">'+
					      	'<input type="radio" name="question[' + index + '][question-multichoice-single-choice]" value="1" checked> Ühe vastusevariandi<br>' +
          					'<input type="radio" name="question[' + index + '][question-multichoice-single-choice]" value="0"> Mitu vastusevarianti' +
					    '</div>'+
					  '</div>' +
						'<hr>'+
			  		  '<div class="form-group">'+
					    '<label class="control-label col-sm-3" for="question-multichoice-' + index + '-0">Vastusevariandid: </label>'+
					      '<div class="col-sm-7">'+
					      '<input class="form-control" type="text" id="question-multichoice-' + index + '-0" name="question[' + index + '][question-multichoice-0]" size="100" placeholder="Vastusevariant 1" required/>'+
					    '</div>'+
					  '</div>'+
					  '<div class="form-group">'+
					    '<label class="control-label col-sm-3" for="question-multichoice-' + index + '-1">&nbsp;</label>'+
					      '<div class="col-sm-7">'+
					      '<input class="form-control" type="text" id="question-multichoice-' + index + '-1" name="question[' + index + '][question-multichoice-1]" size="100" placeholder="Vastusevariant 2" required/>'+
					    '</div>'+
					  '</div>' +
					  '<div class="form-group" id="multichoice-add-group-' + index + '">'+
					  	'<label class="control-label col-sm-3" for="no-target">&nbsp;</label>'+
					  	'<div class="col-sm-7">'+
					  		'<button id="multichoice-add-button-' + index + '" type="button" class="btn btn-default" onClick="addMultichoice(' + index + ',2);">Lisa vastusevariant</button>'+
					  	'</div>'+
					  '</div>')
	}
}

function addMultichoice(index, choice_index){
	$("#multichoice-add-group-" + index).remove();
	$("#question-data-holder-" + index).append('<div class="form-group" id="multichoice-answer-group-' + index + '-' + choice_index + '">'+
					    '<label class="control-label col-sm-3" for="question-multichoice-' + index + '-' + choice_index + '">&nbsp;</label>'+
					      '<div class="col-sm-7">'+
					      '<input class="form-control" type="text" id="question-multichoice-' + index + '-' + choice_index + '" name="question[' + index + '][question-multichoice-' + choice_index + ']" size="100"  placeholder="Vastusevariant ' + (choice_index + 1) + '" required/>'+
					    '</div>'+
					  '</div>');
	$("#question-data-holder-" + index).append('<div class="form-group" id="multichoice-add-group-' + index + '">'+
					  	'<label class="control-label col-sm-3" for="no-target">&nbsp;</label>'+
					  	'<div class="col-sm-7">'+
					  		'<button id="multichoice-add-button-' + index + '" type="button" class="btn btn-default" onClick="addMultichoice(' + index + ',' + (choice_index + 1) + ');">Lisa vastusevariant</button>&nbsp;&nbsp;'+
					  		'<button id="multichoice-remove-button-' + index + '" type="button" class="btn btn-danger" onClick="removeMultichoice(' + index + ',' + choice_index + ');">Eemalda viimane vastusevariant</button>' +
					  	'</div>'+
					  '</div>');
}

function removeMultichoice(index, choice_index){
	$("#multichoice-add-group-" + index).remove();
	$("#multichoice-answer-group-" + index + "-" + choice_index).remove();
	var removeButtonString = '<button id="multichoice-remove-button-' + index + '" type="button" class="btn btn-danger" onClick="removeMultichoice(' + index + ',' + (choice_index - 1) + ');">Eemalda viimane vastusevariant</button>';
	if (choice_index == 2) {
		removeButtonString = '';
	}
	$("#question-data-holder-" + index).append('<div class="form-group" id="multichoice-add-group-' + index + '">'+
					  	'<label class="control-label col-sm-3" for="no-target">&nbsp;</label>'+
					  	'<div class="col-sm-7">'+
					  		'<button id="multichoice-add-button-' + index + '" type="button" class="btn btn-default" onClick="addMultichoice(' + index + ',' + choice_index + ');">Lisa vastusevariant</button>&nbsp;&nbsp;'+
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
			        '<label class="control-label col-sm-3" for="event-title-' + eventCount + '">Sündmuse nimi: </label>' +
			        '<div class="col-sm-7">' +
			          '<input class="form-control" type="text" id="event-title-' + eventCount + '" name="event[' + eventCount +'][event-title]" size="100" placeholder="Sündmuse nimi" required/>' +
			        '</div>' +
			    '</div>' +
			    '<div class="form-group">' +
			        '<label class="control-label col-sm-3" for="event-control-time-' + eventCount + '">Kontrollaeg: </label>' +
			        '<div class="col-sm-7 form-inline">' +
			          	'<input class="form-control input-sm" type="number" id="event-control-time-' + eventCount + '" name="event[' + eventCount +'][event-control-time]" size="5" min="1" placeholder="0" required/>&nbsp;&nbsp;' +
          				'<select class="form-control input-sm" name="event[' + eventCount + '][event-control-time-unit]">' +
			            '<option value="m">minutit</option>' +
			            '<option value="h" selected>tundi</option>' +
			            '<option value="d">päeva</option>' +
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
	var r = confirm("Kustuta viimane sündmus?");
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
	    	$("#study-start-date").get(0).setCustomValidity("Vigane kuupäev");
	    }
    } else {
    	$("#study-start-date-group").addClass("has-error");
	    $("#study-start-date").get(0).setCustomValidity("Vigane kuupäev");
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
	    	$("#study-end-date").get(0).setCustomValidity("Vigane kuupäev");
	    }
    } else {
    	$("#study-end-date-group").addClass("has-error");
	    $("#study-end-date").get(0).setCustomValidity("Vigane kuupäev");
    }
});

function comparedates(){
	if (($("#study-end-date").val().length) == 10 && ($("#study-start-date").val().length) == 10){
	    var endDate = new Date($("#study-end-date").val());
	    var startDate = new Date($("#study-start-date").val());
	    if (endDate instanceof Date && startDate instanceof Date && startDate.toString() != "Invalid Date" && endDate.toString() != "Invalid Date"){
		    if (startDate < endDate){
		    	$("#study-end-date").get(0).setCustomValidity("");
		    	$("#study-start-date-group").removeClass("has-error");
		    	$("#study-end-date-group").removeClass("has-error");
		    	$("#study-date-help").hide();	
		    } else {
		    	$("#study-end-date").get(0).setCustomValidity("Alguskuupäev peab eelnema lõppkuupäevale.");
		    	$("#study-start-date-group").addClass("has-error");
		    	$("#study-end-date-group").addClass("has-error");
		    	$("#study-date-help").show();
		    }
	    }
	}
}

$("#study-beep-start-time").on("propertychange keyup paste input", function(){
	if (validTime($("#study-beep-start-time").val())){
		$("#study-beep-start-time").get(0).setCustomValidity("");
		comparetimes();
	} else {
		$("#study-beep-start-time").get(0).setCustomValidity("Vigane kellaaeg.");
	}
});

$("#study-beep-end-time").on("propertychange keyup paste input", function(){
	if (validTime($("#study-beep-end-time").val())){
		$("#study-beep-end-time").get(0).setCustomValidity("");
		comparetimes();
	} else {
		$("#study-beep-end-time").get(0).setCustomValidity("Vigane kellaaeg.");
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
				$("#study-beep-end-time").get(0).setCustomValidity("Piiksude algusaeg peab eelnema lõppajale.");
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
			$("#study-beeps-per-day").get(0).setCustomValidity("Piiksude arv ei mahu piiksude perioodi sisse ära.");
			$("#study-beep-amt-group").addClass("has-error");
			$("#study-beep-amt-help").show();
		}
	}
}

$("[name=gen[study-duration-for-user]]").change(function() {
    if ($("[name=gen[study-duration-for-user]]:checked").val() == "1"){
    	$("#study-duration-time").prop('required', true);
    } else {
    	$("#study-duration-time").prop('required', false);
    }
});

$("#study-allow-postpone").change(function() {
    if ($("study-allow-postpone").prop('checked')){
    	$("#study-allow-postpone").prop('required', true);
    } else {
    	$("#study-allow-postpone").prop('required', false);
    }
});
