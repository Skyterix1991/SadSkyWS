package pl.skyterix.sadsky.user.domain.prediction.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.skyterix.sadsky.user.domain.UserFacade;
import pl.skyterix.sadsky.util.JpaModelMapper;

@Configuration
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class PredictionConfig {

    private final UserFacade userFacade;
    private final PredictionRepository predictionRepository;
    private final JpaModelMapper jpaModelMapper;

    @Bean
    PredictionFacade predictionFacade() {
        PredictionRepositoryPort predictionRepositoryAdapter = new PredictionRepositoryAdapter(predictionRepository);

        return new PredictionFacade(
                jpaModelMapper,
                predictionRepository,
                new CreatePredictionAdapter(predictionRepositoryAdapter, jpaModelMapper),
                new DeletePredictionAdapter(predictionRepositoryAdapter),
                new UpdatePredictionAdapter(predictionRepositoryAdapter),
                new ReplacePredictionAdapter(predictionRepositoryAdapter)
        );
    }

}