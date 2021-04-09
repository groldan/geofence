/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.model;

import java.net.Inet4Address;
import java.net.InetAddress;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.geoserver.geofence.core.model.util.SubnetV4Utils;

/** @author ETj (etj at geo-solutions.it) */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IPAddressRange {

    /** The lower 64 bits. For IPv4, only the lower 32 are used. */
    private Long low;
    /** The higher 64 bits. For IPv4, this is null */
    private Long high;

    /**
     * CIDR based prefix size. It's equivalent to the number of leading 1 bits in the routing prefix
     * mask. http://en.wikipedia.org/wiki/Classless_Inter-Domain_Routing
     */
    private Integer size;

    public IPAddressRange(String cidrNotation) {
        SubnetV4Utils su = new SubnetV4Utils(cidrNotation);
        low = new Long(su.getInfo().getAddressAsInteger());
        size = su.getInfo().getMaskSize();
    }

    public boolean match(String address) {
        if (!SubnetV4Utils.isAddress(address)) return false;

        SubnetV4Utils su = new SubnetV4Utils(low, size);
        return su.getInfo().isInRange(address);
    }

    public boolean match(InetAddress address) {
        if (address instanceof Inet4Address) {
            return match((Inet4Address) address);
        } else {
            throw new UnsupportedOperationException("IPv6 non implemented yet");
        }
    }

    public boolean match(Inet4Address address) {
        SubnetV4Utils su = new SubnetV4Utils(low, size);
        return su.getInfo().isInRange(address.getHostAddress());
    }

    public String getAddress() {
        return high == null ? encodeIPv4() : encodeIPv6();
    }

    /** @return the range in CIDR format: x.y.z.w/sz */
    public String getCidrSignature() {
        if (high == null) {
            SubnetV4Utils su = new SubnetV4Utils(low, size);
            return su.getInfo().getCidrSignature();
        } else {
            throw new UnsupportedOperationException("IPv6 non implemented yet");
        }
    }

    protected String encodeIPv4() {
        SubnetV4Utils su = new SubnetV4Utils(low, size);
        return su.getInfo().getAddress();
    }

    protected String encodeIPv6() {
        throw new UnsupportedOperationException("IPv6 non implemented yet");
    }

    @Override
    public String toString() {
        return getCidrSignature();
    }
}
