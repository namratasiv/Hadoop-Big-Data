<?php

class Loginsuccess extends CI_Controller {

function __construct() {
parent::__construct();
$this->load->model('login');
$this->load->library('session');
}
function index() {


//Including validation library
$this->load->library('form_validation');

$this->form_validation->set_error_delimiters('<div class="error">', '</div>');



//Validating Email Field
$this->form_validation->set_rules('email', 'Email', 'required|valid_email');





if ($this->form_validation->run() == FALSE) {
//$this->load->view('login');
redirect('login');
} else {


$result = $this->login->validate();
// Now we verify the result
if(! $result){
    // If user did not validate, then show them login page again
    $this->load->view('servicelogin');
}else{
    // If user did validate, 
    // Send them to members area
    $this->load->view('clientlogin');
}        


}


}
}

?>