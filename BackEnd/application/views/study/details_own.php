<div class="page-header">
  <h2><?php echo $study_details['study-title']; ?></h2>
</div>


<div class="panel panel-info">
  <div class="panel-heading">
    <?php echo $this->lang->line('study-sharing'); ?>
  </div>
  <div class="panel-body">
    <div class="row">
      <div class="col-sm-6">
        <p><strong><?php echo $this->lang->line('study-shared-with'); ?></strong></p>
          <ul class="list-group">
            <?php foreach ($shared_with as $sw):
              echo '<li class="list-group-item"><div class="row"><div class="col-md-6">' . $sw['username'] . '</div>  <div class="col-md-6 text-right"><a href="' . site_url('study/remove_share/'.$study_details['id'].'/'.$sw['users_id']) . '" class="text-danger">' . $this->lang->line('remove-user') . '</a></div></div></li>';
            endforeach; ?>
          </ul>
      </div>
      <div class="col-sm-6">
  			<?php echo form_open('study/share/'.$study_details['id']); ?>
        <div class="form-group">
          <label class="control-label" for="share-study-username"><?php echo $this->lang->line('enter-name'); ?></label>
          <input class="form-control" list="usernames" id="share-study-username" name="share-study-username" placeholder="<?php echo $this->lang->line('name-placeholder'); ?>" required>
          <datalist id="usernames">
            <?php foreach ($other_users as $ou):
              echo '<option value="' . $ou['username'] . '">';
            endforeach; ?>
          </datalist>
        </div>
        <input class="btn btn-primary" type="submit" name="submit" id="submit-button" value="<?php echo $this->lang->line('share-study'); ?>"/>
  			</form>
      </div>
    </div>
  </div>
</div>
<?php if (!$study_details['study-is-public']) { ?>
<div class="panel panel-success">
  <div class="panel-heading">
    <?php echo $this->lang->line('adding-participants'); ?>
  </div>
  <div class="panel-body">
    <div class="row">
      <div class="col-sm-6">
        <p><strong><?php echo $this->lang->line('participants'); ?></strong></p>
          <ul class="list-group">
            <?php foreach ($participants as $p):
              echo '<li class="list-group-item"><div class="row"><div class="col-md-6">' . $p['email'] . '</div>  <div class="col-md-6 text-right"><a href="' . site_url('study/remove_participant/'.$study_details['id'].'/'.$p['participant_id']) . '" class="text-danger">' . $this->lang->line('remove-user') . '</a></div></div></li>';
            endforeach; ?>
          </ul>
      </div>
      <div class="col-sm-6">
        <?php echo form_open('study/add_participant/'.$study_details['id']); ?>
        <div class="form-group">
          <label class="control-label" for="add-participant-username"><?php echo $this->lang->line('enter-email'); ?></label>
          <input class="form-control" list="partic" id="add-participant-username" name="add-participant-username" placeholder="<?php echo $this->lang->line('email-placeholder'); ?>" required>
          <datalist id="partic">
            <?php foreach ($all_participants as $ou):
              echo '<option value="' . $ou['email'] . '">';
            endforeach; ?>
          </datalist>
        </div>
        <input class="btn btn-primary" type="submit" name="submit" id="submit-button" value="<?php echo $this->lang->line('add-participant'); ?>"/>
        </form>
      </div>
    </div>
  </div>
</div>
<?php } ?>
<br><br>

<div class="panel panel-info">
  <div class="panel-heading">
    <?php echo $this->lang->line('study-info'); ?>
  </div>
  <div class="panel-body">
    <p><strong><?php echo $study_details['study-is-public'] ? $this->lang->line('public') : $this->lang->line('private') ?></strong> <?php echo $this->lang->line('study'); ?>.</p>
    <p><strong><?php echo $this->lang->line('period'); ?>: </strong>
        <?php echo explode(" ", $study_details['study-start-date'])[0]; ?> - 
        <?php echo explode(" ", $study_details['study-end-date'])[0]; ?><br>
        <strong><?php echo $this->lang->line('duration-for-user'); ?>:</strong> <?php echo $study_details['study-duration-for-user'] ? ((($study_details['study-duration-time'] / 10080) < 1) ? ($study_details['study-duration-time'] / 1440) . ' ' . $this->lang->line('days') : ($study_details['study-duration-time'] / 10080) . ' ' . $this->lang->line('weeks')) : $this->lang->line('as-long-as-they-wish') ?>
        </p>
    <p><strong><?php echo $this->lang->line('beeps'); ?>: </strong>
        <strong><?php echo $study_details['study-beeps-per-day']; ?></strong> <?php echo $this->lang->line('beeps-per-day'); ?>. 
        <?php echo $this->lang->line('beeps-time-between'); ?> <strong><?php echo ($study_details['study-min-time-between-beeps'] / 60); ?></strong> <?php echo $this->lang->line('beep-hours'); ?>. <?php echo $this->lang->line('beep-interval'); ?>: 
        <strong><?php echo $study_details['study-beep-start-time']; ?></strong> - <strong><?php echo $study_details['study-beep-end-time']; ?></strong>
        </p>
    <p><strong><?php echo $this->lang->line('postponing'); ?>: </strong>
        <?php echo $study_details['study-allow-postpone'] ? $study_details['study-postpone-time'] . ' ' . $this->lang->line('minutes') : $this->lang->line('postpone-disabled') ?>
        </p>
    <p><strong><?php echo $this->lang->line('study-language'); ?>: </strong>
        <?php 
          if ($study_details['study-language'] == 'est') {
            echo $this->lang->line('study-lang-est');
          } else if ($study_details['study-language'] == 'eng') {
            echo $this->lang->line('study-lang-eng');
          } else if ($study_details['study-language'] == 'rus') {
            echo $this->lang->line('study-lang-rus');
          } else if ($study_details['study-language'] == 'ger') {
            echo $this->lang->line('study-lang-ger');
          } else {
            echo $this->lang->line('study-lang-unknown') . ': \'' . $study_details['study-language'] . '\'';
          }
        ?>
        </p>
  </div>
