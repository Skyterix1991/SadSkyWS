package pl.skyterix.sadsky.prediction.domain;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
@Transactional
interface PredictionRepository extends JpaRepository<Prediction, Long> {
    @Query("from Prediction p where p.owner.userId = ?1 and p.predictionId = ?2")
    Optional<Prediction> findPredictionByUserIdAndPredictionId(UUID userId, UUID predictionId);

    boolean existsByPredictionId(UUID predictionId);

    void deleteByPredictionId(UUID predictionId);

    @Query("from Prediction p where p.owner.userId = ?1 order by p.createDate desc")
    Set<Prediction> findAllByUserId(UUID userId);

    Optional<Prediction> findPredictionByPredictionId(UUID predictionId);
}
