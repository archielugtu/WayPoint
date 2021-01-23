package com.springvuegradle.team200.jwt;

import com.springvuegradle.team200.model.User;
import com.springvuegradle.team200.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementation of UserDetailsService that queries the main userRepository
 * Required for Jwt spring security implementation
 */
@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    /**
     * Returns the user with the given EMAIL address
     *
     * @param username username to be queried
     * @return user with that username
     * @throws UsernameNotFoundException Thrown if an {@link UserDetailsService} implementation cannot locate a
     *                                   {@link org.springframework.security.core.userdetails.User} by its username.
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.getUserFromUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Could not find a user with that name");
        }
        return user;
    }
}
