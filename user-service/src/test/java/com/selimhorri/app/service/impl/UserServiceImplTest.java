package com.selimhorri.app.service.impl;

import static org.junit.jupiter.api.Assertions.*;
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
import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.domain.User;
import com.selimhorri.app.helper.UserMappingHelper;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testFindById_returnsUser() {
        User user = new User(1, "John", "Doe", null, "john@mail.com", "123", null, null);
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
        UserDto dto = new UserDto(1, "John", "Doe", null, "john@mail.com", "123", null, null);
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
        when(userRepository.findByCredentialUsername("john")).thenReturn(Optional.of(user));

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

