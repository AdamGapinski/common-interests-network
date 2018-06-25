package com.adam.commoninterestsservice.services;

import com.adam.commoninterestsservice.entities.Like;
import com.adam.commoninterestsservice.entities.Post;
import com.adam.commoninterestsservice.entities.User;
import com.adam.commoninterestsservice.exceptions.PostNotFoundException;
import com.adam.commoninterestsservice.repositories.LikeRepository;
import com.adam.commoninterestsservice.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;

    @Autowired
    public LikeService(LikeRepository likeRepository, PostRepository postRepository) {
        this.likeRepository = likeRepository;
        this.postRepository = postRepository;
    }

    public void removeLike(Long likeId) {
        likeRepository.deleteById(likeId);
    }

    public Collection<Like> getAllByPostId(Long postId) {
        return likeRepository.findAllByPostId(postId);
    }

    public Like addLike(Long postId, User user) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not found"));
        if (likeRepository.findByPostAndAuthor(post, user).isPresent()) {
            throw new RuntimeException("Like duplicated");
        }
        Like toSave = new Like();
        toSave.setAuthor(user);
        toSave.setPost(post);
        return likeRepository.save(toSave);
    }
}
