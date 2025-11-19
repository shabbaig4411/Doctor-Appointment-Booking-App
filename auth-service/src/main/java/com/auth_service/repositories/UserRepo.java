package com.auth_service.repositories;

import com.auth_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepo extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    boolean existsByMobile(String mobile);

//    @Query(value = "SELECT id,role FROM User WHERE mobile =?1", nativeQuery = true)
//    User findUserIdAndRole(String mobile);

    User findByMobile(String mobile);
}
