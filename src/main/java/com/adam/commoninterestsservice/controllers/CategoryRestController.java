package com.adam.commoninterestsservice.controllers;

import com.adam.commoninterestsservice.entities.Category;
import com.adam.commoninterestsservice.entities.Group;
import com.adam.commoninterestsservice.entities.Post;
import com.adam.commoninterestsservice.services.CategoryService;
import com.adam.commoninterestsservice.services.GroupService;
import com.adam.commoninterestsservice.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/categories")
public class CategoryRestController {

    private final CategoryService categoryService;
    private final PostService postService;
    private final GroupService groupService;

    @Autowired
    public CategoryRestController(CategoryService categoryService, PostService postService, GroupService groupService) {
        this.categoryService = categoryService;
        this.postService = postService;
        this.groupService = groupService;
    }

    @RequestMapping(method = RequestMethod.GET)
    Collection<Category> readAllCategories() {
        return this.categoryService.getAllCategories();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{categoryName}/posts")
    Collection<Post> getAllPostsByCategory(@PathVariable String categoryName,
                                           @RequestParam(value="group_id", required = false) Long groupId) {
        return filterByGroupId(this.postService.getAllPostsByCategory(categoryName), groupId);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/list/{categories}/posts")
    Collection<Post> getAllPostsByCategories(@PathVariable String categories,
                                             @RequestParam(value="group_id", required = false) Long groupId) {
        String[] categoriesArray = categories.split("3");
        Collection<Post> result = new HashSet<>();
        for (String category : categoriesArray) {
            result.addAll(postService.getAllPostsByCategory(category.trim()));
        }
        return filterByGroupId(result, groupId);
    }

    private Collection<Post> filterByGroupId(Collection<Post> posts, Long groupId) {
        if (groupId == null) {
            return posts;
        }
        Group group = groupService.get(groupId);
        return posts.stream()
                .filter(post -> group.getUsers().stream().anyMatch(u -> u.getId().equals(post.getAuthor().getId())))
                .collect(Collectors.toSet());
    }

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<?> createCategory(@RequestBody Category input) {
        Category created = this.categoryService.addCategory(input);
        URI location = buildLocation(created);
        return ResponseEntity.created(location).build();
    }

    private URI buildLocation(Category created) {
        return ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(created.getId()).toUri();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{categoryId}")
    Category readSingleCategory(@PathVariable Long categoryId) {
        return this.categoryService.get(categoryId);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{categoryId}")
    ResponseEntity<?> updateCategory(@PathVariable Long categoryId, @RequestBody Category input) {
        this.categoryService.updateCategory(categoryId, input.getName());
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{categoryId}")
    ResponseEntity<?> deleteCategory(@PathVariable Long categoryId) {
        this.categoryService.removeCategory(categoryId);
        return ResponseEntity.noContent().build();
    }
}
