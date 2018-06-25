package com.adam.commoninterestsservice.repositories;

import com.adam.commoninterestsservice.entities.Like;
import com.adam.commoninterestsservice.entities.Post;
import com.adam.commoninterestsservice.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Collection<Like> findAllByPostId(Long postId);
    Optional<Like> findByPostAndAuthor(Post post, User author);
}
