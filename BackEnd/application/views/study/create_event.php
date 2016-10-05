<hr>
<div class="panel panel-success">
  <div class="panel-heading">
  	Sündmused
  </div>
  <div class="panel-body">
	<input type="hidden" id="study-event-count" name="event[study-event-count]" value="0" required/>
	<div id="eventHolder">
	</div>
	<button type="button" class="btn btn-lg btn-default" onClick="add_event();">Lisa sündmus</button>
	<button style="display: none;" id="event-remove-button" type="button" class="btn btn-lg btn-danger" onClick="remove_event();">Eemalda viimane sündmus</button>
  </div>
</div>

<div class="alert alert-warning" role="alert" id="alert-question-add">
    Uuringu salvestamiseks lisa vähemalt üks küsimus.
</div>

<input class="btn btn-lg btn-primary" type="submit" name="submit" id="submit-button" disabled="disabled" value="Salvesta uuring"/>

</form>
