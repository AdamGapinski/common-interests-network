package com.adam.commoninterestsservice.controllers;

import com.adam.commoninterestsservice.entities.Group;
import com.adam.commoninterestsservice.entities.User;
import com.adam.commoninterestsservice.services.GroupService;
import com.adam.commoninterestsservice.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collection;

@RestController
public class GroupRestController {

    private final GroupService groupService;
    private final UserService userService;

    @Autowired
    public GroupRestController(GroupService groupService, UserService userService) {
        this.groupService = groupService;
        this.userService = userService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/groups")
    Collection<Group> readAllGroups() {
        return this.groupService.getAllGroups();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/user/groups")
    Collection<Group> readAllGroupsByUserId(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername());
        return this.groupService.getAllGroupsByUserId(user);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/groups")
    ResponseEntity<?> createGroup(@RequestBody Group input, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername());
        Group created = this.groupService.addGroup(input, user);
        URI location = buildLocation(created);
        return ResponseEntity.created(location).build();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/user/groups")
    ResponseEntity<?> joinGroup(@RequestBody Group input, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername());
        groupService.joinGroup(input, user);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/user/groups/leave")
    ResponseEntity<?> leaveGroup(@RequestBody Group input, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername());
        groupService.leaveGroup(input, user);
        return ResponseEntity.noContent().build();
    }

    private URI buildLocation(Group created) {
        return ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(created.getId()).toUri();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/groups/{groupId}")
    Group readSingleGroup(@PathVariable Long groupId) {
        return this.groupService.get(groupId);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/groups/{groupId}")
    ResponseEntity<?> updateGroup(@PathVariable Long groupId, @RequestBody Group input) {
        this.groupService.updateGroup(groupId, input);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/groups/{groupId}")
    ResponseEntity<?> deleteGroup(@PathVariable Long groupId) {
        this.groupService.removeGroup(groupId);
        return ResponseEntity.noContent().build();
    }
}

