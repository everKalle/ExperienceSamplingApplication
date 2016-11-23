<hr>
<div class="panel panel-success">
  <div class="panel-heading">
  	<?php echo $this->lang->line('events'); ?>
  </div>
  <div class="panel-body">
	<input type="hidden" id="study-event-count" name="event[study-event-count]" value="0" required/>
	<div id="eventHolder">
	</div>
	<button type="button" class="btn btn-lg btn-default" onClick="add_event();"><?php echo $this->lang->line('add-event'); ?></button>
	<button style="display: none;" id="event-remove-button" type="button" class="btn btn-lg btn-danger" onClick="remove_event();"><?php echo $this->lang->line('remove-event'); ?></button>
  </div>
</div>

<div class="alert alert-warning" role="alert" id="alert-question-add">
    <?php echo $this->lang->line('add-at-least-one-question'); ?>
</div>

<input class="btn btn-lg btn-primary" type="submit" name="submit" id="submit-button" disabled="disabled" value="<?php echo $this->lang->line('save-study'); ?>"/>

</form>

<script>
	var lang = {
		'remove-question-confirm' : '<?php echo $this->lang->line('remove-question-confirm'); ?>',
		'question-type' : '<?php echo $this->lang->line('question-type'); ?>',
		'question-type-freetext' : '<?php echo $this->lang->line('question-type-freetext'); ?>',
		'question-type-multichoice' : '<?php echo $this->lang->line('question-type-multichoice'); ?>',
		'multichoice-can-choose' : '<?php echo $this->lang->line('multichoice-can-choose'); ?>',
		'multichoice-one-choice' : '<?php echo $this->lang->line('multichoice-one-choice'); ?>',
		'multichoice-multi-choice' : '<?php echo $this->lang->line('multichoice-multi-choice'); ?>',
		'multichoice-title' : '<?php echo $this->lang->line('multichoice-title'); ?>',
		'multichoice-title-placeholder' : '<?php echo $this->lang->line('multichoice-title-placeholder'); ?>',
		'freetext-question' : '<?php echo $this->lang->line('freetext-question'); ?>',
		'multichoice-add' : '<?php echo $this->lang->line('multichoice-add'); ?>',
		'multichoice-remove' : '<?php echo $this->lang->line('multichoice-remove'); ?>',
		'remove-event-confirm' : '<?php echo $this->lang->line('remove-event-confirm'); ?>',
		'event-name' : '<?php echo $this->lang->line('event-name'); ?>',
		'event-control-time' : '<?php echo $this->lang->line('event-control-time'); ?>',
		'minutes' : '<?php echo $this->lang->line('minutes'); ?>',
		'hours' : '<?php echo $this->lang->line('hours'); ?>',
		'days' : '<?php echo $this->lang->line('days'); ?>',
		'invalid-date' : '<?php echo $this->lang->line('invalid-date'); ?>',
		'invalid-time' : '<?php echo $this->lang->line('invalid-time'); ?>',
		'begin-must-be-before-end' : "<?php echo $this->lang->line('begin-must-be-before-end'); ?>",
		'begin-must-be-after-today' : "<?php echo $this->lang->line('begin-must-be-after-today'); ?>",
		'duration-must-be-shorter' : "<?php echo $this->lang->line('duration-must-be-shorter'); ?>",
		'beeps-dont-fit' : "<?php echo $this->lang->line('beeps-dont-fit'); ?>",
		'beep-start-time-before-end-time' : "<?php echo $this->lang->line('beep-start-time-before-end-time'); ?>"
	}
</script>