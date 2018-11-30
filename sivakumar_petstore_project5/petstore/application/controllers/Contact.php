<?php
class Contact extends CI_Controller {
    public function index(){
        $this->load->helper('url');
      $this->load->view('header');
      $this->load->view('contact');
      $this->load->view('footer');
    }
  }
?>