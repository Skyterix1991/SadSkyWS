package pl.skyterix.sadsky.prediction.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import pl.skyterix.sadsky.exception.DayDeadlineException;
import pl.skyterix.sadsky.exception.PredictionIsExpiredException;
import pl.skyterix.sadsky.exception.RecordNotFoundException;
import pl.skyterix.sadsky.prediction.domain.day.domain.Day;
import pl.skyterix.sadsky.prediction.domain.day.domain.Emotion;
import pl.skyterix.sadsky.prediction.domain.day.domain.dto.DayDTO;
import pl.skyterix.sadsky.prediction.domain.dto.PredictionDTO;
import pl.skyterix.sadsky.user.domain.UserRepository;
import pl.skyterix.sadsky.user.domain.dto.UserDTO;
import pl.skyterix.sadsky.util.JpaModelMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static pl.skyterix.sadsky.prediction.domain.Prediction.EXPIRE_DAYS;

@SpringBootTest
class PredictionFacadeTest {

    @Autowired
    private JpaModelMapper jpaModelMapper;

    @Autowired
    private PredictionFacade predictionFacade;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PredictionRepository predictionRepository;

    private PredictionDTO predictionDTO;
    private Prediction prediction;

    @BeforeEach
    void beforeEach() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("dsadsa@dsadas.pl");
        userDTO.setWakeHour(LocalDateTime.now().getHour());

        List<DayDTO> days = IntStream.range(1, EXPIRE_DAYS + 1)
                .mapToObj(Day::new)
                .map(day -> jpaModelMapper.mapEntity(day, DayDTO.class))
                .collect(Collectors.toList());

        predictionDTO = new PredictionDTO();
        predictionDTO.setPredictionId(UUID.randomUUID());
        predictionDTO.setOwner(userDTO);
        predictionDTO.setExpireDate(LocalDate.now().plusDays(EXPIRE_DAYS));
        predictionDTO.setDays(days);

        userDTO.setPredictions(Collections.singletonList(predictionDTO));

