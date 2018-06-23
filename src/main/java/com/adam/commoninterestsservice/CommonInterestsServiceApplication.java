package com.adam.commoninterestsservice;

import com.adam.commoninterestsservice.services.CategoryService;
import com.adam.commoninterestsservice.services.PostService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CommonInterestsServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CommonInterestsServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner init(PostService postService, CategoryService categoryService) {
        return args -> {
//            postService.addPost("Hello everyone");
//            postService.addPost("Welcome");
//            postService.addPost("Another post");
//            categoryService.addCategory("programming");
//            categoryService.addCategory("sport");
//            categoryService.addCategory("football");
        };
    }
}
