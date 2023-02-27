package org.geoserver.geofence.jpa.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import javax.sql.DataSource;

@Configuration
@Import(GeoFenceJPAPropertiesConfiguration.class)
public class GeoFenceDataSourceConfiguration {

    /**
     * E.g.:
     *
     * <pre>{@code
     * geofence:
     *   datasource:
     *     url: jdbc:h2:mem:geofence
     *     username: sa
     *     password: sa
     *     hikari:
     *       minimum-idle: 0
     *       maximum-pool-size: 20
     * }</pre>
     *
     * Or:
     *
     * <pre>{@code
     * geofence:
     *   datasource:
     *     jndiName: java:comp/env/jdbc/geofence
     * }</pre>
     */
    @Bean("geofenceDataSource")
    public DataSource geofeDataSource(GeofenceJPAPropertiesResolver props) {
        final String jndiName =
                Optional.ofNullable(props.get("datasource.jndiName"))
                        .orElseGet(() -> props.get("datasource.jndi-name"));

        if (null != jndiName) {
            new JndiDataSourceLookup().getDataSource(jndiName);
        }

        String url = props.get("datasource.url");
        if (!StringUtils.hasText(url)) {
            throw new IllegalArgumentException(
                    "geofence.datasource.url or geofence.datasource.jndiName is requried");
        }
        String username = props.get("datasource.username");
        String pwd = props.get("datasource.password");
        HikariConfig hconf = new HikariConfig(hikariProperties(props));
        hconf.setJdbcUrl(url);
        hconf.setUsername(username);
        hconf.setPassword(pwd);
        HikariDataSource ds = new HikariDataSource(hconf);
        return ds;
    }

    private Properties hikariProperties(GeofenceJPAPropertiesResolver config) {

        Map<String, String> map = config.getMap("hikari");
        Properties props = new Properties();
        props.putAll(map);
        return props;
    }
}