        prediction = jpaModelMapper.mapEntity(predictionDTO, Prediction.class);
    }

    @Test
    @DisplayName("Get full user predictions")
    void givenPredictionId_whenGetFullUserPredictions_thenReturnValidSetOfUserPredictions() {
        // Given
        // When
        when(userRepository.existsByUserId(any())).thenReturn(true);
        when(predictionRepository.findAllByUserId(any())).thenReturn(Collections.singletonList(prediction));
        // Then
        List<PredictionDTO> predictions = predictionFacade.getFullUserPredictions(UUID.randomUUID());

        assertAll(() -> {
            assertEquals(
                    1,
                    predictions.size(),
                    "Received set length is not the same as original.");
            assertNotNull(predictions.iterator().next().getOwner().getEmail(), "Sensitive data is not exposed.");
        });
    }

    @Test
    @DisplayName("Get full user predictions by non existing uuid")
    void givenNonExistingPredictionId_whenGetFullUserPredictions_thenReturnValidSetOfUserPredictions() {
        // Given
        // When
        when(userRepository.existsByUserId(any())).thenReturn(false);
        // Then
        assertThrows(RecordNotFoundException.class, () -> predictionFacade.getFullUserPredictions(UUID.randomUUID()), "Exception was not thrown.");
    }

    @Test
    @DisplayName("Get mini user predictions")
    void givenUserId_whenGetMiniUserPredictions_thenReturnValidSetOfUserPredictions() {
        // Given
        // When
        when(userRepository.existsByUserId(any())).thenReturn(true);
        when(predictionRepository.findAllByUserId(any())).thenReturn(Collections.singletonList(prediction));
        // Then
        List<PredictionDTO> predictions = predictionFacade.getMiniUserPredictions(UUID.randomUUID());

        assertAll(() -> {
            assertEquals(
                    1,
                    predictions.size(),
                    "Received set length is not the same as original.");
            assertNull(predictions.iterator().next().getOwner().getEmail(), "Sensitive data is not exposed.");
        });
    }

    @Test
    @DisplayName("Get mini user predictions by non existing uuid")
    void givenNonExistingUserId_whenGetMiniUserPredictions_thenReturnValidSetOfUserPredictions() {
        // Given
        // When
        when(userRepository.existsByUserId(any())).thenReturn(false);
        // Then
        assertThrows(RecordNotFoundException.class, () -> predictionFacade.getMiniUserPredictions(UUID.randomUUID()), "Exception was not thrown.");
    }

    @Test
    @DisplayName("Get full user prediction")
    void givenPredictionIdAndUserId_whenGetFullUserPrediction_thenReturnValidPrediction() {
        // Given
        // When
        when(predictionRepository.findPredictionByUserIdAndPredictionId(any(), any())).thenReturn(Optional.of(prediction));
        // Then
        PredictionDTO prediction = predictionFacade.getFullUserPrediction(UUID.randomUUID(), UUID.randomUUID());

        assertNotNull(prediction.getOwner().getEmail(), "Sensitive data is not exposed.");
    }

    @Test
    @DisplayName("Get full user prediction by non existing uuid")
    void givenNonExistingPredictionIdAndNonExistingUserId_whenGetFullUserPrediction_thenThrowRecordNotFoundException() {
        // Given
        // When
        when(predictionRepository.findPredictionByUserIdAndPredictionId(any(), any())).thenReturn(Optional.empty());
        // Then
        assertThrows(RecordNotFoundException.class, () -> predictionFacade.getFullUserPrediction(UUID.randomUUID(), UUID.randomUUID()), "Exception was not thrown.");
    }

    @Test
    @DisplayName("Get mini user prediction")
    void givenPredictionIdAndUserId_whenGetMiniUserPrediction_thenReturnValidPrediction() {
        // Given
        // When
        when(predictionRepository.findPredictionByUserIdAndPredictionId(any(), any())).thenReturn(Optional.of(prediction));
        // Then
        PredictionDTO prediction = predictionFacade.getMiniUserPrediction(UUID.randomUUID(), UUID.randomUUID());

        assertNull(prediction.getOwner().getEmail(), "Sensitive data is exposed.");
    }

    @Test
    @DisplayName("Get mini user prediction by non existing uuid")
    void givenNonExistingPredictionIdAndNonExistingUserId_whenGetMiniUserPrediction_thenThrowRecordNotFoundException() {
        // Given
        // When
        when(predictionRepository.findPredictionByUserIdAndPredictionId(any(), any())).thenReturn(Optional.empty());
        // Then
        assertThrows(RecordNotFoundException.class, () -> predictionFacade.getMiniUserPrediction(UUID.randomUUID(), UUID.randomUUID()), "Exception was not thrown.");
    }

    @Test
    @DisplayName("Generate prediction result")
    void givenUserIdAndPredictionId_whenGeneratePredictionResult_thenRunSuccessfully() {
        // Given
        prediction.setExpireDate(LocalDate.now().minusDays(1));
        // When
        when(predictionRepository.findPredictionByUserIdAndPredictionId(any(), any())).thenReturn(Optional.of(prediction));
        // Then
        assertDoesNotThrow(() -> predictionFacade.generatePredictionResult(UUID.randomUUID(), UUID.randomUUID()), "Exception was thrown.");
    }

    @Test
    @DisplayName("Set prediction day emotions")
    void givenUserIdAndPredictionId_whenSetPredictionDayEmotions_thenRunSuccessfully() {
        // Given
        // When
        when(predictionRepository.findPredictionByUserIdAndPredictionId(any(), any()))
                .thenReturn(Optional.of(prediction));
        // Then
        assertDoesNotThrow(() ->
                        predictionFacade.setPredictionDayEmotions(UUID.randomUUID(), UUID.randomUUID(), Collections.singleton(Emotion.ACTIVE)),
                "Exception was thrown");
    }

    @Test
    @DisplayName("Set expired prediction day emotions")
    void givenUserIdAndExpiredPredictionId_whenSetPredictionDayEmotions_thenThrowPredictionExpiredException() {
        // Given
        prediction.setExpireDate(LocalDate.now());
        // When
        when(predictionRepository.findPredictionByUserIdAndPredictionId(any(), any()))
                .thenReturn(Optional.of(prediction));
        // Then
        assertThrows(PredictionIsExpiredException.class, () ->
                        predictionFacade.setPredictionDayEmotions(UUID.randomUUID(), UUID.randomUUID(), Collections.singleton(Emotion.ACTIVE)),
                "Exception was not thrown");
    }

    @Test
    @DisplayName("Set prediction day emotions by non existing prediction UUID")
    void givenUserIdAndNonExistingPredictionId_whenSetPredictionDayEmotions_thenThrowRecordNotFoundException() {
        // Given
        // When
        when(predictionRepository.findPredictionByUserIdAndPredictionId(any(), any()))
                .thenReturn(Optional.empty());
        // Then
        assertThrows(RecordNotFoundException.class, () ->
                        predictionFacade.setPredictionDayEmotions(UUID.randomUUID(), UUID.randomUUID(), Collections.singleton(Emotion.ACTIVE)),
                "Exception was not thrown");
    }

    @Test
    @DisplayName("Set prediction day emotions outside of any deadline")
    void givenUserIdAndPredictionIdOutsideOfAnyDeadlines_whenSetPredictionDayEmotions_thenThrowDayDeadlineException() {
        // Given
        prediction.getOwner().setWakeHour(LocalDateTime.now().getHour() + 1);
        // When
        when(predictionRepository.findPredictionByUserIdAndPredictionId(any(), any()))
                .thenReturn(Optional.of(prediction));
        // Then
        assertThrows(DayDeadlineException.class, () ->
                        predictionFacade.setPredictionDayEmotions(UUID.randomUUID(), UUID.randomUUID(), Collections.singleton(Emotion.ACTIVE)),
                "Exception was not thrown");
    }
}