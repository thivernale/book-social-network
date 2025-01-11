package deprecated.role;

import java.util.Optional;

//@Repository
public interface RoleRepository /*extends JpaRepository<Role, Long>*/ {
    Optional<Role> findByName(String name);
}
