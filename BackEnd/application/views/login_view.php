<div class="container" style="max-width:330px;">
  	 <?php echo validation_errors(); ?>
	   <?php echo form_open('VerifyLogin', array('class' => 'form-signin')); ?>
	<p class="text-center"> E-posti aadress </p>
        <input type="text" name="username" id="inputText" class="form-control" placeholder="Kasutajanimi" required autofocus>
	<p class="text-center" style="margin-top:10px;"> Parool </p>
        <input type="password" id="inputPassword" class="form-control" placeholder="Password" name="password" required>
        <button class="btn btn-sm btn-primary center-block" type="submit" style="margin-top:10px; ">Logi sisse</button>
      </form>

    </div> <!-- /container -->
