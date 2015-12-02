package com.cisco.ccit.example.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cisco.ccit.example.service.ExampleService;

/**
 * @author Pavel Lechev <plechev@cisco.com>
 * @since 29/04/2015
 */
@Path("/example")
@Component("ExampleResource")
public class ExampleResource {

    @Autowired
    private ExampleService exampleService;


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getExamples() {
        return Response.status(200).entity(exampleService.getExamples()).build();
    }

}
