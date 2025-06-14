package com.skhuring.mentoring.repository;

import com.skhuring.mentoring.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    public Optional<User> findBySocialId(String socialId);
    public Optional<User> findByName(String sender);
    @Query("SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:email)")
    public Optional<User> findByEmail(String email);
}
