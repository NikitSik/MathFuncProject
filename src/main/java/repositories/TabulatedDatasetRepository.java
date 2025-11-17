package repositories;

import entities.MathFunction;
import entities.TabulatedDataset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TabulatedDatasetRepository extends JpaRepository<TabulatedDataset, Long> {
    List<TabulatedDataset> findByFunction(MathFunction function);

    List<TabulatedDataset> findByFunction_Id(Long functionId);
}
