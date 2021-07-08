package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.config.security.authentication.JeapAuthenticationToken;
import ch.admin.bag.covidcertificate.config.security.authentication.ServletJeapAuthorization;
import ch.admin.bag.covidcertificate.testutil.JeapAuthenticationTestTokenBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.Jwt;

import javax.servlet.http.HttpServletRequest;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityHelperTest {

    @InjectMocks
    private SecurityHelper securityHelper;

    @Mock
    private ServletJeapAuthorization jeapAuthorization;

    @Test
    void authorizeUser_userAuthorized() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString(anyString())).thenReturn("test");
        when(jwt.getClaimAsString("homeName")).thenReturn("E-ID CH-LOGIN");
        when(jwt.getClaimAsString("unitName")).thenReturn("Other");
        JeapAuthenticationToken jeapAuthenticationToken = JeapAuthenticationTestTokenBuilder.createWithJwt(jwt).build();
        when(jeapAuthorization.getJeapAuthenticationToken()).thenReturn(jeapAuthenticationToken);
        boolean authorizeUser = securityHelper.authorizeUser(request);
        assertTrue(authorizeUser);
    }

    @Test
    void authorizeUser_userNotAuthorized() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString(anyString())).thenReturn("test");
        when(jwt.getClaimAsString("homeName")).thenReturn("E-ID CH-LOGIN");
        when(jwt.getClaimAsString("unitName")).thenReturn("HIN");
        JeapAuthenticationToken jeapAuthenticationToken = JeapAuthenticationTestTokenBuilder.createWithJwt(jwt).build();
        when(jeapAuthorization.getJeapAuthenticationToken()).thenReturn(jeapAuthenticationToken);

        assertThrows(AccessDeniedException.class, () -> {
            securityHelper.authorizeUser(request);
        });
    }
}