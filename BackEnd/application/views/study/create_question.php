<hr>
<div class="panel panel-info">
  <div class="panel-heading">
  	Küsimused
  </div>
  <div class="panel-body">
	<input type="hidden" id="study-question-count" name="study-question-count" value="0" required/>
	<div id="questionHolder">
	</div>
	<button type="button" class="btn btn-lg btn-default" onClick="add_question();">Lisa küsimus</button>
	<button style="display: none;" id="question-remove-button" type="button" class="btn btn-lg btn-danger" onClick="remove_question();">Eemalda viimane küsimus</button>
  </div>
</div>

<input class="btn btn-lg btn-primary" type="submit" name="submit" value="Salvesta uuring"/>

</form>



<script>
var questionCount = 0;
function add_question(){
	$("#questionHolder").append('<div class="panel panel-default" id="question-panel-' + questionCount + '">' + 
		  '<div class="panel-body">' +
		  	'<div class="form-horizontal">' +
		      '<div class="form-group">' +
		        '<label class="control-label col-sm-3" for="question-type-' + questionCount + '">Küsimuse tüüp: </label>' +
		        '<div class="col-sm-7">' + 
				    '<select class="form-control input-sm" name="question-type-' + questionCount + '" id="question-type-' + questionCount + '" onChange="modifyType(' + questionCount + ');">' +
				      '<option value="freetext" selected>Vabatekst</option>' + 
				      '<option value="multichoice">Valikvastustega</option>' + 
				    '</select>' + 
			    '</div>' + 
		      '</div>' + 
		      	'<div class="form-group">' + 
			        '<label class="control-label col-sm-3" for="question-title-' + questionCount + '">Küsimus: </label>' + 
			        '<div class="col-sm-7">' + 
			          '<input class="form-control" type="text" id="question-title-' + questionCount + '" name="question-title-' + questionCount + '" size="100" required/>' + 
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
	$("#question-remove-button").show();
}

function remove_question(){
	var r = confirm("Kustuta viimane küsimus?");
	if (r) {
		if (questionCount > 0){
			$("#question-panel-" + (questionCount-1)).remove();
			questionCount--;
			if (questionCount == 0){
				$("#question-remove-button").hide();
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
					    '<label class="control-label col-sm-3" for="question-multichoice-' + index + '-0">Vastusevariandid: </label>'+
					      '<div class="col-sm-7">'+
					      '<input class="form-control" type="text" id="question-multichoice-' + index + '-0" name="question-multichoice-' + index + '-0" size="100" required/>'+
					    '</div>'+
					  '</div>'+
					  '<div class="form-group">'+
					    '<label class="control-label col-sm-3" for="question-multichoice-' + index + '-1">&nbsp;</label>'+
					      '<div class="col-sm-7">'+
					      '<input class="form-control" type="text" id="question-multichoice-' + index + '-1" name="question-multichoice-' + index + '-1" size="100" required/>'+
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
					      '<input class="form-control" type="text" id="question-multichoice-' + index + '-' + choice_index + '" name="question-multichoice-' + index + '-' + choice_index + '" size="100" required/>'+
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
</script>