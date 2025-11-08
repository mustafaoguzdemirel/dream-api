package com.mustafaoguzdemirel.dream_api.repository;

import com.mustafaoguzdemirel.dream_api.entity.AppUser;
import com.mustafaoguzdemirel.dream_api.entity.Dream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DreamRepository extends JpaRepository<Dream, UUID> {
    List<Dream> findByUser(AppUser user);

    List<Dream> findTop5ByUserOrderByCreatedAtDesc(AppUser user);

    List<Dream> findByUserOrderByCreatedAtDesc(AppUser user);
    List<Dream> findTop3ByUserOrderByCreatedAtDesc(AppUser user);

    void deleteAllByUser(AppUser user);

}
