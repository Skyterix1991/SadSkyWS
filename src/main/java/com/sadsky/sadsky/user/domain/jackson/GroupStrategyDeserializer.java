package com.sadsky.sadsky.user.domain.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.sadsky.sadsky.exception.Errors;
import com.sadsky.sadsky.exception.GroupNotFoundException;
import com.sadsky.sadsky.user.domain.converter.GroupConverter;
import com.sadsky.sadsky.user.domain.group.strategy.GroupStrategy;

import java.io.IOException;

public class GroupStrategyDeserializer extends StdDeserializer<GroupStrategy> {

    public GroupStrategyDeserializer() {
        super(GroupStrategy.class);
    }

    private GroupStrategyDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public GroupStrategy deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode jsonNode = jsonParser.getCodec().readTree(jsonParser);

        String strategyString = jsonNode.get("group").asText();

        GroupStrategy strategy = new GroupConverter().convertToEntityAttribute(strategyString);

        if (strategy == null)
            throw new GroupNotFoundException(Errors.GROUP_NOT_FOUND.getErrorMessage(strategyString));

        return strategy;
    }
}
