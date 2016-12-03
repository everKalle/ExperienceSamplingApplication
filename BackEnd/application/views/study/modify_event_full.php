<hr>
<div class="panel panel-success">
  <div class="panel-heading">
  	<?php echo $this->lang->line('events'); ?>
  </div>
  <div class="panel-body">
	<input type="hidden" id="study-event-count" name="event[study-event-count]" value="<?php echo count($events); ?>" required/>
	<div id="eventHolder">
    <?php
    $i = 0;
    foreach($events as $event): ?>
      <div class="panel panel-default" id="event-panel-<?php echo $i; ?>">
      <div class="panel-body">
        <div class="form-horizontal">
            <div class="form-group">
              <label class="control-label col-sm-3" for="event-title-<?php echo $i; ?>"><?php echo $this->lang->line('event-name'); ?>: </label>
              <div class="col-sm-7">
                <input class="form-control" type="text" id="event-title-<?php echo $i; ?>" name="event[<?php echo $i; ?>][event-title]" size="100" placeholder="<?php echo $this->lang->line('event-name'); ?>" value="<?php echo $event['event-title']; ?>" required/>
              </div>
          </div>
          <div class="form-group">
              <label class="control-label col-sm-3" for="event-control-time-<?php echo $i; ?>"><?php echo $this->lang->line('event-control-time'); ?>: </label>
              <div class="col-sm-7 form-inline">
                  <input class="form-control input-sm" type="number" id="event-control-time-<?php echo $i; ?>" name="event[<?php echo $i; ?>][event-control-time]" size="5" min="1" placeholder="0" value="<?php echo $event['event-control-time']; ?>" required/>&nbsp;&nbsp;
                  <select class="form-control input-sm" name="event[<?php echo $i; ?>][event-control-time-unit]">
                  <option value="m" <?php if ($event['event-control-time-unit']=="m") echo "selected"; ?>><?php echo $this->lang->line('minutes'); ?></option>
                  <option value="h" <?php if ($event['event-control-time-unit']=="h") echo "selected"; ?>><?php echo $this->lang->line('hours'); ?></option>
                  <option value="d" <?php if ($event['event-control-time-unit']=="d") echo "selected"; ?>><?php echo $this->lang->line('days'); ?></option>
                </select>
              </div>
          </div>
        </div>
      </div>
    </div>
    
  <?php
  $i++;
  endforeach; ?>
	</div>
  <button type="button" class="btn btn-lg btn-default" onClick="add_event();"><?php echo $this->lang->line('add-event'); ?></button>
  <button <?php if($i==0) echo 'style="display: none;"'; ?> id="event-remove-button" type="button" class="btn btn-lg btn-danger" onClick="remove_event();"><?php echo $this->lang->line('remove-event'); ?></button>
  </div>
  <script>
    var eventCount = <?php echo $i; ?>;
  </script>
</div>

<input class="btn btn-lg btn-primary" type="submit" name="submit" id="submit-button" value="<?php echo $this->lang->line('save-modifications'); ?>"/>

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