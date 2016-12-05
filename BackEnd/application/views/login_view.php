<div class="container" style="max-width:330px;">
  	 <?php echo validation_errors(); ?>
	   <?php echo form_open('VerifyLogin', array('class' => 'form-signin')); ?>
	<p class="text-center"> <?php echo $this->lang->line('e-mail'); ?> </p>
        <input type="text" name="username" id="inputText" class="form-control" placeholder="<?php echo $this->lang->line('e-mail'); ?>" required autofocus>
	<p class="text-center" style="margin-top:10px;"> <?php echo $this->lang->line('password'); ?> </p>
        <input type="password" id="inputPassword" class="form-control" placeholder="<?php echo $this->lang->line('password'); ?>" name="password" required>
        <button class="btn btn-sm btn-primary center-block" type="submit" style="margin-top:10px; "><?php echo $this->lang->line('log-in'); ?></button>
      </form>

    </div> <!-- /container -->
