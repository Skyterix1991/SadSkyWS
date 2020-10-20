package pl.skyterix.sadsky.util;

import lombok.RequiredArgsConstructor;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.Filter;

@Configuration
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UtilsConfig {

    @PersistenceContext
    private EntityManager entityManager;

    @Bean
    public JpaModelMapper jpaModelMapper() {
        return new JpaModelMapper(entityManager, modelMapper());
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setPropertyCondition(Conditions.isNotNull());

        return modelMapper;
    }

    @Bean
    public Filter etag() {
        return new ShallowEtagHeaderFilter();
    }

    @Bean
    public SortBlacklistUtil sortBlacklistUtil() {
        return new SortBlacklistUtil();
    }
}
