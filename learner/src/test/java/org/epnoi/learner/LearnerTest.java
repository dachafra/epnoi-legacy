package org.epnoi.learner;

import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.util.*;
import org.apache.commons.io.FileUtils;
import org.epnoi.learner.filesystem.FolderUtils;
import org.epnoi.learner.helper.LearnerHelper;
import org.epnoi.model.Paper;
import org.epnoi.model.Term;
import org.epnoi.model.domain.resources.Domain;
import org.epnoi.model.domain.resources.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import es.cbadenes.lab.test.IntegrationTest;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
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
            ResourcesInfo resourcesInfo = new ResourcesInfo();
            FileWriter file = null;
            PrintWriter pw = null, pw2=null;
            System.out.println("Starting an ontology learning test");
            long startTime = System.currentTimeMillis();
            //System.out.println("Using the following parameters "+learnerProperties);

            try {
                file = new FileWriter("/home/dchaves/corpus/salidas/salida.txt");
                pw = new PrintWriter(file);
                pw2 = new PrintWriter(new FileOutputStream("/home/dchaves/corpus/times.csv"),true);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

           // helper.getDemoDataLoader().erase();

            Domain domain = Resource.newDomain();
            domain.setUri("http://epnoi.org/domains/sample");
            domain.setName("sample-domain");


            LOG.info("Loading data");


            helper.getDemoDataLoader().loadDomain(domain.getUri(), domain.getName(), loadPapers());


            LOG.info("Learning terms and relations from domain: " + domain + "src/main");

            List<Term> terms = new ArrayList<>(helper.getLearner().learn(domain.getUri()).getTerms());
            long memory = resourcesInfo.getCosumtionMemory();
            if (terms != null) {
                System.out.println("Number of terms found in domain: " + terms.size());
                if (terms.size() <= 0) {
                    System.out.println("No terms found in domain: " + domain.getUri());
                    return;
                }

                HashSet<Term> terms2  = new HashSet<>(terms);
                terms = new ArrayList(terms2);
                Collections.sort(terms, new Term());
                System.out.println(terms.size());
                String text="";
                pw.println("Terms;C-Value");
                for (Term term : terms) {
                    text=text+term.getAnnotatedTerm().getWord() + ";" + term.getAnnotatedTerm().getAnnotation().getCValue()+"\n";
                }
                pw.println(text);

            /*
            LOG.info("Retrieving relations from domain..");
            List<org.epnoi.model.Relation> relations = new ArrayList<>(helper.getLearner().retrieveRelations(domain.getUri()).getRelations());
            if ((relations == null) || (relations.isEmpty())) {
                LOG.warn("No relations found in domain: " + domain.getUri());
                relations = new ArrayList<>();
            }
            for (int i = 0; i < relations.size(); i++) {
                pw.println(relations.get(i).getSource());
            }

            LOG.info("Number of relations found in domain: " + relations.size());

            */
            }
             else {
                System.out.println("Terms=null");
            }
            long endTime = System.currentTimeMillis() - startTime;
            pw2.println(endTime+","+memory);

            try {
                file.close();
                pw2.close();
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }



        assert (true);

    }


    private List<Paper> loadPapers(){
        return helper.getFilesystemHarvester().harvest("/home/dchaves/corpus/vadim/");
    }


/*
    private void loadText(String text) {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        Annotation document = new Annotation(text);
        pipeline.annotate(document);
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                if (token.get(CoreAnnotations.PartOfSpeechAnnotation.class).startsWith("N")) {
                    String n = token.get(CoreAnnotations.TextAnnotation.class);
                    if (!nouns.contains(n))
                        nouns.add(n);
                }
            }
        }

    }
    */
}
