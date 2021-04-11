/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.jpa.repository;

import org.geoserver.geofence.jpa.model.JPALayerDetails;
import org.springframework.stereotype.Repository;

/**
 * Public implementation of the RuleLimitsDAO interface
 *
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
@Repository
public class LayerDetailsRepository extends BaseRepository<JPALayerDetails, Long> {}
