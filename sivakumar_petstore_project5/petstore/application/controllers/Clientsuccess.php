<?php

class Clientsuccess extends CI_Controller {

function __construct() {
parent::__construct();
$this->load->model('client');
}
function index() {


//Including validation library
$this->load->library('form_validation');

$this->form_validation->set_error_delimiters('<div class="error">', '</div>');

//Validating FName Field
$this->form_validation->set_rules('fname', 'First Name', 'required|min_length[2]|max_length[25]');

//Validating LName Field
$this->form_validation->set_rules('lname', 'Last Name', 'required|min_length[2]|max_length[25]');

//Validating Email Field
$this->form_validation->set_rules('email', 'Email', 'required|valid_email');

//Validating Phone Field
$this->form_validation->set_rules('phone', 'Phone', 'required|regex_match[/^[0-9]{10}$/]');



if ($this->form_validation->run() == FALSE) {
$this->load->view('client');
} else {


//Setting values for  client table columns
$data = array(
'fname' => $this->input->post('fname'),
'lname' => $this->input->post('lname'),
'email' => $this->input->post('email'),
'phone' => $this->input->post('phone')
);
$e = $this->input->post('email');
//Transfering data to Model
$this->client->form_insert($data);

//Loading View
$this->load->view('clientsuc', $data);

$data1 = array(
    
    'email' => $this->input->post('email'),
    'password' => '1234567',
    'roleid'=> '2'
    );
    
    //Transfering data to Model
    $this->client->form_insertt($data1);
}
$this->load->library('email'); // Note: no $config param needed
$this->email->from('nam.sivvv@gmail.com');
$this->email->to($e);
$this->email->subject('Pet Store Inc. Registration Successful');
$this->email->message('You have registered as a client successfully! You can login with password 1234567');
$this->email->send();

}


}

?>