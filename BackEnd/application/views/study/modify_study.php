<br>
<?php echo validation_errors(); ?>

<?php echo form_open('study/modify/' . $study_details['id'])?>
<div class="panel panel-info">
  <div class="panel-heading">
    <h3 class="panel-title"><?php echo $this->lang->line('study-settings'); ?></h3>
  </div>
  <div class="panel-body">
    <div class="form-horizontal">
      <div class="form-group">
        <label class="control-label col-sm-3" for="study-title"><?php echo $this->lang->line('study-title'); ?>:</label>
        <div class="col-sm-7">
          <input type="hidden" id="study-id" name="study-id" value="<?php echo $study_details['id']; ?>">
          <input class="form-control" type="text" id="study-title" name="gen[study-title]" size="100" placeholder="<?php echo $this->lang->line('study-title'); ?>" value="<?php echo $study_details['study-title']; ?>" required/>
        </div>
      </div>
      <div class="form-group" id="study-start-date-group">
        <label class="control-label col-sm-3" for="study-start-date"><?php echo $this->lang->line('begin-date'); ?></label>
        <div class="col-sm-7">
          <input class="form-control" type="text" id="study-start-date" name="gen[study-start-date]" size="30" placeholder="yyyy-mm-dd" value="<?php echo explode(" ", $study_details['study-start-date'])[0]; ?>" required/>
        </div>
      </div>
      <div class="form-group" id="study-end-date-group">
        <label class="control-label col-sm-3" for="study-end-date"><?php echo $this->lang->line('end-date'); ?></label>
        <div class="col-sm-7">
          <input class="form-control" type="text" id="study-end-date" name="gen[study-end-date]" size="30" placeholder="yyyy-mm-dd" value="<?php echo explode(" ", $study_details['study-end-date'])[0]; ?>" required/><br>
          <span style="display: none;" id="study-date-help" class="help-block"><?php echo $this->lang->line('begin-must-be-before-end'); ?></span>
          <span style="display: none;" id="study-date-help-too-early" class="help-block"><?php echo $this->lang->line('begin-must-be-after-today'); ?></span>
        </div>
      </div>
      <div class="form-group">
        <label class="control-label col-sm-3" for="study-duration-for-user"><?php echo $this->lang->line('study-duration'); ?></label>
        <div class="col-sm-7">
          <input type="radio" name="gen[study-duration-for-user]" value="0" <?php if (!$study_details['study-duration-for-user']) echo "checked"; ?>> <?php echo $this->lang->line('as-long-as-they-wish'); ?><br>
          <input type="radio" name="gen[study-duration-for-user]" value="1" <?php if ($study_details['study-duration-for-user']) echo "checked"; ?>> <?php echo $this->lang->line('limited-duration'); ?>:
        </div>
      </div>
      <div class="form-group" id="study-duration-group">
        <label class="control-label col-sm-3" for="no-target">&nbsp;</label>
        <div class="col-sm-7 form-inline">
          <input class="form-control input-sm" type="number" id="study-duration-time" name="gen[study-duration-time]" size="5" placeholder="0" min="1" value="<?php echo ($study_details['study-duration-time'] / 1440); ?>" <?php if ($study_details['study-duration-for-user']) echo "required"; ?>/>
          <select class="form-control input-sm" name="study-duration-time-unit" id="study-duration-time-unit">
            <option value="1440" selected><?php echo $this->lang->line('days'); ?></option>
            <option value="10080"><?php echo $this->lang->line('weeks'); ?></option>
          </select>
          <span style="display: none;" id="study-duration-help-exceeded" class="help-block"><?php echo $this->lang->line('duration-must-be-shorter'); ?></span>
        </div>
      </div><hr>
      <div class="form-group" id="study-beep-amt-group">
        <label class="control-label col-sm-3" for="study-beeps-per-day"><?php echo $this->lang->line('create-beeps-per-day'); ?>:</label>
        <div class="col-sm-7 form-inline">
          <input class="form-control input-sm" type="number" id="study-beeps-per-day" name="gen[study-beeps-per-day]" size="5" min="1" placeholder="0" value="<?php echo $study_details['study-beeps-per-day']; ?>" required/> tk
          <span style="display: none;" id="study-beep-amt-help" class="help-block"><?php echo $this->lang->line('beeps-dont-fit'); ?></span>
        </div>
      </div>
      <div class="form-group">
        <label class="control-label col-sm-3" for="study-min-time-between-beeps"><?php echo $this->lang->line('min-time-between-beeps'); ?></label>
        <div class="col-sm-7 form-inline">
          <input class="form-control input-sm" type="number" id="study-min-time-between-beeps" name="gen[study-min-time-between-beeps]" size="5" min="1" placeholder="0" value="<?php echo ($study_details['study-min-time-between-beeps'] / 60); ?>" required/> <?php echo $this->lang->line('hours'); ?>
        </div>
      </div>
      <div class="form-group" id="study-beep-time-group">
        <label class="control-label col-sm-3" for="study-beep-start-time"><?php echo $this->lang->line('beeps-period'); ?></label>
        <div class="col-sm-7 form-inline">
          <input class="form-control input-sm" type="text" id="study-beep-start-time" name="gen[study-beep-start-time]" size="5" placeholder="00:00" value="<?php echo explode(":", $study_details['study-beep-start-time'])[0] . ':' . explode(":", $study_details['study-beep-start-time'])[1]; ?>" required/> kuni
          <input class="form-control input-sm" type="text" id="study-beep-end-time" name="gen[study-beep-end-time]" size="5" placeholder="00:00" value="<?php echo explode(":", $study_details['study-beep-end-time'])[0] . ':' . explode(":", $study_details['study-beep-end-time'])[1]; ?>" required/>
          <span style="display: none;" id="study-time-help-before" class="help-block"><?php echo $this->lang->line('beep-start-time-before-end-time'); ?></span>
        </div>
      </div><hr>
      <div class="form-group">
        <label class="control-label col-sm-3" for="no-target"><?php echo $this->lang->line('postponing'); ?></label>
        <div class="col-sm-7">
          <input type="checkbox" id="study-allow-postpone" name="gen[study-allow-postpone]" value="1" <?php if ($study_details['study-allow-postpone']) echo "checked"; ?>></b>
        </div>
      </div>
      <div class="form-group">
        <label class="control-label col-sm-3" for="study-postpone-time"><?php echo $this->lang->line('postponing-time'); ?></label>
        <div class="col-sm-7 form-inline">
          <input class="form-control input-sm" type="number" name="gen[study-postpone-time]" size="5" min="1" placeholder="0" value="15" value="<?php echo $study_details['study-postpone-time']; ?>" <?php if ($study_details['study-allow-postpone']) echo "required"; ?> /> <?php echo $this->lang->line('minutes'); ?>
        </div>
      </div><hr>
      <div class="form-group">
        <label class="control-label col-sm-3" for="study-language"><?php echo $this->lang->line('study-language'); ?>: </label>
        <div class="col-sm-7 form-inline">
          <select class="form-control input-sm" name="gen[study-language]">
            <option value="est" <?php if ($study_details['study-language'] == "est") echo "selected"; ?>><?php echo $this->lang->line('study-lang-est'); ?></option>
            <option value="eng" <?php if ($study_details['study-language'] == "eng") echo "selected"; ?>><?php echo $this->lang->line('study-lang-eng'); ?></option>
            <option value="rus" <?php if ($study_details['study-language'] == "rus") echo "selected"; ?>><?php echo $this->lang->line('study-lang-rus'); ?></option>
            <option value="ger" <?php if ($study_details['study-language'] == "ger") echo "selected"; ?>><?php echo $this->lang->line('study-lang-ger'); ?></option>
          </select>
        </div>
      </div>

      <div class="form-group">
        <label class="control-label col-sm-3" for="study-is-public"><?php echo $this->lang->line('study-participation-access'); ?></label>
        <div class="col-sm-7 form-inline">
          <input type="radio" name="gen[study-is-public]" value="1" <?php if ($study_details['study-is-public']) echo "checked"; ?>> <?php echo $this->lang->line('public-description'); ?><br>
          <input type="radio" name="gen[study-is-public]" value="0" <?php if (!$study_details['study-is-public']) echo "checked"; ?>> <?php echo $this->lang->line('private-description'); ?>
        </div>
      </div>
    </div>
  </div>
</div>