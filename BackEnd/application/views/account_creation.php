<div class="container" style="max-width:520px;">
  	 <?php echo validation_errors(); ?>
	   <?php echo form_open('superuser'); ?>
	<p class="text-center"> <?php echo $this->lang->line('e-mail'); ?> </p>
        <input type="text" name="username" id="inputText" class="form-control" placeholder="<?php echo $this->lang->line('e-mail'); ?>" required autofocus>
        <br>
	<p class="text-center" style="margin-top:10px;"> <?php echo $this->lang->line('confirm-pass'); ?>: </p>
        <input type="password" id="inputPassword" class="form-control" placeholder="<?php echo $this->lang->line('password'); ?>" name="password" required>
        <br>
        <?php if ($error_msg != "") { ?>
        <div class="alert alert-danger" role="alert">
	      	<?php echo $error_msg; ?>
	      </div>
	      <?php } ?>

	      <?php if ($success_msg != "") { ?>
        <div class="alert alert-success" role="alert">
	      	<?php echo $success_msg; ?>
	      </div>
	      <?php } ?>
        <button class="btn btn-primary center-block" type="submit" style="margin-top:10px; "><?php echo $this->lang->line('create-account'); ?></button>
      </form>

    </div> <!-- /container -->
