package pl.skyterix.sadsky.user.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import pl.skyterix.sadsky.util.JpaModelMapper;

@Configuration
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserConfig {

    private final Environment environment;
    private final UserRepository userRepository;
    private final JpaModelMapper jpaModelMapper;

    @Bean
    UserFacade userFacade() {
        UserRepositoryPort userRepositoryAdapter = new UserRepositoryAdapter(userRepository);
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        return new UserFacade(
                environment,
                jpaModelMapper,
                userRepository,
                new CreateUserAdapter(userRepositoryAdapter, jpaModelMapper, bCryptPasswordEncoder),
                new DeleteUserAdapter(userRepositoryAdapter),
                new UpdateUserAdapter(userRepositoryAdapter, bCryptPasswordEncoder),
                new ReplaceUserAdapter(userRepositoryAdapter, bCryptPasswordEncoder),
                new SetUserGroupAdapter(userRepositoryAdapter)
        );
    }

}