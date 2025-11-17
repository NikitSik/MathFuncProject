package repositories;

import entities.FunctionComponent;
import entities.FunctionComponentId;
import entities.MathFunction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FunctionComponentRepository extends JpaRepository<FunctionComponent, FunctionComponentId> {
    List<FunctionComponent> findByComposite(MathFunction composite);
}
