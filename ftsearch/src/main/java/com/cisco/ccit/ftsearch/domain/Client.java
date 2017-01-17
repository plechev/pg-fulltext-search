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
@Table(name="CLIENT")
public class Client {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "bio")
    private String bio;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBio() {
        return bio;
    }

    public Client withId(final Integer id) {
        this.id = id;
        return this;
    }

    public Client withName(final String name) {
        this.name = name;
        return this;
    }

    public Client withBio(final String bio) {
        this.bio = bio;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Client{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", bio='").append(bio).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
