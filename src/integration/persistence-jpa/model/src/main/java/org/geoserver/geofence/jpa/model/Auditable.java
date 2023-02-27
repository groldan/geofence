package org.geoserver.geofence.jpa.model;

import lombok.Data;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@Data
@MappedSuperclass
public abstract class Auditable implements Serializable {

    private static final long serialVersionUID = 141481953116476081L;

    @CreatedBy
    @Column(updatable = false, nullable = true)
    private String createdBy;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdDate;

    @LastModifiedBy
    @Column(updatable = true, nullable = true)
    private String lastModifiedBy;

    @LastModifiedDate
    @Column(updatable = true, nullable = true)
    private LocalDateTime lastModifiedDate;
}
