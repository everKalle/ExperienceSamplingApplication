<!DOCTYPE html>
<html lang="et">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <meta name="description" content="">
    <meta name="author" content="">

    <title><?php echo $title; ?></title>

    <!-- Bootstrap core CSS -->
    <link href="<?php echo base_url(); ?>bootstrap/css/bootstrap.min.css" rel="stylesheet">

    <!-- Custom styles -->
    <link href="<?php echo base_url(); ?>public/css/style.css" rel="stylesheet">

    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
  </head>

  <body>

    <div class="container">

      <!-- Static navbar -->
      <nav class="navbar navbar-default">
        <div class="container-fluid">
          <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
              <span class="sr-only">Toggle navigation</span>
              <span class="icon-bar"></span>
              <span class="icon-bar"></span>
              <span class="icon-bar"></span>
            </button>
          </div>
          <div id="navbar" class="navbar-collapse collapse">
            <ul class="nav navbar-nav">
<?php if ($logged_in != FALSE) { ?>
              <li <?php if ($active_page == "own_studies") echo 'class="active"'; ?>><a href="<?php echo base_url(); ?>index.php">Minu uuringud</a></li>
              <li <?php if ($active_page == "shared_studies") echo 'class="active"'; ?>><a href="<?php echo base_url(); ?>index.php/study/shared/">Minuga jagatud uuringud</a></li>
              <li <?php if ($active_page == "create_study") echo 'class="active"'; ?>><a href="<?php echo base_url(); ?>index.php/study/create/">Uus uuring</a></li>

            </ul>
            <ul class="nav navbar-nav navbar-right">
              <li <?php if ($active_page == "account_settings") echo 'class="active"'; ?>><a href="<?php echo base_url(); ?>index.php/settings">Konto seaded</a></li>
	      <li><a href="<?php echo base_url(); ?>index.php/login/logout">Logi välja</a></li>
<?php } else { ?>
	      <li><a href="<?php echo base_url(); ?>index.php/login">Logi sisse</a></li>
<?php } ?>
            </ul>
          </div><!--/.nav-collapse -->
        </div><!--/.container-fluid -->
      </nav>
