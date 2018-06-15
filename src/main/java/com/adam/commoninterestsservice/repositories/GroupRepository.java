package com.adam.commoninterestsservice.repositories;

import com.adam.commoninterestsservice.entities.Group;
import com.adam.commoninterestsservice.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    Collection<Group> findAllByUsersContains(User user);
}
