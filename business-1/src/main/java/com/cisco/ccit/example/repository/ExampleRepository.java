package com.cisco.ccit.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cisco.ccit.example.domain.Example;

/**
 * @author Pavel Lechev <plechev@cisco.com>
 * @since 01/06/2015
 */
public interface ExampleRepository extends JpaRepository<Example, Long> {


    /**
     * Auto-implemented by SpringData
     *
     * @param name
     * @return
     */
    Example findByName(String name);

}
