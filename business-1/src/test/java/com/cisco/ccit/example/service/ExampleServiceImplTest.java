package com.cisco.ccit.example.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.cisco.ccit.example.repository.ExampleRepository;


@RunWith(MockitoJUnitRunner.class)
public class ExampleServiceImplTest {

    @InjectMocks
    private ExampleServiceImpl underTest;

    @Mock
    private ExampleRepository exampleRepositoryMock;


    /**
     * @verifies make example call
     * @see ExampleServiceImpl#exampleMethod()
     */
    @Test
    public void exampleMethod_shouldMakeExampleCall() throws Exception {
        //TODO auto-generated
        Assert.fail("Not yet implemented");
    }
}
