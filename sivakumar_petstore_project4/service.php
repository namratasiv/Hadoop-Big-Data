<?php
require_once('SMTP.php');
require_once('PHPMailer.php');
require_once('Exception.php');

use \PHPMailer\PHPMailer\PHPMailer;
use \PHPMailer\PHPMailer\Exception;

$servername = "localhost";
$database = "pet";
$username = "root";
$password = "";

// Create connection

$conn = mysqli_connect($servername, $username, $password, $database);

// Check connection

if (!$conn) {
      die("Connection failed: " . mysqli_connect_error());
}
 
echo "<p>Success!</p><br>";

if (isset($_POST['fname'])) {
	$fname = mysqli_real_escape_string($conn, $_POST['fname']);
  } else {
	echo '<p>It looks like you forgot to enter your first name.</p>';
  }
  if (isset($_POST['lname'])) {
	$lname = mysqli_real_escape_string($conn, $_POST['lname']);
  } else {
	echo '<p>It looks like you forgot to enter your last name.</p>';
  }
  if (isset($_POST['email'])) {
	$email = mysqli_real_escape_string($conn, $_POST['email']);
  } else {
	echo '<p>It looks like you forgot to enter your email.</p>';
  }
  if (isset($_POST['phone'])) {
	$phone = mysqli_real_escape_string($conn, $_POST['phone']);
  } else {
	echo '<p>It looks like you forgot to enter your phone number.</p>';
  }if (isset($_POST['bname'])) {
	$bname = mysqli_real_escape_string($conn, $_POST['bname']);
  } else {
	echo '<p>It looks like you forgot to enter your business name.!</p>';
  }
 
  $query2 = "INSERT INTO users ( email, password, roleid) VALUES ('$email', '1234567', '1')";     
  if (mysqli_query($conn, $query2)) {
      echo "<p>Recorded!</p>";
  } else {
      echo "Error: " . $sql . "<br>" . mysqli_error($conn);
  }
  $sql = "INSERT INTO service (fname, lname, email, phone) VALUES ('$fname', '$lname', '$email', '$phone')";
  if (mysqli_query($conn, $sql)) {
      echo "<p>We will get in touch with you soon!</p>";
  } else {
      echo "Error: " . $sql . "<br>" . mysqli_error($conn);
  }

$mail=new PHPMailer(true); // Passing `true` enables exceptions

try {
    //settings
    $mail->SMTPDebug=2; // Enable verbose debug output
    $mail->isSMTP(); // Set mailer to use SMTP
    $mail->Host='smtp.gmail.com';
    $mail->SMTPAuth=true; // Enable SMTP authentication
    $mail->Username='nam.sivvv@gmail.com'; // SMTP username
    $mail->Password='sairam1207'; // SMTP password
    $mail->SMTPSecure='ssl';
    $mail->Port=465;

    $mail->setFrom('sender@whatever.com', 'PetStore Inc.');

    //recipient
    $mail->addAddress($email, $fname);     // Add a recipient
    //content
    $mail->isHTML(true); // Set email format to HTML
    $mail->Subject='New Business Registration - Pet Store';
    $mail->Body='<p>Hello, Thank you for registering as a business with us!</p><br>You can login with your email address: '.$email.' and password 1234567';
    $mail->AltBody='This is the body in plain text for non-HTML mail clients';

    $mail->send();

    echo 'Message has been sent';
    echo '<a href = "login.html"> Click here to login </a>';
} 
catch(Exception $e) {
    echo 'Message could not be sent.';
    echo 'Mailer Error: '.$mail->ErrorInfo;
}



mysqli_close($conn);

?>