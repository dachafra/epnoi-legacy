package org.epnoi.learner.service.rest;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.epnoi.model.*;
import org.epnoi.model.Term;
import org.epnoi.model.domain.resources.*;
import org.epnoi.model.domain.resources.Item;
import org.epnoi.model.domain.resources.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;

/**
 * Created by cbadenes on 21/02/16.
 */
@Component
public class RestRouteBuilder extends RouteBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(RestRouteBuilder.class);

    @Value("${learner.rest.port}")
    protected Integer port;

    @Override
    public void configure() throws Exception {

        restConfiguration()
                .component("servlet")
                .scheme("http")
                .bindingMode(RestBindingMode.json_xml)
                .skipBindingOnErrorCode(true)
                .jsonDataFormat("json-jackson")
                .xmlDataFormat("jaxb")
                .enableCORS(false)
                .componentProperty("minThreads","1")
                .componentProperty("maxThreads","10")
                .componentProperty("maxConnectionsPerHost","-1")
                .componentProperty("maxTotalConnections","-1")
                .dataFormatProperty("prettyPrint", "true")
                .dataFormatProperty("json.in.disableFeatures", "FAIL_ON_UNKNOWN_PROPERTIES,ADJUST_DATES_TO_CONTEXT_TIME_ZONE")
//                .dataFormatProperty("json.out.disableFeatures", "WRITE_NULL_MAP_VALUES")
                .dataFormatProperty("xml.out.mustBeJAXBElement", "false")
                .contextPath("learner/0.1")
                .port(port);


        rest("/learning").description("rest service for learning")
                .post("/{id}").description("Learn a new model").outType(URI.class)
                .produces("application/json").to("bean:learnerResource?method=learnDomain(${header.id})");

        rest("/terms").description("rest service for terms")
                .get("/{id}").description("Retrieve terms of domain").outTypeList(Term.class)
                .produces("application/json").to("bean:learnerResource?method=getTerms(${header.id})");

        rest("/relations").description("rest service for relations")
                .get("/{id}").description("Retrieve relations of domain").outTypeList(Term.class)
                .produces("application/json").to("bean:learnerResource?method=getRelations(${header.id})");

        rest("/corpus").description("rest service for corpus")
                .post("/").description("Harvest a new corpus").to("bean:demoResource?method=create()")
                .delete("/").description("Remove existing corpus").to("bean:demoResource?method=remove()");

        rest("/patterns").description("rest service for patterns model")
                .post("/{path}").description("Create a new pattern model in path").outType(URI.class)
                .produces("application/json").to("bean:trainerResource?method=createRelationalPatternsModel(${path})");

        rest("/sentences").description("rest service for relational sentences corpus")
                .post("/{maxLenght}&{id}").description("Create a new pattern model in path").outType(URI.class)
                .produces("application/json").to("bean:trainerResource?method=createRelationalSentenceCorpus(${maxLenght},${uri})");



        ;


    }

    private void createReadUpdateDeleteOf(String domain, String label, Class facade, Class resource){


    }


}