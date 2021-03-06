package org.epnoi.learner.scheduler;

import es.cbadenes.lab.test.IntegrationTest;
import org.epnoi.learner.LearnerConfig;
import org.epnoi.learner.helper.LearnerHelper;
import org.epnoi.learner.service.LearnerService;
import org.epnoi.learner.service.LearnerTask;
import org.epnoi.model.Event;
import org.epnoi.model.domain.relations.Contains;
import org.epnoi.model.domain.relations.Relation;
import org.epnoi.model.domain.resources.Document;
import org.epnoi.model.domain.resources.Domain;
import org.epnoi.model.domain.resources.Resource;
import org.epnoi.model.modules.EventBus;
import org.epnoi.model.modules.RoutingKey;
import org.epnoi.storage.UDM;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by cbadenes on 21/02/16.
 */
@Category(IntegrationTest.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = LearnerConfig.class)
@ActiveProfiles("develop")
@TestPropertySource(properties = {
        "learner.task.terms.extract = true",
        "learner.task.terms.store = false",
        "learner.task.relations.parallel = true",
        "learner.task.delay = 1000",
        "epnoi.cassandra.contactpoints = drinventor.dia.fi.upm.es",
        "epnoi.cassandra.port = 5011",
        "epnoi.cassandra.keyspace = research",
        "epnoi.elasticsearch.contactpoints = drinventor.dia.fi.upm.es",
        "epnoi.elasticsearch.port = 5021",
        "epnoi.neo4j.contactpoints = drinventor.dia.fi.upm.es",
        "epnoi.neo4j.port = 5030",
        "epnoi.eventbus.host = drinventor.dia.fi.upm.es"
})
public class LearnerTaskTest {

    private static final Logger LOG = LoggerFactory.getLogger(LearnerTaskTest.class);

    @Autowired
    LearnerService service;

    @Autowired
    UDM udm;

    @Autowired
    EventBus eventBus;

    @Autowired
    LearnerHelper helper;

    @Test
    public void fromEvent() throws InterruptedException {

        Contains contains = Relation.newContains("http://drinventor.eu/domains/7df34748-7fad-486e-a799-3bcd86a03499","http://drinventor.eu/documents/c369c917fecf3b4828688bdb6677dd6e");


        LOG.info("sending post event...");
        eventBus.post(Event.from(contains), RoutingKey.of(Relation.Type.CONTAINS,Relation.State.CREATED));

//        service.buildModels(domain);


        LOG.info("Sleeping .. ");
        Thread.sleep(300000);
        LOG.info("Wake up! ");

    }


    @Test
    public void fromDirectReference() throws InterruptedException {

        String domainUri = "http://drinventor.eu/domains/7df34748-7fad-486e-a799-3bcd86a03499";

        LearnerTask task = new LearnerTask(domainUri,helper);
        task.run();

    }

}
