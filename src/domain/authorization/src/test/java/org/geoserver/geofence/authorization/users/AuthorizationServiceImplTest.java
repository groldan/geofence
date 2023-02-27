package org.geoserver.geofence.authorization.users;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.geoserver.geofence.authorization.users.AuthUser.Role;
import org.geoserver.geofence.users.model.GeoServerUser;
import org.geoserver.geofence.users.service.UserAdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

class AuthorizationServiceImplTest {

    private UserAdminService userService;
    private AuthorizationServiceImpl authService;

    @BeforeEach
    void setUp() throws Exception {
        userService = mock(UserAdminService.class);
        authService = new AuthorizationServiceImpl(userService);
    }

    @Test
    void testAuthorize() throws AuthorizationException {

        assertThrows(NullPointerException.class, () -> authService.authorize(null, "s3cret"));
        assertThrows(NullPointerException.class, () -> authService.authorize("jdoe", null));

        when(userService.get(eq("jdoe"))).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> authService.authorize("jdoe", "s3cret"));

        GeoServerUser user = GeoServerUser.builder().name("jdoe").password("notsecret").build();
        GeoServerUser admin = user.withName("admin").withAdmin(true);

        when(userService.get(eq("jdoe"))).thenReturn(Optional.of(user));
        when(userService.get(eq("admin"))).thenReturn(Optional.of(admin));

        assertThrows(
                InvalidCredentialsException.class, () -> authService.authorize("jdoe", "s3cret"));
        assertThrows(
                InvalidCredentialsException.class, () -> authService.authorize("admin", "s3cret"));

        AuthUser authorized = authService.authorize("jdoe", "notsecret");
        assertThat(authorized).isNotNull();
        assertThat(authorized.getName()).isEqualTo("jdoe");
        assertThat(authorized.getRole()).isEqualTo(Role.USER);

        authorized = authService.authorize("admin", "notsecret");
        assertThat(authorized).isNotNull();
        assertThat(authorized.getName()).isEqualTo("admin");
        assertThat(authorized.getRole()).isEqualTo(Role.ADMIN);
    }
}
