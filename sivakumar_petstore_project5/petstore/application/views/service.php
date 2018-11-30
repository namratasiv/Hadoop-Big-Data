

<h2> Service </h2> 
     </head>
     <body>
          <p> Required Information is marked with an asterisk(*)</p> <br>

           

<?php echo form_open('servicesuccess'); ?>

<?php if (isset($message)) { ?>

<?php } ?>
<?php echo form_label('First Name :'); ?> <?php echo form_error('fname'); ?><br />
<?php echo form_input(array('id' => 'fname', 'name' => 'fname')); ?><br />

<?php echo form_label('Last Name :'); ?> <?php echo form_error('lname'); ?><br />
<?php echo form_input(array('id' => 'lname', 'name' => 'lname')); ?><br />

<?php echo form_label('Email :'); ?> <?php echo form_error('email'); ?><br />
<?php echo form_input(array('id' => 'email', 'name' => 'email', 'placeholder' => 'Your email')); ?><br />

<?php echo form_label('Phone :'); ?> <?php echo form_error('phone'); ?><br />
<?php echo form_input(array('id' => 'phone', 'name' => 'phone')); ?><br />

<?php echo form_label('Business Name :'); ?> <?php echo form_error('bname'); ?><br />
<?php echo form_input(array('id' => 'bname', 'name' => 'bname')); ?><br />

<?php echo form_submit(array('id' => 'submit', 'value' => 'Submit')); ?>
<?php echo form_close(); ?><br/>
 </div>
</div>
     </body>
    </div>
     