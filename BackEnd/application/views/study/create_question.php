<hr>
<div class="panel panel-success">
  <div class="panel-heading">
  	<?php echo $this->lang->line('questions'); ?>
  </div>
  <div class="panel-body">
	<input type="hidden" id="study-question-count" name="question[study-question-count]" value="0" required/>
	<div id="questionHolder">
	</div>
	<button type="button" class="btn btn-lg btn-default" onClick="add_question();"><?php echo $this->lang->line('add-question'); ?></button>
	<button style="display: none;" id="question-remove-button" type="button" class="btn btn-lg btn-danger" onClick="remove_question();"><?php echo $this->lang->line('remove-question'); ?></button>
  </div>
</div>