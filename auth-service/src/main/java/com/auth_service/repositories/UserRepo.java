package com.auth_service.repositories;

import com.auth_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestParam;

public interface UserRepo extends JpaRepository<User, String> {

    boolean existsByEmail(String email);

    boolean existsByMobile(String mobile);

//    @Query(value = "SELECT id,role FROM User WHERE mobile =?1", nativeQuery = true)
//    User findUserIdAndRole(String mobile);

    User findByMobile(String mobile);

    @Query(value = "SELECT name FROM user WHERE id = :doctorId", nativeQuery = true)
    String getNameById(@Param("doctorId") String doctorId);
}
