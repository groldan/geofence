package org.geoserver.geofence.jpa.repository;

import org.springframework.transaction.annotation.Propagation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@org.springframework.transaction.annotation.Transactional(
        transactionManager = "geofenceTransactionManager",
        propagation = Propagation.REQUIRED)
@Inherited
@Retention(value = RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface TransactionRequired {}
