package pl.skyterix.sadsky.user.domain.prediction.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import pl.skyterix.sadsky.exception.RecordNotFoundException;
import pl.skyterix.sadsky.user.domain.UserRepository;
import pl.skyterix.sadsky.user.domain.dto.UserDTO;
import pl.skyterix.sadsky.user.domain.prediction.domain.dto.PredictionDTO;
import pl.skyterix.sadsky.util.JpaModelMapper;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

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

        predictionDTO = new PredictionDTO();
        predictionDTO.setPredictionId(UUID.randomUUID());
        predictionDTO.setOwner(userDTO);

        prediction = jpaModelMapper.mapEntity(predictionDTO, Prediction.class);
    }

    @Test
    @DisplayName("Create prediction")
    void givenPredictionDTO_whenCreatePrediction_thenReturnPredictionId() {
        // Given
        // When
        when(predictionRepository.save(any())).thenReturn(prediction);
        // Then
        assertEquals(predictionDTO.getPredictionId(), predictionFacade.createPrediction(predictionDTO), "Returned uuid is valid.");
    }

    @Test
    @DisplayName("Delete prediction")
    void givenPredictionId_whenDeletePrediction_thenRunSuccessfully() {
        // Given
        // When
        when(predictionRepository.existsByPredictionId(any())).thenReturn(true);
        // Then
        assertDoesNotThrow(() -> predictionFacade.deletePrediction(UUID.randomUUID()), "Exception was thrown.");
    }

    @Test
    @DisplayName("Delete prediction with non existing uuid")
    void givenNonExistingPredictionId_whenDeletePrediction_thenRunSuccessfully() {
        // Given
        // When
        when(predictionRepository.existsByPredictionId(any())).thenReturn(false);
        // Then
        assertThrows(RecordNotFoundException.class, () -> predictionFacade.deletePrediction(UUID.randomUUID()), "Exception was not thrown.");
    }

    @Test
    @DisplayName("Get full user predictions")
    void givenPredictionId_whenGetFullUserPredictions_thenReturnValidSetOfUserPredictions() {
        // Given
        // When
        when(userRepository.existsByUserId(any())).thenReturn(true);
        when(predictionRepository.findAllByUserId(any())).thenReturn(Collections.singleton(prediction));
        // Then
        Set<PredictionDTO> predictions = predictionFacade.getFullUserPredictions(UUID.randomUUID());

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
        when(predictionRepository.findAllByUserId(any())).thenReturn(Collections.singleton(prediction));
        // Then
        Set<PredictionDTO> predictions = predictionFacade.getMiniUserPredictions(UUID.randomUUID());

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
    @DisplayName("Update prediction")
    void givenPredictionIdAndPredictionDTO_whenUpdatePrediction_thenRunSuccessfully() {
        // Given
        // When
        when(predictionRepository.findPredictionByPredictionId(any())).thenReturn(Optional.of(prediction));
        // Then
        assertDoesNotThrow(() -> predictionFacade.updatePrediction(UUID.randomUUID(), predictionDTO), "Exception was thrown.");
    }

    @Test
    @DisplayName("Update prediction by non existing uuid")
    void givenNonExistingPredictionIdAndPredictionDTO_whenUpdatePrediction_thenThrowRecordNotFoundException() {
        // Given
        // When
        when(predictionRepository.findPredictionByPredictionId(any())).thenReturn(Optional.empty());
        // Then
        assertThrows(RecordNotFoundException.class, () -> predictionFacade.updatePrediction(UUID.randomUUID(), predictionDTO), "Exception was not thrown.");
    }

    @Test
    @DisplayName("Replace prediction")
    void givenPredictionIdAndPredictionDTO_whenReplacePrediction_thenRunSuccessfully() {
        // Given
        // When
        when(predictionRepository.findPredictionByPredictionId(any())).thenReturn(Optional.of(prediction));
        // Then
        assertDoesNotThrow(() -> predictionFacade.replacePrediction(UUID.randomUUID(), predictionDTO), "Exception was thrown.");
    }

    @Test
    @DisplayName("Replace prediction by non existing uuid")
    void givenNonExistingPredictionIdAndPredictionDTO_whenReplacePrediction_thenThrowRecordNotFoundException() {
        // Given
        // When
        when(predictionRepository.findPredictionByPredictionId(any())).thenReturn(Optional.empty());
        // Then
        assertThrows(RecordNotFoundException.class, () -> predictionFacade.replacePrediction(UUID.randomUUID(), predictionDTO), "Exception was not thrown.");
    }
}