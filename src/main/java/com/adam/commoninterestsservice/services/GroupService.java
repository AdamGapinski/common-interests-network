package com.adam.commoninterestsservice.services;

import com.adam.commoninterestsservice.entities.Group;
import com.adam.commoninterestsservice.entities.User;
import com.adam.commoninterestsservice.exceptions.GroupJoinDuplicationException;
import com.adam.commoninterestsservice.exceptions.GroupNotFoundException;
import com.adam.commoninterestsservice.repositories.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class GroupService {

    private final GroupRepository groupRepository;

    @Autowired
    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public Collection<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    public Collection<Group> getAllGroupsByUserId(User user) {
        return groupRepository.findAllByUsersContains(user);
    }

    public Group addGroup(Group input, User creator) {
        Group toSave = new Group();
        toSave.setCreator(creator);
        toSave.setName(input.getName());
        toSave.setDescription(input.getDescription());
        toSave.setUsers(Collections.singleton(creator));
        return groupRepository.save(toSave);
    }

    public Group get(Long groupId) {
        return groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException("Group not found"));
    }

    public void joinGroup(Group input, User user) {
        Group toJoin = groupRepository.findByName(input.getName())
                .orElseThrow(() -> new GroupNotFoundException(String.format("Group %s not found", input.getName())));
        if (toJoin.getUsers().stream().anyMatch(u -> u.getId().equals(user.getId()))) {
            throw new GroupJoinDuplicationException("User have already joined this group");
        }
        toJoin.getUsers().add(user);
        groupRepository.save(toJoin);
    }

    public void updateGroup(Long groupId, Group input) {
        Group toSave = groupRepository.findById(groupId).orElseThrow(() -> new RuntimeException("Group not found"));
        toSave.setName(input.getName());
        toSave.setDescription(input.getDescription());
    }

    public void removeGroup(Long groupId) {
        groupRepository.delete(groupRepository.findById(groupId).orElseThrow(() -> new RuntimeException("Group not found")));
    }
}
