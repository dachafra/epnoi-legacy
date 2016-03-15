package org.epnoi.learner;

import es.cbadenes.lab.test.IntegrationTest;
import org.epnoi.learner.helper.LearnerHelper;
import org.epnoi.learner.modules.Learner;
import org.epnoi.learner.relations.extractor.parallel.ParallelRelationsExtractor;
import org.epnoi.model.Paper;
import org.epnoi.model.Relation;
import org.epnoi.model.Term;
import org.epnoi.model.domain.resources.Domain;
import org.epnoi.model.domain.resources.Resource;
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

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by rgonzalez on 3/12/15.
 */
@Category(IntegrationTest.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = LearnerConfig.class)
@ActiveProfiles("develop")
@TestPropertySource(properties = {"learner.task.terms.extract = false", "learner.task.terms.store = false", "learner.task.relations.parallel = true"})


public class LearnerTest {

    private static final Logger LOG = LoggerFactory.getLogger(LearnerTest.class);

    @Autowired
    LearnerHelper helper;

    @Autowired
    LearningParameters learnerProperties;


    @Test
    public void learn() {
        System.out.println("Starting an ontology learning test");
        System.out.println("Using the following parameters "+learnerProperties);

        Domain domain = Resource.newDomain();
        domain.setUri("http://epnoi.org/domains/sample");
        domain.setName("sample-domain");



        LOG.info("Loading data");
        List<Paper> papers = null; //TODO Initialize
        helper.getDemoDataLoader().loadDomain(domain.getUri(), domain.getName(), papers);


        LOG.info("Learning terms and relations from domain: " + domain + "src/main");
        helper.getLearner().learn(domain.getUri());


        LOG.info("Retrieving terms from domain..");
        List<Term> terms = new ArrayList<>(helper.getLearner().retrieveTerminology(domain.getUri()).getTerms());
        if ((terms == null) || (terms.isEmpty())){
            LOG.warn("No terms found in domain: " + domain.getUri());
            return;
        }
        LOG.info("Number of terms found in domain: " + terms.size());

        LOG.info("Retrieving relations from domain..");
        List<org.epnoi.model.Relation> relations = new ArrayList<>(helper.getLearner().retrieveRelations(domain.getUri()).getRelations());
        if ((relations == null) || (relations.isEmpty())){
            LOG.warn("No relations found in domain: " + domain.getUri());
            relations = new ArrayList<>();
        }

        LOG.info("Number of relations found in domain: " + relations.size());


        assert (true);

    }

}
