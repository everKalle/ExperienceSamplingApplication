<div class="container" style="max-width:330px;">
<h2 class="text-center"><?php echo $this->lang->line('activation-title'); ?></h2>
  	 <?php echo validation_errors(); ?>
	   <?php echo form_open('login/activate/' . $act_link, array('class' => 'form-signin')); ?>
		<p class="text-center" style="margin-top:10px;"> <?php echo $this->lang->line('password'); ?> </p>
        <input type="password" id="inputPassword" class="form-control" placeholder="<?php echo $this->lang->line('password'); ?>" name="password" required>
		<p class="text-center" style="margin-top:10px;"> <?php echo $this->lang->line('password-repeat'); ?> </p>
        <input type="password" id="inputPasswordRepeat" class="form-control" placeholder="<?php echo $this->lang->line('password-repeat'); ?>" name="password_repeat" required><br>
        <button class="btn btn-lg btn-primary center-block" type="submit" style="margin-top:10px; "><?php echo $this->lang->line('activate-account'); ?></button>
      </form>

    </div> <!-- /container -->
