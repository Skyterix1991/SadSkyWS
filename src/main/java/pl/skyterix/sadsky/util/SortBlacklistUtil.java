package pl.skyterix.sadsky.util;

import pl.skyterix.sadsky.util.annotation.SortBlacklisted;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Util preventing fields being sorted by.
 *
 * @author Skyte
 */
public class SortBlacklistUtil {

    /**
     * Looks for fields annotated with @SortBlacklisted in the specified class
     * and creates blacklist form their names.
     *
     * @param classToLookIn Class to search in for @SortBlacklisted annotation.
     * @param <T>           Class object type.
     * @return List of blacklisted fields names for the specified class.
     */
    public <T> ArrayList<String> getBlackListedFields(Class<T> classToLookIn) {
        ArrayList<String> blackListedFieldsNames = new ArrayList<>();

        for (Field field : classToLookIn.getDeclaredFields()) {
            SortBlacklisted sortBlacklisted = field.getAnnotation(SortBlacklisted.class);

            if (sortBlacklisted != null) {
                blackListedFieldsNames.add(field.getName());
            }
        }

        return blackListedFieldsNames;
    }
}