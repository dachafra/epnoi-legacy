package org.epnoi.learner;

import es.cbadenes.lab.test.IntegrationTest;
import org.epnoi.learner.modules.Learner;
import org.epnoi.learner.modules.Trainer;
import org.epnoi.learner.service.rest.TrainerResource;
import org.epnoi.model.Relation;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
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

    @Autowired
    TrainerResource trainerResource;


    @Autowired
    LearningParameters learnerProperties;

    @Value("${learner.demo.harvester.uri}")
    String domainUri;



    @Test
    public void train() {
        System.out.println("Starting an ontology learning task for " + domainUri);
        System.out.println("Using the following parameters "+learnerProperties);


        // Create corpus
        Response res1 = trainerResource.createDemoData();
        System.out.println("Create Demo Data response: " + res1);

        //

        String uri = (String) res1.getEntity();
        int corpusMaxSize = 10;
        System.out.println("Uri: " + uri);
        System.out.println("Corpus Max Size: " + corpusMaxSize);
        Response res2 = trainerResource.createRelationalSentenceCorpus(corpusMaxSize, uri);
        System.out.println("Create Relational Sentence Corpus response: " + res2);


        String modelPath = "/opt/epnoi/out";
        Response res3 = trainerResource.createRelationalPatternsModel(modelPath);
        System.out.println("Create Relational Patterns Model response: " + res3);


        assert (true);

    }

}
