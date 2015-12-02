package com.cisco.ccit.example.rest;

import org.apache.cxf.transport.servlet.CXFServlet;
import org.jmockring.annotation.BootstrapConfig;
import org.jmockring.annotation.DynamicContext;
import org.jmockring.annotation.Filter;
import org.jmockring.annotation.Param;
import org.jmockring.annotation.Server;
import org.jmockring.annotation.Servlet;
import org.jmockring.junit.ExternalServerJUnitSuiteRunner;
import org.jmockring.spi.client.RestAssuredClient;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;

/**
 * This is a JUnit suite of tests which runs the {@link ExternalServerJUnitSuiteRunner} from JMockring tool.
 * <p/>
 * The {@link Server} annotation contains configuration to bootstrap the web application
 * and deploy in on Jetty Web server, which is started automatically and listens on available port.
 * <p/>
 * The wrapper class {@link RestAssuredClient} provides auto-configured REST client which delegates to
 * <a href="https://github.com/jayway/rest-assured">RestAssured</a> testing tool.
 * <p/>
 * See {@link ExampleResourceIntegration} for test examples.
 *
 * @author Pavel Lechev <plechev@cisco.com>
 * @since 30/04/2015
 */
@RunWith(ExternalServerJUnitSuiteRunner.class)
@BootstrapConfig(numberOfAttempts = 100,
        systemProperties = {
                @Param(name = "spring.profiles.active", value = "lcl,rest-api,default")
        }
)
@Server(testClass = RestSuiteIT.class,
        dynamicContexts = {
                @DynamicContext(
                        autoMocks = false,   // disables automatic mocking of missing Spring beans
                        contextPath = "/",   // this is the context path part which comes after the [hostname:port]
                        springContextLocations = {
                                "classpath:/spring/bootstrap-rest-context.xml"
                        },
                        filters = {
                                @Filter(
                                        mapping = "/api/*",    // ensure this path matches the servlet mapping in the `web.xml`
                                        name = "OpenEntityManagerInViewFilter",
                                        filterClass = OpenEntityManagerInViewFilter.class
                                )
                        },
                        servlets = {
                                @Servlet(
                                        mapping = "/api/*",
                                        servletClass = CXFServlet.class,
                                        params = {
                                                @Param(name = "config-location", value = "/spring/cxf-config.xml"),
                                                @Param(name = "service-list-path", value = "/cxf-services-wadl")
                                        }
                                )
                        }
                )
        })
@Suite.SuiteClasses({
        // list all test classes which are part of this suite:
        ExampleResourceIntegration.class
}
)
public class RestSuiteIT {

}

