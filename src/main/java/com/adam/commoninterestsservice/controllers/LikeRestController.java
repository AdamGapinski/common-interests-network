package com.adam.commoninterestsservice.controllers;

import com.adam.commoninterestsservice.entities.Group;
import com.adam.commoninterestsservice.entities.Like;
import com.adam.commoninterestsservice.entities.User;
import com.adam.commoninterestsservice.services.GroupService;
import com.adam.commoninterestsservice.services.LikeService;
import com.adam.commoninterestsservice.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/posts/{postId}/likes")
public class LikeRestController {

    private final LikeService likeService;
    private final UserService userService;
    private final GroupService groupService;

    @Autowired
    public LikeRestController(LikeService likeService, UserService userService, GroupService groupService) {
        this.likeService = likeService;
        this.userService = userService;
        this.groupService = groupService;
    }

    @RequestMapping(method = RequestMethod.GET)
    Collection<Like> readAllLikes(@PathVariable Long postId,
                                  @RequestParam(value="group_id", required = false) Long groupId) {
        return filterByGroupId(this.likeService.getAllByPostId(postId), groupId);
    }

    private Collection<Like> filterByGroupId(Collection<Like> likes, Long groupId) {
        if (groupId == null) {
            return likes;
        }
        Group group = groupService.get(groupId);
        return likes.stream()
                .filter(like -> group.getUsers().stream().anyMatch(u -> u.getId().equals(like.getAuthor().getId())))
                .collect(Collectors.toSet());
    }

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<?> createLike(@PathVariable Long postId, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername());
        Like created = this.likeService.addLike(postId, user);
        URI location = buildLocation(created);
        return ResponseEntity.created(location).build();
    }

    private URI buildLocation(Like created) {
        return ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(created.getId()).toUri();
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{likeId}")
    ResponseEntity<?> deleteLike(@PathVariable Long likeId) {
        this.likeService.removeLike(likeId);
        return ResponseEntity.noContent().build();
    }
}
