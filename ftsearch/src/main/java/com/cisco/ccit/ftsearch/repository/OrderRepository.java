package com.cisco.ccit.ftsearch.repository;

import org.springframework.data.repository.CrudRepository;

import com.cisco.ccit.ftsearch.domain.Order;

/**
 * @author Pavel Lechev <p.lechev@gmail.com>
 * @since 17/01/2017
 */
public interface OrderRepository extends CrudRepository<Order, Integer> {

}
