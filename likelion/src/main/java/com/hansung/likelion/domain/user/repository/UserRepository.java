package com.hansung.likelion.domain.user.repository;

import com.hansung.likelion.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    default User getUserById(Long id){
        return findById(id).orElseThrow(null);
    }
}
