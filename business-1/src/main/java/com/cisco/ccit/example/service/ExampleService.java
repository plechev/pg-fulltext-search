package com.cisco.ccit.example.service;

import java.util.List;

import com.cisco.ccit.example.domain.Example;

/**
 * @author Pavel Lechev <plechev@cisco.com>
 * @since 01/06/2015
 */
public interface ExampleService {

    void exampleMethod();

    Example getExample();

    List<Example> getExamples();

}
