<?php
class Service extends CI_Controller {
    public function index(){
      $this->load->helper('url');
      $this->load->view('header');
      $this->load->view('service');
      $this->load->view('footer');
    }
  }
?>