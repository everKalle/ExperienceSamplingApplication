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
          <input class="form-control" type="text" id="study-title" name="gen[study-title]" size="100" placeholder="Uuringu pealkiri" required/>
        </div>
      </div>
      <div class="form-group" id="study-start-date-group">
        <label class="control-label col-sm-3" for="study-start-date">Alguskuupäev</label>
        <div class="col-sm-7">
          <input class="form-control" type="text" id="study-start-date" name="gen[study-start-date]" size="30" placeholder="yyyy-mm-dd" required/>
        </div>
      </div>
      <div class="form-group" id="study-end-date-group">
        <label class="control-label col-sm-3" for="study-end-date">Lõppkuupäev</label>
        <div class="col-sm-7">
          <input class="form-control" type="text" id="study-end-date" name="gen[study-end-date]" size="30" placeholder="yyyy-mm-dd" required/><br>
          <span style="display: none;" id="study-date-help" class="help-block">Alguskuupäev peab eelnema lõppkuupäevale!</span>
          <span style="display: none;" id="study-date-help-too-early" class="help-block">Alguskuupäev ei saa eelneda praegusele kuupäevale!</span>
        </div>
      </div>
      <div class="form-group">
        <label class="control-label col-sm-3" for="study-duration-for-user">Kestvus</label>
        <div class="col-sm-7">
          <input type="radio" name="gen[study-duration-for-user]" value="0" checked> Kuni kasutaja soovib<br>
          <input type="radio" name="gen[study-duration-for-user]" value="1"> Piiratud kestvus:
        </div>
      </div>
      <div class="form-group" id="study-duration-group">
        <label class="control-label col-sm-3" for="no-target">&nbsp;</label>
        <div class="col-sm-7 form-inline">
          <input class="form-control input-sm" type="number" id="study-duration-time" name="gen[study-duration-time]" size="5" placeholder="0" min="1"/>
          <select class="form-control input-sm" name="study-duration-time-unit" id="study-duration-time-unit">
            <option value="1440">Päeva</option>
            <option value="10080" selected>Nädalat</option>
          </select>
          <span style="display: none;" id="study-duration-help-exceeded" class="help-block">Kestvus ühe kasutaja jaoks on pikem kui kogu uuringu kestvus!</span>
        </div>
      </div><hr>
      <div class="form-group" id="study-beep-amt-group">
        <label class="control-label col-sm-3" for="study-beeps-per-day">Piiksude arv päevas:</label>
        <div class="col-sm-7 form-inline">
          <input class="form-control input-sm" type="number" id="study-beeps-per-day" name="gen[study-beeps-per-day]" size="5" min="1" placeholder="0" required/> tk
          <span style="display: none;" id="study-beep-amt-help" class="help-block">Piiksude arv ei mahu piiksude perioodi sisse ära!</span>
        </div>
      </div>
      <div class="form-group">
        <label class="control-label col-sm-3" for="study-min-time-between-beeps">Minimaalne aeg piiksude vahel</label>
        <div class="col-sm-7 form-inline">
          <input class="form-control input-sm" type="number" id="study-min-time-between-beeps" name="gen[study-min-time-between-beeps]" size="5" min="1" placeholder="0" required/> tundi
        </div>
      </div>
      <div class="form-group" id="study-beep-time-group">
        <label class="control-label col-sm-3" for="study-beep-start-time">Piiksud toimuvad ajavahemikul</label>
        <div class="col-sm-7 form-inline">
          <input class="form-control input-sm" type="text" id="study-beep-start-time" name="gen[study-beep-start-time]" size="5" placeholder="00:00" required/> kuni
          <input class="form-control input-sm" type="text" id="study-beep-end-time" name="gen[study-beep-end-time]" size="5" placeholder="00:00" required/>
          <span style="display: none;" id="study-time-help-before" class="help-block">Piiksude lõppaeg peab olema hiljem kui algusaeg!</span>
        </div>
      </div><hr>
      <div class="form-group">
        <label class="control-label col-sm-3" for="no-target">Vastamise edasilükkamine</label>
        <div class="col-sm-7">
          <input type="checkbox" id="study-allow-postpone" name="gen[study-allow-postpone]" value="1" checked></b>
        </div>
      </div>
      <div class="form-group">
        <label class="control-label col-sm-3" for="study-postpone-time">Vastamist lükatakse edasi</label>
        <div class="col-sm-7 form-inline">
          <input class="form-control input-sm" type="number" name="gen[study-postpone-time]" size="5" min="1" placeholder="0" value="15" required /> minutit
        </div>
      </div><hr>
      <div class="form-group">
        <label class="control-label col-sm-3" for="study-language">Uuringu keel: </label>
        <div class="col-sm-7 form-inline">
          <select class="form-control input-sm" name="gen[study-language]">
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
          <input type="radio" name="gen[study-is-public]" value="1" checked> Avatud - igaüks saab osaleda<br>
          <input type="radio" name="gen[study-is-public]" value="0"> Privaatne - administraator lisab osalejad
        </div>
      </div>
    </div>
  </div>
</div>

          
