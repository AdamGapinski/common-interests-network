package com.adam.commoninterestsservice.controllers;

import com.adam.commoninterestsservice.entities.Comment;
import com.adam.commoninterestsservice.entities.Group;
import com.adam.commoninterestsservice.entities.User;
import com.adam.commoninterestsservice.services.CommentService;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/posts/{postId}/comments")
public class CommentRestController {

    private final CommentService commentService;
    private final UserService userService;
    private final GroupService groupService;

    @Autowired
    public CommentRestController(CommentService commentService, UserService userService, GroupService groupService) {
        this.commentService = commentService;
        this.userService = userService;
        this.groupService = groupService;
    }

    @RequestMapping(method = RequestMethod.GET)
    Collection<Comment> readAllComments(@PathVariable Long postId,
                                        @RequestParam(value="group_id", required = false) Long groupId) {
        return filterByGroupId(this.commentService.getAllByPostId(postId), groupId);
    }

    private Collection<Comment> filterByGroupId(Collection<Comment> comments, Long groupId) {
        if (groupId == null) {
            return comments;
        }
        Group group = groupService.get(groupId);
        return comments.stream()
                .filter(comment -> group.getUsers().stream().anyMatch(u -> u.getId().equals(comment.getAuthor().getId())))
                .collect(Collectors.toSet());
    }

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<?> createComment(@PathVariable Long postId, @RequestBody Comment input, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User author = userService.findByUsername(userDetails.getUsername());
        Comment created = this.commentService.addComment(input, postId, author);
        URI location = buildLocation(created);
        return ResponseEntity.created(location).build();
    }

    private URI buildLocation(Comment created) {
        return ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(created.getId()).toUri();
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{commentId}")
    ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
        this.commentService.removeComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
