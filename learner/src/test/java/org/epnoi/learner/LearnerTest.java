package org.epnoi.learner;

import es.cbadenes.lab.test.IntegrationTest;
import org.epnoi.learner.helper.LearnerHelper;
import org.epnoi.model.Paper;
import org.epnoi.model.Term;
import org.epnoi.model.domain.resources.Domain;
import org.epnoi.model.domain.resources.Resource;
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

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by rgonzalez on 3/12/15.
 * Modified by dachafra on 4/4/16.
 */
//@Category(IntegrationTest.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = LearnerConfig.class)
@ActiveProfiles("develop")
@TestPropertySource(properties = {"learner.task.terms.extract = true", "learner.task.terms.store = true", "learner.task.relations.parallel = true"})


public class LearnerTest {

    private static final Logger LOG = LoggerFactory.getLogger(LearnerTest.class);

    @Autowired
    LearnerHelper helper;

    @Autowired
    LearningParameters learnerProperties;

    @Test
    public void LearnerTest() {
        FileWriter file = null;
        PrintWriter pw = null;
        System.out.println("Starting an ontology learning test");
        System.out.println("Using the following parameters "+learnerProperties);

        try
        {
            file = new FileWriter("/home/dchaves/TFM/salidas/out.txt");
            pw = new PrintWriter(file);
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        helper.getDemoDataLoader().erase();
        Domain domain = Resource.newDomain();
        domain.setUri("http://epnoi.org/domains/sample");
        domain.setName("sample-domain");


        LOG.info("Loading data");

        helper.getDemoDataLoader().loadDomain(domain.getUri(), domain.getName(), loadPapers());


        LOG.info("Learning terms and relations from domain: " + domain + "src/main");
        helper.getLearner().learn(domain.getUri());


        LOG.info("Retrieving terms from domain..");
        List<Term> terms = new ArrayList<>(helper.getLearner().retrieveTerminology(domain.getUri()).getTerms());
        if ((terms == null) || (terms.isEmpty())){
            LOG.warn("No terms found in domain: " + domain.getUri());
            return;
        }
        LOG.info("Number of terms found in domain: " + terms.size());

        for(int i=0; i<terms.size();i++){
           pw.println(terms.get(i).getAnnotatedTerm().getWord()+": "+terms.get(i).getAnnotatedTerm().getAnnotation().getTermhood());
        }


        LOG.info("Retrieving relations from domain..");
        List<org.epnoi.model.Relation> relations = new ArrayList<>(helper.getLearner().retrieveRelations(domain.getUri()).getRelations());
        if ((relations == null) || (relations.isEmpty())){
            LOG.warn("No relations found in domain: " + domain.getUri());
            relations = new ArrayList<>();
        }
        for(int i=0; i<relations.size();i++){
            pw.println(relations.get(i).getSource());
        }

        LOG.info("Number of relations found in domain: " + relations.size());

        try {
            file.close();
        } catch (Exception ex) {
            System.out.println("Error: "+ex.getMessage());
        }
        assert (true);

    }

    private List<Paper> loadPapers(){
        List<Paper> papers=helper.getFilesystemHarvester().harvest("/home/dchaves/TFM/documents");
        for(int i=0; i<papers.size();i++)
            helper.getFilesystemHarvester().addPaper(papers.get(i));
        return papers;
    }

}
