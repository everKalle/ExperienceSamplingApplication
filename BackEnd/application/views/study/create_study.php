<br>
<?php echo validation_errors(); ?>

<?php echo form_open('study/create')?>
<div class="panel panel-info">
  <div class="panel-heading">
    <h3 class="panel-title">Uuringu seaded</h3>
  </div>
  <div class="panel-body">
    <div class="form-horizontal">
      <div class="form-group">
        <label class="control-label col-sm-3" for="study-title">Uuringu pealkiri:</label>
        <div class="col-sm-7">
          <input class="form-control" type="text" id="study-title" name="study-title" size="100" required/>
        </div>
      </div>
      <div class="form-group">
        <label class="control-label col-sm-3" for="study-start-date">Alguskuupäev</label>
        <div class="col-sm-7">
          <input class="form-control" type="text" id="study-start-date" name="study-start-date" size="30" required/>
        </div>
      </div>
      <div class="form-group">
        <label class="control-label col-sm-3" for="study-end-date">Lõppkuupäev</label>
        <div class="col-sm-7">
          <input class="form-control" type="text" id="study-end-date" name="study-end-date" size="30" required/><br>
        </div>
      </div>
      <div class="form-group">
        <label class="control-label col-sm-3" for="study-duration-for-user">Kestvus</label>
        <div class="col-sm-7">
          <input type="radio" name="study-duration-for-user" value="0" checked> Kuni kasutaja soovib<br>
          <input type="radio" name="study-duration-for-user" value="1"> Piiratud kestvus:
        </div>
      </div>
      <div class="form-group">
        <label class="control-label col-sm-3" for="no-target">&nbsp;</label>
        <div class="col-sm-7 form-inline">
          <input class="form-control input-sm" type="number" id="study-duration-time" name="study-duration-time" size="5" min="1"/>
          <label for="study-duration-time">Nädalat</label>
        </div>
      </div><hr>
      <div class="form-group">
        <label class="control-label col-sm-3" for="study-beeps-per-day">Piiksude arv päevas:</label>
        <div class="col-sm-7 form-inline">
          <input class="form-control input-sm" type="number" id="study-beeps-per-day" name="study-beeps-per-day" size="5" min="1" required/> tk
        </div>
      </div>
      <div class="form-group">
        <label class="control-label col-sm-3" for="study-min-time-between-beeps">Minimaalne aeg piiksude vahel</label>
        <div class="col-sm-7 form-inline">
          <input class="form-control input-sm" type="number" id="study-min-time-between-beeps" name="study-min-time-between-beeps" size="5" min="1" required/> tundi
        </div>
      </div>
      <div class="form-group">
        <label class="control-label col-sm-3" for="study-beep-start-time">Piiksud toimuvad ajavahemikul</label>
        <div class="col-sm-7 form-inline">
          <input class="form-control input-sm" type="text" id="study-beep-start-time" name="study-beep-start-time" size="5" required/> kuni
          <input class="form-control input-sm" type="text" id="study-beep-end-time" name="study-beep-start-time" size="5" required/>
        </div>
      </div><hr>
      <div class="form-group">
        <label class="control-label col-sm-3" for="no-target">Vastamise edasilükkamine</label>
        <div class="col-sm-7">
          <input type="checkbox" name="study-allow-postpone" value="1" checked></b>
        </div>
      </div>
      <div class="form-group">
        <label class="control-label col-sm-3" for="study-postpone-time">Vastamist lükatakse edasi</label>
        <div class="col-sm-7 form-inline">
          <input class="form-control input-sm" type="number" id="study-postpone-time" name="study-postpone-time" size="5" min="1" value="15"/> minutit
        </div>
      </div><hr>
      <div class="form-group">
        <label class="control-label col-sm-3" for="study-language">Uuringu keel: </label>
        <div class="col-sm-7 form-inline">
          <select class="form-control input-sm" name="study-language">
            <option value="est" selected>Eesti</option>
            <option value="eng">Inglise</option>
            <option value="rus">Vene</option>
            <option value="ger">Saksa</option>
          </select>
        </div>
      </div>

      <div class="form-group">
        <label class="control-label col-sm-3" for="study-is-public">Uuringule ligipääs</label>
        <div class="col-sm-7 form-inline">
          <input type="radio" name="study-is-public" value="1" checked> Avatud - igaüks saab osaleda<br>
          <input type="radio" name="study-is-public" value="0"> Privaatne - administraator lisab osalejad
        </div>
      </div>
    </div>
  </div>
</div>



          