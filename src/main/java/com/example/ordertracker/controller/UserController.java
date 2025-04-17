package com.example.ordertracker.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ordertracker.entity.User;
import com.example.ordertracker.model.UserDetailUpdateRequest;
import com.example.ordertracker.model.response.UserDetailsResponse;
import com.example.ordertracker.service.UserDetailSevice;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api")
public class UserController {
  private final UserDetailSevice userDetailService;

  public UserController(UserDetailSevice userDetailService) {
    super();
    this.userDetailService = userDetailService;
  }

  @PostMapping("/users/add")
  public String addUser(@RequestBody @Valid @NotNull User userDetails) {
    return userDetailService.addUserDetails(userDetails);
  }
  @GetMapping("/users")
  public ResponseEntity<List<UserDetailsResponse>> getAllUserDetails() {
    List<UserDetailsResponse> users = userDetailService.getAllUserDetails();
    return new ResponseEntity<>(users, HttpStatus.OK);
  }

  @GetMapping("/users/{id}")
  public ResponseEntity<UserDetailsResponse> getUserDetailsById(@PathVariable(name = "id") @NotEmpty String id) {
    return userDetailService.getUserDetailsById(id);
  }

  @PutMapping("/users/{id}")
  public String updateUserDetail(@PathVariable @NotEmpty String id,
      @RequestBody @Valid @NotNull UserDetailUpdateRequest userDetailsUpdate) {
    return userDetailService.updateUserDetails(id, userDetailsUpdate);
  }

  @DeleteMapping("/users/{id}")
  public String deleteUserDetail(@PathVariable @NotEmpty String id) {
    return userDetailService.deleteUserDetails(id);
  }
}
