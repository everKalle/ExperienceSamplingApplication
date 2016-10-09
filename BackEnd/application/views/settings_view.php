   <?php echo validation_errors(); ?>
   <?php echo form_open('Settings/update_password'); ?>
     <label for="oldpass">Vana parool:</label>
     <input type="password" size="20" name="old_password"/>
     <br/>
     <label for="password">Uus parool:</label>
     <input type="password" size="20" id="password" name="new_password"/>
     <br/>     
	 <label for="password">Korda uut parooli:</label>
     <input type="password" size="20" id="password" name="new_password2"/>
     <br/>
     <input type="submit" value="Uuenda parooli"/>
   </form>
