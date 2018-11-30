<!DOCTYPE HTML>
<html lang="en">
<head>
<meta charset="utf-8">
<link rel="stylesheet"  href="<?php echo base_url();?>assets/css/pet.css">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<div class="pet"> 

    <h1> Pet Store </h1>  

</div>


<div class="row">
 <div class="col-2"> 
<nav>
<?php $this->load->helper('url');
 ?>
   <a href="<?php echo site_url("home")?>"> Home</a> <br>
   <a href="<?php echo site_url("about")?>">About Us </a><br>
    <a href="<?php echo site_url("contact")?>">Contact Us </a> <br>
   <a href="<?php echo site_url("client")?>">Client </a> <br>
   <a href="<?php echo site_url("service")?>">Service</a><br>
   <a href="<?php echo site_url("login")?>">Login</a><br>
</nav>


</div>  

<div class="col-8">

<img src="<?php echo base_url();?>assets/css/pet store banner 5 png (1).png" alt="petbanner" >