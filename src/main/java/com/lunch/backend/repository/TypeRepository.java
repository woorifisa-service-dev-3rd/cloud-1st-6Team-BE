package com.lunch.backend.repository;

import com.lunch.backend.domain.SocialType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TypeRepository extends JpaRepository<SocialType, Long> {
    SocialType findByProvider(String provider);
}
