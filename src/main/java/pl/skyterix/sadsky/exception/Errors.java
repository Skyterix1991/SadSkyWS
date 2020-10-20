package pl.skyterix.sadsky.exception;

import lombok.RequiredArgsConstructor;

import java.text.MessageFormat;

/**
 * Class contains error messages enum which can be accessed using getErrorMessage().
 * Messages can contain parenthesis which can be filled using ex. Your id is {0}! and use method getErrorMessage(3);
 * Messages also can contain multiple parenthesis all can be filled using ex. Your id is {0} and your name is {1}! and use method getErrorMessage(new String[]{"3", "John"})
 * It would be nice to also indicate what value was filled for ex. [uuid={0}].
 */
@RequiredArgsConstructor
public enum Errors {

    NO_RECORD_FOUND("Wynik [uuid={0}] nie został znaleziony."),
    RECORD_ALREADY_EXISTS("Wynik [uuid={0}] już istnieje."),
    UNAUTHORIZED_GROUP("Twoja grupa [group={0}] nie posiada uprawnień aby wykonać tą akcję."),
    GROUP_NOT_FOUND("Grupa [group={0}] nie została znaleziona."),
    SORT_NOT_ALLOWED_ON_FIELD("Sortowanie po polu [field={0}] jest niedozwolone."),
    AGE_NOT_MEETING_REQUIRED("Twój wiek musi być pomiędzy 16 lat a 100 lat."),
    PREDICTION_RESULT_NOT_READY_TO_GENERATE("Resultat nie jest jeszcze możliwy do wygenerowania."),
    PREDICTION_RESULT_IS_ALREADY_GENERATED("Rezultat jest już wygenerowany."),
    PREDICTION_IS_EXPIRED("Wynik [uuid={0}] wygasł."),
    DAY_DEADLINE_EXCEPTION("Ostatni termin zmiany emocji na dzisiaj wygasł. Nie można ustawić emocji."),
    BAD_REQUEST("Przekazane wartości są niepoprawne."),
    UNIDENTIFIED("Wystąpił nieokreślony błąd."),
    TARGET_RECORD_IS_THE_SAME_AS_SOURCE("Docelowy rekord jest ten sam to źródłowy.");

    private final String errorMessage;

    public String getErrorMessage(Object[] data) {
        return MessageFormat.format(errorMessage, data);
    }

    public String getErrorMessage(Object data) {
        return MessageFormat.format(errorMessage, data);
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}