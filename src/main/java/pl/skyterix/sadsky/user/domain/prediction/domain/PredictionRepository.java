package pl.skyterix.sadsky.user.domain.prediction.domain;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional
interface PredictionRepository extends JpaRepository<Prediction, Long>, QuerydslPredicateExecutor<Prediction> {
    Optional<Prediction> findPredictionByPredictionId(UUID predictionId);

    boolean existsByPredictionId(UUID predictionId);

    void deleteByPredictionId(UUID predictionId);
}
