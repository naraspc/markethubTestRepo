package org.hanghae.markethub.domain.user.repository;

import org.hanghae.markethub.domain.store.entity.Store;
import org.hanghae.markethub.domain.user.entity.User;
import org.hanghae.markethub.global.constant.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT p FROM User p WHERE p.email = :email AND p.status = 'EXIST'")
    Optional<User> findByEmail(String email);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.id = :id AND u.status = 'EXIST'")
    boolean existsById(@Param("id") Long id);


    boolean existsByEmail(String email);

    @Query("SELECT p FROM User p WHERE p.id = :id AND p.status = 'EXIST'")
    Optional<User> findById(Long id);
}



