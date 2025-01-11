package deprecated.user;

import java.util.Optional;

//@Repository
public interface TokenRepository /*extends JpaRepository<Token, Long>*/ {
    Optional<Token> findByToken(String token);
}
