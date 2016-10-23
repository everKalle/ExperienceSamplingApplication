<hr>
<div class="panel panel-success">
  <div class="panel-heading">
  	Sündmused
  </div>
  <div class="panel-body">
	<input type="hidden" id="study-event-count" name="event[study-event-count]" value="<?php echo count($events); ?>" required/>
	<div id="eventHolder">
    <?php
    $i = 0;
    foreach($events as $event): ?>
    <div class="panel panel-default" id="event-panel-<?php echo $i;?>">
      <div class="panel-body">
        <div class="form-horizontal">
            <div class="form-group">
              <label class="control-label col-sm-3" for="event-title-<?php echo $i;?>">Sündmuse nimi: </label>
              <div class="col-sm-7">
                <input type="hidden" id="event-id-<?php echo $i;?>" name="event[<?php echo $i;?>][event-id]" value="<?php echo $event['id']; ?>">
                <input class="form-control" type="text" id="event-title-<?php echo $i;?>" name="event[<?php echo $i;?>][event-title]" size="100" placeholder="Sündmuse nimi" value="<?php echo $event['event-title']; ?>" required/>
              </div>
          </div>
          <div class="form-group">
              <label class="control-label col-sm-3" for="event-control-time-<?php echo $i;?>">Kontrollaeg: </label>
              <div class="col-sm-7 form-inline">
                  <input class="form-control input-sm" type="number" id="event-control-time-<?php echo $i;?>" name="event[<?php echo $i;?>][event-control-time]" size="5" min="1" placeholder="0" value="<?php echo $event['event-control-time']; ?>" required/>&nbsp;&nbsp;
                  <select class="form-control input-sm" name="event[<?php echo $i;?>][event-control-time-unit]">
                  <option value="m" <?php if ($event['event-control-time-unit']=="m") echo "selected"; ?>>minutit</option>
                  <option value="h" <?php if ($event['event-control-time-unit']=="h") echo "selected"; ?>>tundi</option>
                  <option value="d" <?php if ($event['event-control-time-unit']=="d") echo "selected"; ?>>päeva</option>
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
  </div>
</div>

<input class="btn btn-lg btn-primary" type="submit" name="submit" id="submit-button" value="Salvesta muudatused"/>

</form>
