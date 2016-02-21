package org.epnoi.learner;

import es.cbadenes.lab.test.IntegrationTest;
import org.apache.commons.lang3.StringUtils;
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

    private static Integer MAX_HEADER = 20;

    private static final Logger LOG = LoggerFactory.getLogger(SimpleTest.class);

    @Autowired
    DemoResource demoResource;

    @Autowired
    TrainerResource trainerResource;

    @Autowired
    LearnerResource learnerResource;

    @Value("${learner.demo.harvester.uri}")
    String domainUri;

//    @Value("${learner.corpus.sentences.maxlength}")
//    Integer maxLength;



    @Test
    public void train() {
        LOG.info("Starting an ontology learning task for " + domainUri);

        // Clean previous data
        head("Cleaning previous data: " + domainUri);
        Response res = demoResource.removeDemoData();
        LOG.info("Response of cleaning: " + res);

        // Read papers
        head("Harvesting files from ftp folder for domain: " + domainUri);
        Response res1 = demoResource.createDemoData();
        LOG.info("Response of harvestring: " + res1);

        head("Creating demo data from trainer: " + domainUri);
        Response res5 = trainerResource.createDemoData();
        LOG.info("Response of create demo from trainer: " + res5);

        head("Creating a relational sentences corpus ...");
        String relDomain = domainUri + "/relational-corpus";
        int maxLength = 200;
        Response res4 = trainerResource.createRelationalSentenceCorpus(maxLength, domainUri);
        LOG.info("Create Relational Sentence Corpus response: " + res4);

        // Create model.bin
        String modelPath = "/opt/epnoi/epnoideployment/secondReviewResources/lexicalModel/model.bin";
        head("Creating a relational patterns model ...");
        Response res3 = trainerResource.createRelationalPatternsModel(modelPath);
        LOG.info("Create Relational Patterns Model response: " + res3);


        // Create corpus
        head("Learning domain ..");
        Response res2 = learnerResource.learnDomain(relDomain);
        LOG.info("Learn response: " + res2);


        head("Getting relations..");
        Response relations = learnerResource.getDomainRelations(domainUri);
        LOG.info("Relations discovered: " +relations.getEntity());

        head("Getting terms..");
        Response terms = learnerResource.getDomainTerminology(domainUri);
        LOG.info("Terms discovered: " +terms.getEntity());

        assert (true);

    }


    private void head(String title){
        LOG.info(StringUtils.repeat("#",50));
        LOG.info(StringUtils.repeat("#",20) + "   " + title);
        LOG.info(StringUtils.repeat("#",50));
    }

}
