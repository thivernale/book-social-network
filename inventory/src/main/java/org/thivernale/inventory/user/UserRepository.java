package org.thivernale.inventory.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String username);
}
