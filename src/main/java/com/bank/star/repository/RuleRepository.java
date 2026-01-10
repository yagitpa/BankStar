package com.bank.star.repository;

import com.bank.star.entity.DynamicRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы с динамическими правилами
 */
@Repository
public interface RuleRepository extends JpaRepository<DynamicRuleEntity, Long> {

    Optional<DynamicRuleEntity> findByProductId(UUID productId);

    void deleteByProductId(UUID productId);
}
