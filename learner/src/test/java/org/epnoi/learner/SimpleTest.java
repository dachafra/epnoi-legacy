package org.epnoi.learner;

import es.cbadenes.lab.test.IntegrationTest;
import org.epnoi.learner.modules.Learner;
import org.epnoi.learner.modules.Trainer;
import org.epnoi.learner.service.rest.DemoResource;
import org.epnoi.learner.service.rest.LearnerResource;
import org.epnoi.learner.service.rest.TrainerResource;
import org.epnoi.model.Relation;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.ws.rs.core.Response;

/**
 * Created by rgonzalez on 3/12/15.
 */
@Category(IntegrationTest.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = LearnerConfig.class)
@ActiveProfiles("develop")
@TestPropertySource(properties = {"learner.task.terms.extract = false", "learner.task.terms.store = false", "learner.task.relations.parallel = true"})
public class SimpleTest {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleTest.class);

    @Autowired
    DemoResource demoResource;

    @Autowired
    TrainerResource trainerResource;

    @Autowired
    LearnerResource learnerResource;

    @Value("${learner.demo.harvester.uri}")
    String domainUri;



    @Test
    public void train() {
        LOG.info("Starting an ontology learning task for " + domainUri);

        // Clean previous data
        LOG.info("Cleaning previous data: " + domainUri);
        Response res = demoResource.removeDemoData();
        LOG.info("Response of cleaning: " + res);

        // Read papers
        LOG.info("Harvesting files from ftp folder for domain: " + domainUri);
        Response res1 = demoResource.createDemoData();
        LOG.info("Response of harvestring: " + res1);


        // Create model.bin
        String modelPath = "/opt/epnoi/epnoideployment/secondReviewResources/lexicalModel/model3.bin";
        LOG.info("Creating relational sentences corpus ...");
        Response res3 = trainerResource.createRelationalPatternsModel(modelPath);
        LOG.info("Create Relational Patterns Model response: " + res3);


        // Create corpus
        LOG.info("Learning domain: ");
        Response res2 = learnerResource.learnDomain(domainUri);
        LOG.info("Learn response: " + res2);

        int corpusMaxSize = 10;
        LOG.info("Creating relational sentences corpus ...");
        Response res4 = trainerResource.createRelationalSentenceCorpus(corpusMaxSize, domainUri);
        LOG.info("Create Relational Sentence Corpus response: " + res4);









        assert (true);

    }

}
