<div class="page-header">
  <h2><?php echo $study_details['study-title']; ?></h2>
  <h4 class="text-muted">Omanik: <?php echo $owner_name; ?></h4>
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
<br><br>
<?php } ?>

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
