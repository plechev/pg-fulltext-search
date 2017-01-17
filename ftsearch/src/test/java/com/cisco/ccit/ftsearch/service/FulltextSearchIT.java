package com.cisco.ccit.ftsearch.service;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.jmockring.utils.hamcrest.CollectionIntrospectMatcher.collectionWithSize;

import java.util.Date;
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

import com.cisco.ccit.ftsearch.domain.Client;
import com.cisco.ccit.ftsearch.domain.Order;
import com.cisco.ccit.ftsearch.domain.Preference;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/ftsearch-context.xml"
})
@TransactionConfiguration(transactionManager = "ftsearchTransactionManager")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, TransactionalTestExecutionListener.class})
public class FulltextSearchIT {

    @Rule
    public DbUnitRule dbUnitRule = new DbUnitRule("/dbunit/dbunit.properties",
            "dbunit/client-dataset.xml"
    ).withTuner(DbUnitTuner.POSTGRES);

    @Autowired
    private ClientService clientService;

    @Test
    public void shouldFindClientsForName() throws Exception {
        final List<Client> clients = clientService.findClients("Joe");
        assertThat(clients, collectionWithSize(1)
                .hasElementWhere("id", is(3)).andWhere("name", is("Joe"))
        );
    }


    @Test
    public void shouldFindMultipleClientsForAnyQuery() throws Exception {
        final List<Client> clients = clientService.findClients("pilot");

        // the ranking order must be preserved
        assertThat(clients.get(0).getId(), is(4));
        assertThat(clients.get(0).getName(), is("Han Solo"));
        assertThat(clients.get(1).getId(), is(3));
        assertThat(clients.get(1).getName(), is("Joe"));
    }


    @Test
    public void shouldInsertNewClientRecordAndPerformRankedSearch() throws Exception {

        clientService.saveClient(new Client().withId(26).withName("Leia Organa").withBio("Leader of the resistance. Likes rogue pilots"));
        clientService.saveOrder(new Order().withClientId(26).withId(21).withDescription("white robe").withDate(new Date()));
        clientService.savePreference(new Preference().withClientId(26).withLocation("Alderaan").withProducts("white robes, pilot manual"));

        // note the order based on the relevancy ranking
        final List<Client> clients = clientService.findClients("pilot");
        assertThat(clients, hasSize(3));
        assertThat(clients.get(0).getName(), is("Han Solo"));
        assertThat(clients.get(1).getName(), is("Leia Organa"));
        assertThat(clients.get(2).getName(), is("Joe"));

        // note the order based on the relevancy ranking
        final List<Client> clients2 = clientService.findClients("white");
        assertThat(clients2, hasSize(2));
        assertThat(clients2.get(0).getName(), is("Kylo Ren"));    // Despite the fact Leia has 2 occurrences of the word `white`, she comes second since
        assertThat(clients2.get(1).getName(), is("Leia Organa")); // Kylo has the word `white` in his BIO, which has higher weight when calculating relevancy.

        // note the order based on the relevancy ranking
        final List<Client> clients3 = clientService.findClients("galaxy");
        assertThat(clients3, hasSize(2));
        assertThat(clients3.get(0).getName(), is("Kylo Ren"));  // Kylo comes first as is has word `galaxy` in both BIO and LOCATION
        assertThat(clients3.get(1).getName(), is("Han Solo"));  // Han has the word `galaxy` only in LOCATION
    }

}
