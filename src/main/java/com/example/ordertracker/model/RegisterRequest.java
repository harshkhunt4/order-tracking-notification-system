package com.example.ordertracker.model;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

  @NotEmpty(message = "fullname cannot be empty.")
  @Size(max = 50)
	private String fullname;
  
  @NotEmpty(message = "email cannot be empty.")
  @Size(max = 50)
  @Email
	private String email;
  
  @NotEmpty(message = "password cannot be empty.")
  @Size(max = 20)
	private String password;
}
