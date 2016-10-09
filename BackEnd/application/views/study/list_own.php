<div class="page-header">
  <h2>Aktiivsed uuringud</h2>
</div>

<?php if (count($active_studies) == 0) {?>
  <h4 class="text-muted">Puuduvad</h4>
<?php } else { ?>
  <div class="list-group">
  <?php foreach ($active_studies as $study): ?>
    <a href="<?php echo site_url('study/view/'.$study['id']); ?>" class="list-group-item">
      <h4 class="list-group-item-heading"><?php echo $study['study-title']; ?></h4>
      <p class="list-group-item-text">
        <?php echo $study['study-is-public'] ? 'Avalik' : 'Privaatne' ?> uuring. Periood: 
        <?php echo explode(" ", $study['study-start-date'])[0]; ?> - 
        <?php echo explode(" ", $study['study-end-date'])[0]; ?></p>
    </a>
  <?php endforeach; ?>
  </div>
<?php } ?>

<div class="page-header">
  <h2>Lõppenud uuringud</h2>
</div>

<?php if (count($ended_studies) == 0) {?>
  <h4 class="text-muted">Puuduvad</h4>
<?php } else { ?>
  <div class="list-group">
  <?php foreach ($ended_studies as $study): ?>
    <a href="<?php echo site_url('study/view/'.$study['id']); ?>" class="list-group-item">
      <h4 class="list-group-item-heading"><?php echo $study['study-title']; ?></h4>
      <p class="list-group-item-text">
        <?php echo $study['study-is-public'] ? 'Avalik' : 'Privaatne' ?> uuring. Periood: 
        <?php echo explode(" ", $study['study-start-date'])[0]; ?> - 
        <?php echo explode(" ", $study['study-end-date'])[0]; ?></p>
    </a>
  <?php endforeach; ?>
  </div>
<?php } ?>