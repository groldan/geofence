/*
 * (c) 2014 - 2023 Open Source Geospatial Foundation - all rights reserved This code is licensed
 * under the GPL 2.0 license, available at the root application directory.
 */

package org.geoserver.geofence.rules.model;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.With;

import java.net.Inet4Address;
import java.net.InetAddress;

/**
 * @author ETj (etj at geo-solutions.it)
 */
@Value
@With
@Builder(toBuilder = true, builderClassName = "Builder")
public class IPAddressRange {

    private Long low;
    private Long high;
    private Integer size;

    /**
     * @return the range in CIDR format: x.y.z.w/sz
     */
    public static String getCidrSignature(@NonNull IPAddressRange range) {
        if (range.getHigh() == null) {
            Long low = range.getLow();
            Integer size = range.getSize();
            if (low != null && size != null) {
                SubnetV4Utils su = new SubnetV4Utils(low, size);
                return su.getInfo().getCidrSignature();
            }
            return null;
        }
        throw new UnsupportedOperationException("IPv6 non implemented yet");
    }

    public static IPAddressRange fromCidrSignature(@NonNull String cidrNotation) {
        SubnetV4Utils su = new SubnetV4Utils(cidrNotation);
        Long low = Long.valueOf(su.getInfo().getAddressAsInteger());
        Integer size = su.getInfo().getMaskSize();
        return new IPAddressRange(low, null, size);
    }

    public boolean matches(String address) {
        if (!SubnetV4Utils.isAddress(address)) return false;

        SubnetV4Utils su = new SubnetV4Utils(low, size);
        return su.getInfo().isInRange(address);
    }

    public boolean matches(InetAddress address) {
        if (address instanceof Inet4Address) {
            return matches((Inet4Address) address);
        }
        throw new UnsupportedOperationException("IPv6 non implemented yet");
    }

    public boolean matches(Inet4Address address) {
        SubnetV4Utils su = new SubnetV4Utils(low, size);
        return su.getInfo().isInRange(address.getHostAddress());
    }
}
