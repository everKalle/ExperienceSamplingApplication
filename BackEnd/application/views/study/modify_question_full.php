<hr>
<div class="panel panel-success">
  <div class="panel-heading">
  	<?php echo $this->lang->line('questions'); ?>
  </div>
  <div class="panel-body">
	<input type="hidden" id="study-question-count" name="question[study-question-count]" value="<?php echo count($questions); ?>" required/>
	<div id="questionHolder">
		<?php
	    $i = 0;
	    foreach($questions as $question): ?>
	    <div class="panel panel-default" id="question-panel-<?php echo $i; ?>">
		  <div class="panel-body">
		  	<div class="form-horizontal">
		      <div class="form-group">
		        <label class="control-label col-sm-3" for="question-type-<?php echo $i; ?>"><?php echo $this->lang->line('question-type'); ?>: </label>
		        <div class="col-sm-7">
				    <select class="form-control input-sm" name="question[<?php echo $i; ?>][question-type]" id="question-type-<?php echo $i; ?>" onChange="modifyType(<?php echo $i; ?>);">
				      <option value="freetext" <?php if ($question['question-type'] == "freetext") echo 'selected'; ?>><?php echo $this->lang->line('question-type-freetext'); ?></option>
				      <option value="multichoice"<?php if ($question['question-type'] == "multichoice") echo 'selected'; ?>><?php echo $this->lang->line('question-type-multichoice'); ?></option>
				    </select>
			    </div>
		      </div>
		      	<div class="form-group">
			        <label class="control-label col-sm-3" for="question-title-<?php echo $i; ?>"><?php echo $this->lang->line('freetext-question'); ?>: </label>
			        <div class="col-sm-7">
			          <input class="form-control" type="text" id="question-title-<?php echo $i; ?>" name="question[<?php echo $i; ?>][question-title]" size="100" placeholder="<?php echo $this->lang->line('freetext-question'); ?>" value="<?php echo $question['id']; ?>" required/>
			        </div>
			      </div>
			  <div id="question-data-holder-<?php echo $i; ?>">
				  <?php if ($question['question-type'] == "multichoice") { ?>
				  	<hr>
						<div class="form-group">
					    <label class="control-label col-sm-3" for="question-multichoice-single-choice-<?php echo $i; ?>"><?php echo $this->lang->line('multichoice-can-choose'); ?>: </label>
					      <div class="col-sm-7">
					      	<input type="radio" name="question[<?php echo $i; ?>][question-multichoice-single-choice]" value="1" <?php if ($question['question-multichoice-single-choice']) echo 'checked'; ?>> <?php echo $this->lang->line('multichoice-one-choice'); ?><br>
          					<input type="radio" name="question[<?php echo $i; ?>][question-multichoice-single-choice]" value="0" <?php if (!$question['question-multichoice-single-choice']) echo 'checked'; ?>> <?php echo $this->lang->line('multichoice-multi-choice'); ?>
					    </div>
					  </div>
					  <hr>
					  <?php
					  $j = 0;
					  foreach (json_decode($question['question-multichoices']) as $choice): ?>
			  		  <div class="form-group" <?php if ($j >1) echo ' id="multichoice-answer-group-' . $i . '-' . $j .'"'; ?>>
					    <label class="control-label col-sm-3" for="question-multichoice-<?php echo $i; ?>-<?php echo $j; ?>"><?php if ($j==0) echo "vastusevariandid:"; ?></label>
					      <div class="col-sm-7">
					      <input class="form-control" type="text" id="question-multichoice-<?php echo $i; ?>-<?php echo $j; ?>" name="question[<?php echo $i; ?>][question-multichoice-<?php echo $j; ?>]" size="100" placeholder="<?php echo $this->lang->line('multichoice-title-placeholder'); ?> <?php echo ($j + 1); ?>" value="<?php echo $choice;?>" required/>
					    </div>
					  </div>
					  <?php
					  $j++;
					  endforeach;
					  ?>
					  <div class="form-group" id="multichoice-add-group-<?php echo $i; ?>">
					  	<label class="control-label col-sm-3" for="no-target">&nbsp;</label>
					  	<div class="col-sm-7">
					  		<button id="multichoice-add-button-<?php echo $i; ?>" type="button" class="btn btn-default" onClick="addMultichoice(<?php echo $i; ?>,<?php echo $j; ?>);"><?php echo $this->lang->line('multichoice-add'); ?></button>
					  		<?php if($j>1) { ?>
					  		<button id="multichoice-remove-button-<?php echo $i; ?>" type="button" class="btn btn-danger" onClick="removeMultichoice(<?php echo $i; ?>,<?php echo ($j-1); ?>);"><?php echo $this->lang->line('multichoice-remove'); ?></button>
					  		<?php } ?>
					  	</div>
					  </div>
				  <?php } ?>
		      </div>
		    </div>
		  </div>
		</div>
		<?php
		$i++;
		endforeach; ?>
	</div>
	<button type="button" class="btn btn-lg btn-default" onClick="add_question();"><?php echo $this->lang->line('add-question'); ?></button>
	<button id="question-remove-button" type="button" class="btn btn-lg btn-danger" onClick="remove_question();"><?php echo $this->lang->line('remove-question'); ?></button>
  </div>
  <script>
  	var questionCount = <?php echo $i; ?>;
  </script>
</div>