</div>

<div class="panel panel-info">
  <div class="panel-heading">
    <?php echo $this->lang->line('questions'); ?>
  </div>
  <div class="panel-body">
    <?php $ind = 1;
    foreach ($questions as $question): ?>
      <?php echo $ind==1 ? '' : '<hr>'; ?>
      <p><?php echo $ind . '. ' . $question['question-title']; ?></p>
      <?php if ($question['question-type'] == 'freetext') {
        echo '&nbsp;&nbsp;' . $this->lang->line('answer') . ': ' . $this->lang->line('freetext');
      } else if ($question['question-type'] == 'multichoice') {
        echo '&nbsp;&nbsp;' . $this->lang->line('answer') . ': ' . ($question['question-multichoice-single-choice'] ? $this->lang->line('single-choice') : $this->lang->line('one-or-more-choice'));
        echo '<p>';
        $a_ind= 1;
        foreach (json_decode($question['question-multichoices']) as $answer): 
          echo '&nbsp;&nbsp;&nbsp;&nbsp;' . $a_ind . '. ' . $answer . '<br>';
          $a_ind += 1;
        endforeach;
        echo '</p>';
      }
    $ind += 1;
    endforeach; ?>
  </div>
</div>

<div class="panel panel-info">
  <div class="panel-heading">
    <?php echo $this->lang->line('events'); ?>
  </div>
  <div class="panel-body">
    <?php
    if (count($events) == 0) {
      echo '<p class="text-muted">' . $this->lang->line('no-events') . '</p>';
    } else {
    $ind = 1;
    foreach ($events as $event): ?>
      <?php echo $ind==1 ? '' : '<hr>'; ?>
      <p><?php echo $ind . '. ' . $event['event-title']; ?></p>
      <?php
        echo 'Kontrollaeg: <strong>' . $event['event-control-time'] . '</strong> ';
        if ($event['event-control-time-unit'] == "m"){
          echo $this->lang->line('minutes');
        } else if ($event['event-control-time-unit'] == "h"){
          echo $this->lang->line('hours');
        } else if ($event['event-control-time-unit'] == "d"){
          echo $this->lang->line('days');
        } else {
          echo $this->lang->line('event-unknown-unit');
        }
      ?>
    <?php $ind += 1;
    endforeach;
    }?>
  </div>
</div>

<div class="panel panel-success">
  <div class="panel-heading">
    <?php echo $this->lang->line('results'); ?>
  </div>
  <div class="panel-body">
    <p><a href="<?php echo site_url('study/study_results/'.$study_details['id']); ?>"><?php echo $this->lang->line('question-csv'); ?></a></p>
    <?php if (count($events) > 0) { ?>
    <p><a href="<?php echo site_url('study/event_results/'.$study_details['id']); ?>"><?php echo $this->lang->line('events-csv'); ?></a></p>
    <?php } ?>
  </div>
</div>

<a href="<?php echo site_url('study/modify/'.$study_details['id']); ?>" type="button" class="btn btn-lg btn-warning"><?php echo $this->lang->line('modify-study'); ?></a>
<button type="button" class="btn btn-lg btn-danger" data-toggle="modal" data-target="#deleteModal"><?php echo $this->lang->line('delete-study'); ?></button>

<div id="deleteModal" class="modal fade" role="dialog">
  <div class="modal-dialog">

    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">&times;</button>
        <h4 class="modal-title"><?php echo $this->lang->line('delete-confirm-modal-title'); ?></h4>
      </div>
      <div class="modal-body">
        <p><?php echo $this->lang->line('delete-confirm-text'); ?> "<?php echo $study_details['study-title']; ?>"</p>
        <p class="small text-danger"><?php echo $this->lang->line('delete-confirm-irreversible'); ?></p>
      </div>
      <div class="modal-footer">
        <a href="<?php echo site_url('study/delete/'.$study_details['id']); ?>" class="btn btn-danger"><?php echo $this->lang->line('delete-study'); ?></a>
        <button type="button" class="btn btn-default" data-dismiss="modal"><?php echo $this->lang->line('cancel-deletion'); ?></button>
      </div>
    </div>

  </div>
</div>
