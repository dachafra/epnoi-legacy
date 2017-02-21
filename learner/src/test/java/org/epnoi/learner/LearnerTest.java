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

        for(int i=1; i<52; i++) {
            FileWriter file = null;
            PrintWriter pw = null;
            System.out.println("Starting an ontology learning test");
            //System.out.println("Using the following parameters "+learnerProperties);

            try {
                if(i>1) {
                    File srcDir = new File("/home/dchaves/corpus/vadim/D"+i);
                    File destDir = new File("/home/dchaves/corpus/vadim/D1");
                    FileUtils.copyDirectory(srcDir, destDir);
                }
                file = new FileWriter("/home/dchaves/corpus/salidas/D"+i+".txt");
                pw = new PrintWriter(file);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            helper.getDemoDataLoader().erase();
            Domain domain = Resource.newDomain();
            domain.setUri("http://epnoi.org/domains/sample");
            domain.setName("sample-domain");


            LOG.info("Loading data");


            helper.getDemoDataLoader().loadDomain(domain.getUri(), domain.getName(), loadPapers());


            LOG.info("Learning terms and relations from domain: " + domain + "src/main");

            List<Term> terms = new ArrayList<>(helper.getLearner().learn(domain.getUri()).getTerms());

            if (terms != null) {
                System.out.println("Number of terms found in domain: " + terms.size());
                if (terms.size() <= 0) {
                    System.out.println("No terms found in domain: " + domain.getUri());
                    return;
                }


                Collections.sort(terms, new Term());
                for(int j=0; j<terms.size(); j++){
                    for(int q=0; q< terms.size();q++){
                        if(j!=q && terms.get(j).getAnnotatedTerm().getWord().equals(terms.get(q).getAnnotatedTerm().getWord())){
                            terms.remove(q);
                            q--;
                        }
                    }
                }
                pw.println("Terms; C-Value");
                for (Term term : terms) {
                    pw.println(term.getAnnotatedTerm().getWord() + ";" + term.getAnnotatedTerm().getAnnotation().getCValue());
                }

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
            } else {
                System.out.println("Terms=null");
            }

            try {
                file.close();
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }

        }
        assert (true);

    }


    private List<Paper> loadPapers(){
        return helper.getFilesystemHarvester().harvest("/home/dchaves/corpus/vadim/D1");
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
