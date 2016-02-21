package org.epnoi.learner.scheduler;

import es.cbadenes.lab.test.IntegrationTest;
import org.epnoi.learner.LearnerConfig;
import org.epnoi.learner.service.LearnerService;
import org.epnoi.model.domain.resources.Domain;
import org.epnoi.model.domain.resources.Resource;
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
        "learner.task.delay = 1000"})
public class LearnerTaskTest {

    private static final Logger LOG = LoggerFactory.getLogger(LearnerTaskTest.class);

    @Autowired
    LearnerService service;

    @Autowired
    UDM udm;

    @Test
    public void storeInUdm() throws InterruptedException {

        Domain domain = Resource.newDomain();
        domain.setName("siggraph");
        domain.setDescription("from-test-junit");
        udm.save(domain);


        service.buildModels(domain);


        LOG.info("Sleeping .. ");
        Thread.sleep(300000);
        LOG.info("Wake up! ");

    }

}
