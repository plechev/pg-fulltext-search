package com.cisco.ccit.ftsearch.repository;

import org.springframework.data.repository.CrudRepository;

import com.cisco.ccit.ftsearch.domain.Client;

/**
 * @author Pavel Lechev <p.lechev@gmail.com>
 * @since 17/01/2017
 */
public interface ClientRepository extends CrudRepository<Client, Integer> {

}
