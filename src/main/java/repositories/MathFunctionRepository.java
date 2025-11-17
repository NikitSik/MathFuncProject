package repositories;

import entities.MathFunction;
import entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MathFunctionRepository extends JpaRepository<MathFunction, Long> {
    List<MathFunction> findByOwner(User owner);

    Optional<MathFunction> findByOwnerAndName(User owner, String name);
}
