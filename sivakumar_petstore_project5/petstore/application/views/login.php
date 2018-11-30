
<div id="wrapper">
<h2> Login </h2> 
     </head>
     <body>
          <p> Required Information is marked with an asterisk(*)</p> <br>
     
          <?php echo form_open('loginsuccess'); ?>

      <form action='<?php echo base_url();?>loginsuccess' method='post' name='loginsuccess'>
                
            <label for='email'>Email</label>
            <input type='email' name='email' id='email' size='100' /><br />
        
            <label for='password'>Password</label>
            <input type='password' name='password' id='password' size='25' /><br />                            
        
            <input type='Submit' value='Login' />            
        </form>
        </div>
</div>
</body>
</div>
     

 
