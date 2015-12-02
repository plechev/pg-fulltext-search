package com.cisco.ccit.example.repository;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

import java.util.List;

import org.jmockring.utils.dbunit.DbUnitRule;
import org.jmockring.utils.dbunit.DbUnitTuner;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import com.cisco.ccit.example.domain.Example;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/business-1-context.xml"
})
@TransactionConfiguration(transactionManager = "exampleTransactionManager")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, TransactionalTestExecutionListener.class})
public class ExampleRepositoryIT {

    @Rule
    public DbUnitRule dbUnitRule = new DbUnitRule("/dbunit/dbunit-{cisco.life}.properties",
            "dbunit/example-dataset.xml")
            .withTuner(DbUnitTuner.H2);


    @Autowired
    private ExampleRepository underTest;

    @Test
    public void shouldFindAllExamples() throws Exception {
        final List<Example> examples = underTest.findAll();
        assertThat(examples, hasSize(2));
    }

    @Test
    public void shouldSaveExample() throws Exception {
        underTest.save(new Example().withName("New Example 123"));

        final List<Example> examples = underTest.findAll();
        assertThat(examples, hasSize(3));
    }

}
