package kz.kbtu.dang.dang.repositories;

import jakarta.transaction.Transactional;
import kz.kbtu.dang.dang.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);
}
