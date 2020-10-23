package pl.skyterix.sadsky.prediction.domain;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Skyte
 */
@Repository
@Transactional(rollbackOn = Exception.class, dontRollbackOn = RuntimeException.class)
public interface PredictionRepository extends JpaRepository<Prediction, Long> {
    @Query("from Prediction p where p.owner.userId = ?1 and p.predictionId = ?2")
    Optional<Prediction> findPredictionByUserIdAndPredictionId(UUID userId, UUID predictionId);

    boolean existsByPredictionId(UUID predictionId);

    void deleteByPredictionId(UUID predictionId);

    @Query("select p from User u join u.predictions p where u.userId = ?1 order by p.createDate desc")
    List<Prediction> findAllByUserId(UUID userId);

    Optional<Prediction> findPredictionByPredictionId(UUID predictionId);

    @Query("from Prediction where expireDate >= ?1 and depressionResult is null and canceled = false")
    List<Prediction> findAllPotentiallyExpired(LocalDate today);
}
