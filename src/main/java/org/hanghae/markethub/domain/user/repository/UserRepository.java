package org.hanghae.markethub.domain.user.repository;

import org.hanghae.markethub.domain.store.entity.Store;
import org.hanghae.markethub.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT p FROM User p WHERE p.id = :id AND p.status = 'EXIST'")
    Optional<User> findById(Long id);

    @Query("SELECT p FROM User p WHERE p.email = :email AND p.status = 'EXIST'")
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

}

