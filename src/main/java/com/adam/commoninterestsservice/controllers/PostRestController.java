package com.adam.commoninterestsservice.controllers;

import com.adam.commoninterestsservice.entities.Post;
import com.adam.commoninterestsservice.entities.User;
import com.adam.commoninterestsservice.services.PostService;
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
@RequestMapping("/posts")
public class PostRestController {

    private final PostService postService;
    private final UserService userService;

    @Autowired
    public PostRestController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    @RequestMapping(method = RequestMethod.GET)
    Collection<Post> getAllPosts() {
         return this.postService.getAllPosts();
    }

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<?> createPost(@RequestBody Post input, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername());
        Post created = this.postService.addPost(input, user);
        URI location = buildPostLocation(created);
        return ResponseEntity.created(location).build();
    }

    private URI buildPostLocation(Post created) {
        return ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(created.getId()).toUri();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{postId}")
    Post readSinglePost(@PathVariable Long postId) {
        return this.postService.get(postId);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{postId}")
    ResponseEntity<?> updatePost(@PathVariable Long postId, @RequestBody Post input) {
        this.postService.updatePost(postId, input.getContents());
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{postId}")
    ResponseEntity<?> deletePost(@PathVariable Long postId) {
        this.postService.removePost(postId);
        return ResponseEntity.noContent().build();
    }
}
