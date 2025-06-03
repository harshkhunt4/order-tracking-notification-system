package com.example.ordertracker.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.example.ordertracker.entity.User;
import com.example.ordertracker.model.UserDetailUpdateRequest;
import com.example.ordertracker.model.response.UserDetailsResponse;
import com.example.ordertracker.repository.UserRepository;

@Service
public class UserDetailSevice {

  private final UserRepository userRepo;

  public UserDetailSevice(UserRepository userRepo) {
    super();
    this.userRepo = userRepo;
  }

  @PreAuthorize("hasAuthority('SCOPE_WRITE','SCOPE_DELETE')")
  public String addUserDetails(User userDetails) {
    userDetails.setRoles("ROLE_USER");
    userRepo.save(userDetails);
    return "user details are added";
  }

  @PreAuthorize("hasAuthority('SCOPE_WRITE')")
  public String updateUserDetails(String id, UserDetailUpdateRequest updateReq) {
    Optional<User> user = userRepo.findById(id);
    if (user.isPresent()) {
      User existUser = user.get();
      existUser.setFullname(updateReq.getFullName());
      existUser.setEmail(updateReq.getEmail());
      userRepo.save(existUser);
      return "user details for id " + id + " updated";
    } else {
      return "user not exist for id " + id;
    }
  }

  @PreAuthorize("hasAuthority('SCOPE_WRITE')")
  public List<UserDetailsResponse> getAllUserDetails() {
    List<UserDetailsResponse> userDetailsResponses = new ArrayList<>();
    List<User> users = userRepo.findAll();
    for (var user : users) {
      UserDetailsResponse userDetailsResponse = new UserDetailsResponse();
      BeanUtils.copyProperties(user, userDetailsResponse);
      userDetailsResponses.add(userDetailsResponse);
    }
    return userDetailsResponses;
  }

  @PreAuthorize("hasAuthority('SCOPE_READ')")
  public ResponseEntity<UserDetailsResponse> getUserDetailsById(String id) {
    UserDetailsResponse userDetailsResponse = new UserDetailsResponse();
    Optional<User> user = userRepo.findById(id);
    if (user.isPresent()) {
      BeanUtils.copyProperties(user.get(), userDetailsResponse);
      return new ResponseEntity<>(userDetailsResponse, HttpStatus.FOUND);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @PreAuthorize("hasAuthority('SCOPE_DELETE')")
  public String deleteUserDetails(String id) {
    Optional<User> user = userRepo.findById(id);
    if (user.isPresent()) {
      userRepo.deleteById(id);
      return "user details for id " + id + " deleted";
    } else {
      return "user not exist for id " + id;
    }
  }
}
