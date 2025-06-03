package com.example.ordertracker.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDetailUpdateRequest {

  @NotEmpty(message = "fullname cannot be empty.")
  @Size(max = 50)
  private String fullName;
  
  @NotEmpty(message = "email cannot be empty.")
  @Size(max = 50)
  @Email
  private String email;
  
  @NotEmpty(message = "password cannot be empty.")
  @Size(max = 20)
  private String password;
}
