package com.cisco.ccit.example.rest;

import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.core.Is.is;

import org.jmockring.annotation.ContextDefaults;
import org.jmockring.annotation.PartOfSuite;
import org.jmockring.annotation.RemoteBean;
import org.jmockring.annotation.RemoteMock;
import org.jmockring.annotation.RequestClient;
import org.jmockring.junit.ExternalServerJUnitRunner;
import org.jmockring.spi.client.RestAssuredClient;
import org.jmockring.utils.dbunit.DbUnitRule;
import org.jmockring.utils.dbunit.DbUnitTuner;
import org.jmockring.webserver.jetty.JettyWebServer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jayway.restassured.specification.ResponseSpecification;

/**
 * A test class which is run as part of the REST IT Suite {@link RestSuiteIT}.
 * <p/>
 * This requires configured annotations, which specify the Jmockring JUnit runner
 * + {@link PartOfSuite} which links it to the suite configuration and allows the tests
 * here to be executed against the application deployed with the suite configuration.
 * <p/>
 * The easiest way to interact with the REST API is to inject the client {@link RestAssuredClient} into the test - see {@link ExampleResourceIntegration#client}.
 * This is a fully configured client which is ready to be used to issue HTTP requests.
 * <p/>
 * In addition, we can inject Spring beans (with {@link RemoteBean}) or mocked Spring beans (with {@link RemoteMock}) from the deployed application context. <br>
 * This allows us to interact with the Spring context directly from within the test class (the same way we do when running standard Spring tests with {@link SpringJUnit4ClassRunner}).
 */
@RunWith(ExternalServerJUnitRunner.class)
@PartOfSuite(RestSuiteIT.class)
@ContextDefaults(contextPath = "/", bootstrap = JettyWebServer.class)
public class ExampleResourceIntegration {


    /**
     * Here we add a JUnit rule (see {@link Rule}) whose purpose is to fire-up the <a href="http://dbunit.sourceforge.net/">DBUnit</a> tool.
     * <p/>
     * See {@link DbUnitRule} for explanation how this rule is used in tests.
     */
    @Rule
    public DbUnitRule dbUnitRule = new DbUnitRule("/dbunit/dbunit-{cisco.life}.properties",
            "dbunit/#1_examples.xml"
    ).withTuner(DbUnitTuner.H2);


    @RequestClient
    private RestAssuredClient client;

    /**
     * Each test can iisue one or more REST requests and do assertions on the responses.
     * <p/>
     * The most commonly used assertions are:
     * <ol>
     *  <li>HTTP Response status code - {@link ResponseSpecification#statusCode(int)} </li>
     *  <li>Response body contents with Json assertions - </li>
     * </ol>
     *
     * @throws Exception
     */
    @Test
    public void shouldGetAllExamples() throws Exception {

        client.newRequest()
                .request().log().all(true)
                .response().log().all(true)
                .expect()
                .statusCode(OK.getStatusCode())
                .body("[0].id", is(1))
                .body("[0].name", is("test"))
                .body("[1].id", is(2))
                .body("[1].name", is("test2"))
                .body("[2].id", is(100))
                .body("[2].name", is("test123"))
                .when()
                .get("/api/example");

    }
}
