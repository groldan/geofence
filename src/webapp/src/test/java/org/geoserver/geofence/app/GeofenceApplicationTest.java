package org.geoserver.geofence.app;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class GeofenceApplicationTest {

    private @Autowired ApplicationContext context;

    @Test
    public void contextLoads() {
        assertNotNull(context);
    }
}
