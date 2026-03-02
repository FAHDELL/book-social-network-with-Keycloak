package net.fahd.book_network.security;


import lombok.RequiredArgsConstructor;
import net.fahd.book_network.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//@Service
//@RequiredArgsConstructor
public class UserDetailsServiceImpl{ //implements UserDetailsService {

   /* private final UserRepository userRepository;


    @Override
    @Transactional
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + userEmail));
    }*/
}
