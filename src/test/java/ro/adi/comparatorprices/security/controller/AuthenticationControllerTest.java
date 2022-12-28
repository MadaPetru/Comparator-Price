package ro.adi.comparatorprices.security.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ro.adi.comparatorprices.security.SecurityMock;
import ro.adi.comparatorprices.security.config.JwtUtils;
import ro.adi.comparatorprices.security.dto.request.AuthenticationRequestDto;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthenticationControllerTest {
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private JwtUtils jwtUtils;

    private MockMvc mockMvc;

    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }

    @Test
    void authenticate_shouldGenerateToken() throws Exception {
        AuthenticationRequestDto requestDto = SecurityMock.getRequestDtoMock();

        mockMvc.perform(
                post("/login").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk()).andReturn();
    }

    @Test
    void authenticate_shouldValidateValidToken() throws Exception {
        UserDetails userDetailsMock = SecurityMock.getUserDetailsMock();
        AuthenticationRequestDto requestDto = SecurityMock.getRequestDtoMock();
        String token = "Bearer " + jwtUtils.generateToken(userDetailsMock);

        mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(requestDto)).header(AUTHORIZATION, token)).andExpect(status().isOk())
                .andReturn();
    }
}