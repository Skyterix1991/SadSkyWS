package pl.skyterix.sadsky.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.skyterix.sadsky.user.domain.User;
import pl.skyterix.sadsky.user.domain.UserRepository;

import java.util.Collections;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));

        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_USER");

        return new org.springframework.security.core.userdetails.User(user.getUserId().toString(), user.getEncryptedPassword(), Collections.singletonList(grantedAuthority));
    }
}
