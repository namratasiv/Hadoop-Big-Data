<?php

class Contactinsert extends CI_Controller {

function __construct() {
parent::__construct();
$this->load->model('contact');
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

$this->form_validation->set_rules('comments', 'comments', 'required');


if ($this->form_validation->run() == FALSE) {
$this->load->view('contact');
} else {


//Setting values for  client table columns
$data = array(
'fname' => $this->input->post('fname'),
'lname' => $this->input->post('lname'),
'email' => $this->input->post('email'),
'phone' => $this->input->post('phone'),
'comments' => $this->input->post('comments')
);

//Transfering data to Model
$this->contact->form_insert($data);

//Loading View
$this->load->view('contactsuc', $data);




}

}
}
?>