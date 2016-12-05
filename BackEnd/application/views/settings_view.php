<div class="container" style="max-width:330px;">
   <span class="text-center text-danger"><?php echo validation_errors(); ?></span>
   <?php echo form_open('Settings/update_password'); ?>
     <p class="text-center"><?php echo $this->lang->line('old-password'); ?>: </p>
     <input type="password" class="form-control" placeholder="<?php echo $this->lang->line('old-password'); ?>" name="old_password" required>
     <br/>
     <p class="text-center"><?php echo $this->lang->line('new-password'); ?>: </p>
     <input type="password" class="form-control" placeholder="<?php echo $this->lang->line('new-password'); ?>" name="new_password" required>
     <br/>     
	   <p class="text-center"><?php echo $this->lang->line('repeat-new-password'); ?>: </p>
     <input type="password" class="form-control" placeholder="<?php echo $this->lang->line('repeat-new-password'); ?>" name="new_password2" required>
     <br/>
     <button class="btn btn-sm btn-primary center-block" type="submit" style="margin-top:10px; "><?php echo $this->lang->line('update-password'); ?></button>
   </form>
</div>