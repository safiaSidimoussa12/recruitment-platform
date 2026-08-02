package com.jobboard.jobboard.module.auth;

import com.jobboard.jobboard.module.candidat.CandidatRepository;
import com.jobboard.jobboard.module.recruteur.RecruteurRepository;
import com.jobboard.jobboard.shared.domain.Utilisateur;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final CandidatRepository candidatRepository;
    private final RecruteurRepository recruteurRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Utilisateur utilisateur = candidatRepository.findByEmail(email)
            .map(c -> (Utilisateur) c)
            .orElseGet(() -> recruteurRepository.findByEmail(email)
                .map(r -> (Utilisateur) r)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable : " + email)));

        return new User(
            utilisateur.getEmail(),
            utilisateur.getMotDePasse(),
            List.of(new SimpleGrantedAuthority("ROLE_" + utilisateur.getRole().name()))
        );
    }
}