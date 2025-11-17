package repositories;

import entities.DatasetPoint;
import entities.DatasetPointId;
import entities.TabulatedDataset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DatasetPointRepository extends JpaRepository<DatasetPoint, DatasetPointId> {
    List<DatasetPoint> findByDataset(TabulatedDataset dataset);

    List<DatasetPoint> findByDataset_Id(Long datasetId);
}
