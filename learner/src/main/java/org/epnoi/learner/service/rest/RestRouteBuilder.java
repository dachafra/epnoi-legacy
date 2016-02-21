package org.epnoi.learner.service.rest;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.epnoi.model.Term;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by cbadenes on 21/02/16.
 */
@Component
public class RestRouteBuilder extends RouteBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(RestRouteBuilder.class);

    @Value("${learner.rest.port}")
    protected Integer port;


    @PostConstruct
    public void setup(){
        LOG.info("Initializing Rest Route Builder...");
    }

    @Override
    public void configure() throws Exception {

        LOG.info("Initializing REST services...");
        restConfiguration()
                .component("servlet")
                .bindingMode(RestBindingMode.json_xml)
                .dataFormatProperty("prettyPrint", "true")
                .dataFormatProperty("json.in.disableFeatures", "FAIL_ON_UNKNOWN_PROPERTIES,ADJUST_DATES_TO_CONTEXT_TIME_ZONE")
                .dataFormatProperty("xml.out.mustBeJAXBElement", "false")
                .contextPath("learner/rest")
                .port(port);

        LOG.info("Rest configuration initialized" );

//        rest("/learning").description("rest service for learning")
//                .post("/{id}").description("Learn a new model").outType(URI.class)
//                .produces("application/json").to("bean:learnerResource?method=learnDomain(${header.id})");

        rest("/terms").description("rest service for terms")
                .get("/{id}").description("Retrieve terms of domain").outTypeList(Term.class)
                .produces("application/json").to("bean:learnerResource?method=getTerms(${header.id})");

//        rest("/relations").description("rest service for relations")
//                .get("/{id}").description("Retrieve relations of domain").outTypeList(Term.class)
//                .produces("application/json").to("bean:learnerResource?method=getRelations(${header.id})");
//
//        rest("/corpus").description("rest service for corpus")
//                .post("/").description("Harvest a new corpus").to("bean:demoResource?method=create()")
//                .delete("/").description("Remove existing corpus").to("bean:demoResource?method=remove()");
//
//        rest("/patterns").description("rest service for patterns model")
//                .post("/{path}").description("Create a new pattern model in path").outType(URI.class)
//                .produces("application/json").to("bean:trainerResource?method=createRelationalPatternsModel(${path})");
//
//        rest("/sentences").description("rest service for relational sentences corpus")
//                .post("/{maxLenght}&{id}").description("Create a new pattern model in path").outType(URI.class)
//                .produces("application/json").to("bean:trainerResource?method=createRelationalSentenceCorpus(${maxLenght},${uri})");

        LOG.info("Rest /terms initialized");


    }

}