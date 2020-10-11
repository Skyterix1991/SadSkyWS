package pl.skyterix.sadsky.prediction.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.skyterix.sadsky.user.domain.UserRepository;
import pl.skyterix.sadsky.util.JpaModelMapper;

@Configuration
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class PredictionConfig {

    private final PredictionRepository predictionRepository;
    private final UserRepository userRepository;
    private final JpaModelMapper jpaModelMapper;

    @Bean
    PredictionFacade predictionFacade() {
        PredictionRepositoryPort predictionRepositoryAdapter = new PredictionRepositoryAdapter(predictionRepository);

        return new PredictionFacade(
                jpaModelMapper,
                predictionRepository,
                userRepository,
                new CreatePredictionAdapter(predictionRepositoryAdapter, jpaModelMapper),
                new DeletePredictionAdapter(predictionRepositoryAdapter),
                new UpdatePredictionAdapter(predictionRepositoryAdapter),
                new ReplacePredictionAdapter(predictionRepositoryAdapter)
        );
    }

}