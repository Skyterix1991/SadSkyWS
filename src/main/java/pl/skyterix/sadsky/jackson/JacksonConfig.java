package pl.skyterix.sadsky.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

// Fix for JavaScript date parse problem
@Configuration
class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper serializingObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateSerializer());
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateDeserializer());

        objectMapper.registerModule(javaTimeModule);

        return objectMapper;
    }

    public static class LocalDateSerializer extends JsonSerializer<LocalDateTime> {
        @Override
        public void serialize(LocalDateTime localDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeNumber(localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
    }

    public static class LocalDateDeserializer extends JsonDeserializer<LocalDateTime> {
        @Override
        public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(jsonParser.getValueAsLong()), ZoneId.systemDefault());
        }
    }
}