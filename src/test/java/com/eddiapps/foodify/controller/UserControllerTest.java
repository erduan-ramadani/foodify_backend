package com.eddiapps.foodify.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static java.util.Collections.emptyList;

import com.eddiapps.foodify.dto.UpdateUserRequest;
import com.eddiapps.foodify.dto.UserResponse;
import com.eddiapps.foodify.exception.UserNotFoundException;
import com.eddiapps.foodify.repository.UserRepository;
import com.eddiapps.foodify.service.JwtService;
import com.eddiapps.foodify.service.UserService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void getUserByIdSuccessful() throws Exception {
        UserResponse response = new UserResponse(1L, "eddi@web.de", "Eddi");

        when(userService.getUserById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("eddi@web.de"))
                .andExpect(jsonPath("$.name").value("Eddi"));

        verify(userService).getUserById(1L);
    }

    @Test
    void getUserById_userNotFound() throws Exception {
        when(userService.getUserById(99L)).thenThrow(UserNotFoundException.class);

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound());

        verify(userService).getUserById(99L);
    }

    @Test
    void getAllUsers_withPagination() throws Exception {
        Pageable pageable = PageRequest.of(1, 5);

        when(userService.getAllUsers(pageable)).thenReturn(new PageImpl<>(emptyList()));
        mockMvc.perform(get("/api/users?page=1&size=5"))
                .andExpect(status().isOk());

        verify(userService).getAllUsers(pageable);
    }

    @Test
    void getAllUsers() throws Exception {
        Pageable pageable = PageRequest.of(0, 20);
        UserResponse user1 = new UserResponse(1L, "eddi@web.de", "Eddi");
        UserResponse user2 = new UserResponse(2L, "anna@web.de", "Anna");

        when(userService.getAllUsers(pageable)).thenReturn(new PageImpl<>(List.of(user1, user2)));
        mockMvc.perform(get("/api/users?page=0&size=20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content[0].name").value("Eddi"))
                .andExpect(jsonPath("$.content[1].email").value("anna@web.de"));

        verify(userService).getAllUsers(pageable);
    }

    @Test
    void deleteUserSuccessful() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isOk());

        verify(userService).deleteUser(1L);
    }

    @Test
    void deleteUser_userNotFound() throws Exception {
        doThrow(UserNotFoundException.class).when(userService).deleteUser(99L);
        mockMvc.perform(delete("/api/users/99"))
                .andExpect(status().isNotFound());

        verify(userService).deleteUser(99L);
    }

    @Test
    void updateUser_invalidEmail() throws Exception {
        mockMvc.perform(
                        put("/api/users/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "name": "Eddi Neu",
                                            "email": "keine-email"
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());

        verify(userService, never()).updateUser(eq(1L), any(UpdateUserRequest.class));
    }

    @Test
    void updateUser_blankName() throws Exception {
        mockMvc.perform(
                        put("/api/users/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "name": "",
                                            "email": "eddineu@web.de"
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());

        verify(userService, never()).updateUser(eq(1L), any(UpdateUserRequest.class));
    }

    @Test
    void updateUser_userNotFound() throws Exception {
        when(userService.updateUser(eq(99L), any(UpdateUserRequest.class)))
                .thenThrow(UserNotFoundException.class);


        mockMvc.perform(
                        put("/api/users/99")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "name": "Eddi Neu",
                                            "email": "eddineu@web.de"
                                        }
                                        """)
                )
                .andExpect(status().isNotFound());

        verify(userService).updateUser(eq(99L), any(UpdateUserRequest.class));
    }

    @Test
    void updateUserSuccessful() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setEmail("eddineu@web.de");
        request.setName("Eddi Neu");

        UserResponse response = new UserResponse(1L, request.getEmail(), request.getName());

        when(userService.updateUser(eq(1L), any(UpdateUserRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        put("/api/users/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "name": "Eddi Neu",
                                            "email": "eddineu@web.de"
                                        }
                                        """)
                )
                .andExpect(jsonPath("$.email").value("eddineu@web.de"))
                .andExpect(jsonPath("$.name").value("Eddi Neu"))
                .andExpect(status().isOk());

        verify(userService).updateUser(eq(1L), any(UpdateUserRequest.class));
    }
}
