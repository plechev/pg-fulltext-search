package com.cisco.ccit.ftsearch.domain;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author Pavel Lechev <p.lechev@gmail.com>
 * @since 17/01/2017
 */
@Entity
@Table(name = "CLIENT_ORDER")
public class Order {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "client_id")
    private Integer clientId;

    @Column(name = "description")
    private String description;

    @Column(name = "date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    public Integer getId() {
        return id;
    }

    public Integer getClientId() {
        return clientId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public Order withDate(final Date date) {
        this.date = date;
        return this;
    }

    public Order withId(final Integer id) {
        this.id = id;
        return this;
    }

    public Order withClientId(final Integer clientId) {
        this.clientId = clientId;
        return this;
    }

    public Order withDescription(final String description) {
        this.description = description;
        return this;
    }
}
