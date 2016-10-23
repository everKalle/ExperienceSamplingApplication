<div class="page-header">
  <h2><?php echo $study_details['study-title']; ?></h2>
</div>


<div class="panel panel-info">
  <div class="panel-heading">
    Uuringu jagamine
  </div>
  <div class="panel-body">
    <div class="row">
      <div class="col-sm-6">
        <p><strong>Uuring on jagatud järgnevate inimestega: </strong></p>
          <ul class="list-group">
            <?php foreach ($shared_with as $sw):
              echo '<li class="list-group-item"><div class="row"><div class="col-md-6">' . $sw['username'] . '</div>  <div class="col-md-6 text-right"><a href="' . site_url('study/remove_share/'.$study_details['id'].'/'.$sw['users_id']) . '" class="text-danger">Eemalda</a></div></div></li>';
            endforeach; ?>
          </ul>
      </div>
      <div class="col-sm-6">
  			<?php echo form_open('study/share/'.$study_details['id']); ?>
        <div class="form-group">
          <label class="control-label" for="share-study-username">Sisesta nimi:</label>
          <input class="form-control" list="usernames" id="share-study-username" name="share-study-username" placeholder="Nimi" required>
          <datalist id="usernames">
            <?php foreach ($other_users as $ou):
              echo '<option value="' . $ou['username'] . '">';
            endforeach; ?>
          </datalist>
        </div>
        <input class="btn btn-primary" type="submit" name="submit" id="submit-button" value="Jaga uuring"/>
  			</form>
      </div>
    </div>
  </div>
</div>
<?php if (!$study_details['study-is-public']) { ?>
<div class="panel panel-success">
  <div class="panel-heading">
    Uuringusse osalejate lisamine
  </div>
  <div class="panel-body">
    <strong>TODO</strong>
  </div>
</div>
<?php } ?>
<br><br>

<div class="panel panel-info">
  <div class="panel-heading">
    Uuringu info
  </div>
  <div class="panel-body">
    <p><strong><?php echo $study_details['study-is-public'] ? 'Avalik' : 'Privaatne' ?></strong> uuring.</p>
    <p><strong>Periood: </strong>
        <?php echo explode(" ", $study_details['study-start-date'])[0]; ?> - 
        <?php echo explode(" ", $study_details['study-end-date'])[0]; ?><br>
        <strong>Kestvus kasutaja jaoks:</strong> <?php echo $study_details['study-duration-for-user'] ? ((($study_details['study-duration-time'] / 10080) < 1) ? ($study_details['study-duration-time'] / 1440) . ' päeva' : ($study_details['study-duration-time'] / 10080) . ' nädalat') : 'Kuni kasutaja soovib' ?>
        </p>
    <p><strong>Piiksud: </strong>
        <strong><?php echo $study_details['study-beeps-per-day']; ?></strong> piiksu päevas. 
        Iga piiksu vahel vähemalt <strong><?php echo ($study_details['study-min-time-between-beeps'] / 60); ?></strong> tund(i). Ajavahemikul: 
        <strong><?php echo $study_details['study-beep-start-time']; ?></strong> - <strong><?php echo $study_details['study-beep-end-time']; ?></strong>
        </p>
    <p><strong>Vastamise edasilükkamine: </strong>
        <?php echo $study_details['study-allow-postpone'] ? $study_details['study-postpone-time'] . ' minutit' : 'Keelatud' ?>
        </p>
    <p><strong>Uuringu keel: </strong>
        <?php 
          if ($study_details['study-language'] == 'est') {
            echo 'Eesti';
          } else if ($study_details['study-language'] == 'eng') {
            echo 'Inglise';
          } else if ($study_details['study-language'] == 'rus') {
            echo 'Vene';
          } else if ($study_details['study-language'] == 'ger') {
            echo 'Saksa';
          } else {
            echo 'Tundmatu: \'' . $study_details['study-language'] . '\'';
          }
        ?>
        </p>
  </div>
</div>

<div class="panel panel-info">
  <div class="panel-heading">
    Küsimused
  </div>
  <div class="panel-body">
    <?php $ind = 1;
    foreach ($questions as $question): ?>
      <?php echo $ind==1 ? '' : '<hr>'; ?>
      <p><?php echo $ind . '. ' . $question['question-title']; ?></p>
      <?php if ($question['question-type'] == 'freetext') {
        echo '&nbsp;&nbsp;Vastus: Vabatekst';
      } else if ($question['question-type'] == 'multichoice') {
        echo '&nbsp;&nbsp;Vastus: ' . ($question['question-multichoice-single-choice'] ? 'Üks valik järgnevatest:' : 'Vähemalt üks valik järgnevatest:');
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
    Sündmused
  </div>
  <div class="panel-body">
    <?php
    if (count($events) == 0) {
      echo '<p class="text-muted">Puuduvad</p>';
    } else {
    $ind = 1;
    foreach ($events as $event): ?>
      <?php echo $ind==1 ? '' : '<hr>'; ?>
      <p><?php echo $ind . '. ' . $event['event-title']; ?></p>
      <?php
        echo 'Kontrollaeg: <strong>' . $event['event-control-time'] . '</strong> ';
        if ($event['event-control-time-unit'] == "m"){
          echo 'minutit.';
        } else if ($event['event-control-time-unit'] == "h"){
          echo 'tundi.';
        } else if ($event['event-control-time-unit'] == "d"){
          echo 'päeva.';
        } else {
          echo 'vigast ühikut.';
        }
      ?>
    <?php $ind += 1;
    endforeach;
    }?>
  </div>
</div>

<div class="panel panel-success">
  <div class="panel-heading">
    Tulemused
  </div>
  <div class="panel-body">
    <strong>TODO</strong>
  </div>
</div>

<a href="<?php echo site_url('study/modify/'.$study_details['id']); ?>" type="button" class="btn btn-lg btn-warning">Muuda</a>
<button type="button" class="btn btn-lg btn-danger" data-toggle="modal" data-target="#deleteModal">Kustuta</button>

<div id="deleteModal" class="modal fade" role="dialog">
  <div class="modal-dialog">

    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">&times;</button>
        <h4 class="modal-title">Palun kinnitata uuringu kustutamine</h4>
      </div>
      <div class="modal-body">
        <p>Kas oled kindel, et soovid kustutada uuringu "<?php echo $study_details['study-title']; ?>"</p>
        <p class="small text-danger">Seda tegevust ei saa tagasi võtta!</p>
      </div>
      <div class="modal-footer">
        <a href="<?php echo site_url('study/delete/'.$study_details['id']); ?>" class="btn btn-danger">Kustuta</a>
        <button type="button" class="btn btn-default" data-dismiss="modal">Tühista</button>
      </div>
    </div>

  </div>
</div>
