package com.cisco.ccit.ftsearch.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Pavel Lechev <p.lechev@gmail.com>
 * @since 17/01/2017
 */
@Entity
@Table(name = "CLIENT_PREFERENCE")
public class Preference {

    @Id
    @Column(name = "client_id")
    private Integer clientId;

    @Column(name = "location")
    private String location;

    @Column(name = "products")
    private String products;

    public Integer getClientId() {
        return clientId;
    }

    public String getLocation() {
        return location;
    }

    public String getProducts() {
        return products;
    }

    public Preference withClientId(final Integer clientId) {
        this.clientId = clientId;
        return this;
    }

    public Preference withLocation(final String location) {
        this.location = location;
        return this;
    }

    public Preference withProducts(final String products) {
        this.products = products;
        return this;
    }
}
