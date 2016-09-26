<hr>
<div class="panel panel-success">
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