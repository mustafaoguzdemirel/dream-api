package com.mustafaoguzdemirel.dream_api.repository;

import com.mustafaoguzdemirel.dream_api.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<AppUser, UUID> {

    /**
     * UserId ile kullanıcı bulma (artık id ve userId aynı şey)
     * Backward compatibility için bu metod korunuyor
     */
    @Query("SELECT u FROM AppUser u WHERE u.id = :userId")
    Optional<AppUser> findByUserId(@Param("userId") UUID userId);

    Optional<AppUser> findByGoogleId(String googleId);
}

