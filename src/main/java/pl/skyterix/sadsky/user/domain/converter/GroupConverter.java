package pl.skyterix.sadsky.user.domain.converter;

import pl.skyterix.sadsky.user.domain.group.strategy.GroupStrategy;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Skyte
 */
@Converter(autoApply = true)
public class GroupConverter implements AttributeConverter<GroupStrategy, String> {

    private final String GROUP_STRATEGY_PACKAGE_LOCATION = GroupStrategy.class.getPackage().getName();

    /**
     * Serializes group strategy.
     *
     * @param groupStrategy Strategy class to convert.
     * @return Simple name of the given class.
     */
    @Override
    public String convertToDatabaseColumn(GroupStrategy groupStrategy) {
        if (groupStrategy == null) {
            return null;
        }

        return groupStrategy.getClass().getSimpleName();
    }


    /**
     * Deserializes group strategy.
     *
     * @param className Simple class name in string.
     * @return Class with given name in package strategy.
     */
    @Override
    public GroupStrategy convertToEntityAttribute(String className) {
        GroupStrategy groupStrategy;

        try {
            groupStrategy = (GroupStrategy) Class.forName(GROUP_STRATEGY_PACKAGE_LOCATION + "." + className).getConstructor().newInstance();
        } catch (NoSuchMethodException | ClassNotFoundException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            return null;
        }

        return groupStrategy;
    }
}
