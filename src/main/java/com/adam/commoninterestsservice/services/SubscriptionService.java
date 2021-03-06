package com.adam.commoninterestsservice.services;

import com.adam.commoninterestsservice.entities.Category;
import com.adam.commoninterestsservice.entities.Subscription;
import com.adam.commoninterestsservice.entities.User;
import com.adam.commoninterestsservice.exceptions.CategoryNotFoundException;
import com.adam.commoninterestsservice.exceptions.SubscriptionDuplicationException;
import com.adam.commoninterestsservice.repositories.CategoryRepository;
import com.adam.commoninterestsservice.repositories.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public SubscriptionService(SubscriptionRepository subscriptionRepository, CategoryRepository categoryRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.categoryRepository = categoryRepository;
    }

    public Subscription getByCategoryName(String name) {
        return this.subscriptionRepository.findByCategoryName(name)
                .orElseThrow(() -> new CategoryNotFoundException(String.format("Category with name %s not found", name)));
    }

    public void removeSubscription(Subscription subscription) {
        this.subscriptionRepository.delete(subscription);
    }

    public Subscription addSubscription(Category inputCategory, User user) {
        Subscription toSave = new Subscription();
        Category category = categoryRepository.findByName(inputCategory.getName()).orElseThrow(() -> new CategoryNotFoundException("Category " + inputCategory.getName() + " not found"));
        if (subscriptionRepository.findByCategoryAndUser(category, user).isPresent()) {
            throw new SubscriptionDuplicationException(String.format("User already subscribes to %s category", category.getName()));
        }
        toSave.setCategory(category);
        toSave.setUser(user);
        return subscriptionRepository.save(toSave);
    }

    public Collection<Category> getAllByUserId(Long userId) {
        return subscriptionRepository.findAllByUserId(userId)
                .stream()
                .map(Subscription::getCategory)
                .collect(Collectors.toList());
    }
}
