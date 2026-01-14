package com.bank.star.entity;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.LocalDateTime;

/**
 * EntityListener для автоматического обновления временных меток
 */
public class DynamicRuleEntityListener {

    @PrePersist
    public void prePersist(DynamicRuleEntity entity) {
        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
    }

    @PreUpdate
    public void preUpdate(DynamicRuleEntity entity) {
        entity.setUpdatedAt(LocalDateTime.now());
    }
}