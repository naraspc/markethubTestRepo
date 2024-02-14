package org.hanghae.markethub.domain.user.repository;

import org.hanghae.markethub.domain.user.entity.User;
import org.hanghae.markethub.global.constant.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.id = :id AND u.status = 'EXIST'")
    boolean existsById(@Param("id") Long id);


    boolean existsByEmail(String email);
    User findByEmail(String email);

}

