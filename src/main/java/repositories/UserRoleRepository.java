package repositories;

import entities.Role;
import entities.User;
import entities.UserRole;
import entities.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {
    List<UserRole> findByUser(User user);

    List<UserRole> findByRole(Role role);
}
