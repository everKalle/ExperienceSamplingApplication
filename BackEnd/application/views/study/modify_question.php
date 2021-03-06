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
	    <div class="panel panel-default" id="question-panel-' + questionCount + '">
		<div class="panel-body">
		  	<div class="form-horizontal">
		      <div class="form-group">
		      	<input type="hidden" id="question-id-<?php echo $i;?>" name="question[<?php echo $i;?>][question-id]" value="<?php echo $question['id']; ?>">
		        <strong class="col-sm-3 text-right"><?php echo $this->lang->line('question-type'); ?>: </strong>
		        <p class="col-sm-7">
		        	<?php
		        		if ($question['question-type'] == "freetext") {
		        			echo $this->lang->line('question-type-freetext');
		        		} else if ($question['question-type'] == "multichoice") {
		        			echo $this->lang->line('question-type-multichoice');
		        		} else {
		        			echo "Vigane";
		        		}
		        	?>
		      		<input type="hidden" id="question-type-<?php echo $i;?>" name="question[<?php echo $i;?>][question-type]" value="<?php echo $question['question-type']; ?>">
			    </p>
		      </div>
		      	<div class="form-group">
			        <label class="control-label col-sm-3" for="question-title-<?php echo $i;?>"><?php echo $this->lang->line('freetext-question'); ?>: </label>
			        <div class="col-sm-7">
			          <input class="form-control" type="text" id="question-title-<?php echo $i;?>" name="question[<?php echo $i;?>][question-title]" size="100" placeholder="Küsimus" value="<?php echo $question['question-title']; ?>" required/>
			        </div>
			      </div>
			  <div id="question-data-holder-<?php echo $i;?>">
			  	<?php if ($question['question-type'] == "multichoice") { ?>
			  		<hr>
			  		<div class="form-group">
					    <strong class="col-sm-3 text-right"><?php echo $this->lang->line('multichoice-can-choose'); ?>: </strong>
					    <p class="col-sm-7">
				        	<?php
				        		if ($question['question-multichoice-single-choice']) {
				        			echo $this->lang->line('multichoice-one-choice');
				        		} else {
				        			echo $this->lang->line('multichoice-multi-choice');
				        		}
				        	?>
					    </p>
					  </div>
						<hr>
					  <?php
					  $j = 0;
					  foreach (json_decode($question['question-multichoices']) as $choice): ?>
					  <div class="form-group">
					    <label class="control-label col-sm-3" for="question-multichoice-<?php echo $i;?>-<?php echo $j;?>"><?php if ($j==0) echo "vastusevariandid:"; ?></label>
					      	<div class="col-sm-7">
							      <input class="form-control" type="text" id="question-multichoice-<?php echo $i;?>-<?php echo $j;?>" name="question[<?php echo $i;?>][question-multichoice-<?php echo $j;?>]" size="100" placeholder="<?php echo $this->lang->line('multichoice-title-placeholder'); ?> <?php echo ($j + 1);?>" value="<?php echo $choice;?>" required/>
				  			</div>
				  		</div>
				  	<?php
					$j++;
					endforeach;
					} ?>
		      </div>
		    </div>
		  </div>
		  </div>
		<?php
		$i++;
		endforeach; ?>
	</div>
  </div>
</div>
