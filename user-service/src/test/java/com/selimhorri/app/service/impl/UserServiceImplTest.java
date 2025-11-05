package com.selimhorri.app.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.selimhorri.app.repository.UserRepository;
import com.selimhorri.app.exception.wrapper.UserObjectNotFoundException;
import com.selimhorri.app.dto.CredentialDto;
import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.domain.User;
import com.selimhorri.app.helper.UserMappingHelper;
import com.selimhorri.app.domain.Credential;
import com.selimhorri.app.dto.CredentialDto;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testFindById_returnsUser() {
        User user = new User(1, "John", "Doe", null, "john@mail.com", "123", null, null);

        Credential credential = Credential.builder()
            .credentialId(1)
            .username("john")
            .password("123")
            .isEnabled(true)
            .isAccountNonExpired(true)
            .isAccountNonLocked(true)
            .isCredentialsNonExpired(true)
            .build();

        credential.setUser(user);
        user.setCredential(credential);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        UserDto result = userService.findById(1);

        assertEquals("John", result.getFirstName());
        verify(userRepository).findById(1);
    }

    @Test
    void testFindById_throwsExceptionWhenNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(UserObjectNotFoundException.class, () -> userService.findById(1));
    }

    @Test
    void testSave_createsUser() {
        // crear CredentialDto y UserDto usando el builder (orden claro)
        CredentialDto credDto = new CredentialDto();
        credDto.setCredentialId(1);
        credDto.setUsername("john");
        credDto.setPassword("123");

        // usar builder para evitar confusiones de orden en el constructor
        UserDto dto = UserDto.builder()
            .userId(1)
            .firstName("John")
            .lastName("Doe")
            .imageUrl(null)
            .email("john@mail.com")
            .phone("123")
            .credentialDto(credDto)
            .build();

        // mapear DTO a entidad (helper) para simular lo que retorna el repo
        User entity = UserMappingHelper.map(dto);

        when(userRepository.save(any(User.class))).thenReturn(entity);

        UserDto result = userService.save(dto);

        assertEquals("John", result.getFirstName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testDeleteById_callsRepositoryDelete() {
        userService.deleteById(1);
        verify(userRepository).deleteById(1);
    }

    @Test
    void testFindByUsername_returnsUser() {
        User user = new User(1, "John", "Doe", null, "john@mail.com", "123", null, null);

        Credential credential = Credential.builder()
            .credentialId(1)
            .username("john")
            .password("123")
            .isEnabled(true)
            .isAccountNonExpired(true)
            .isAccountNonLocked(true)
            .isCredentialsNonExpired(true)
            .build();

        credential.setUser(user);
        user.setCredential(credential);

        when(userRepository.findByCredentialUsername("john"))
            .thenReturn(Optional.of(user));

        UserDto result = userService.findByUsername("john");

        assertEquals("John", result.getFirstName());
        verify(userRepository).findByCredentialUsername("john");
    }


    @Test
    void testFindByUsername_throwsException() {
        when(userRepository.findByCredentialUsername("x")).thenReturn(Optional.empty());
        assertThrows(UserObjectNotFoundException.class, () -> userService.findByUsername("x"));
    }
}

