package deprecated.user;

import java.util.Optional;

//@Repository
public interface UserRepository /*extends JpaRepository<User, Long>*/ {
    Optional<User> findByEmail(String email);
}
