package com.jobboard.jobboard.module.auth;

import com.jobboard.jobboard.shared.domain.StatutCompte;
import com.jobboard.jobboard.shared.domain.Utilisateur;
import com.jobboard.jobboard.shared.domain.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.DisabledException;
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

    private final UtilisateurRepository utilisateurRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        boolean enabled = utilisateur.getStatut() == StatutCompte.ACTIF;
        boolean suspended = utilisateur.getStatut() == StatutCompte.SUSPENDU;
        boolean pending = utilisateur.getStatut() == StatutCompte.EN_ATTENTE;

        return User.builder()
                .username(utilisateur.getEmail())
                .password(utilisateur.getMotDePasse())
                .authorities("ROLE_" + utilisateur.getRole().name())
                .accountExpired(false)
                .accountLocked(suspended)
                .credentialsExpired(pending)
                .disabled(!enabled && !suspended && !pending ? true : false)
                .build();
    }
}