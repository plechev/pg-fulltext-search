package com.cisco.ccit.example.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cisco.ccit.example.domain.Example;
import com.cisco.ccit.example.repository.ExampleRepository;

/**
 * @author Pavel Lechev <plechev@cisco.com>
 * @since 01/06/2015
 */
@Service
@Transactional
public class ExampleServiceImpl implements ExampleService {

    @Autowired
    private ExampleRepository exampleRepository;

    /**
     * @should make example call
     */
    public void exampleMethod() {
        exampleRepository.findByName("test");
    }

    public Example getExample() {
        return exampleRepository.findByName("test");
    }

    public List<Example> getExamples() {
        return exampleRepository.findAll();
    }
}
