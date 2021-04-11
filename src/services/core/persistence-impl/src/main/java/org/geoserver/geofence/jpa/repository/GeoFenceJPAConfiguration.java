/* (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.geofence.jpa.repository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** {@link Configuration @Configuration} to create all the JPA repository beans */
@Configuration
public class GeoFenceJPAConfiguration {

    public @Bean GSUserRepository gsUserRepository() {
        return new GSUserRepository();
    }

    public @Bean GFUserRepository gfUserRepository() {
        return new GFUserRepository();
    }

    public @Bean UserGroupRepository userGroupRepository() {
        return new UserGroupRepository();
    }

    public @Bean GSInstanceRepository gsInstanceRepository() {
        return new GSInstanceRepository();
    }

    public @Bean LayerDetailsRepository layerDetailsRepository() {
        return new LayerDetailsRepository();
    }

    public @Bean AdminRuleRepository adminRuleRepository() {
        return new AdminRuleRepository();
    }

    public @Bean RuleRepository ruleRepository() {
        return new RuleRepository();
    }

    public @Bean RuleLimitsRepository ruleLimitsRepository() {
        return new RuleLimitsRepository();
    }
}
