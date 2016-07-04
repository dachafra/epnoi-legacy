package org.epnoi.learner;

import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.util.*;
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
    private ArrayList<String> nouns = new ArrayList<>();

    @Test
    public void LearnerTest() {
        FileWriter file = null, file2=null;
        PrintWriter pw = null, pw2 =null;
        System.out.println("Starting an ontology learning test");
        System.out.println("Using the following parameters "+learnerProperties);

        try
        {
            file = new FileWriter("/home/dchaves/TFM/salidas/terms.txt");
            file2 = new FileWriter("/home/dchaves/TFM/salidas/nouns.txt");
            pw = new PrintWriter(file);
            pw2 = new PrintWriter(file2);
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        helper.getDemoDataLoader().erase();
        Domain domain = Resource.newDomain();
        domain.setUri("http://epnoi.org/domains/sample");
        domain.setName("sample-domain");


        LOG.info("Loading data");
       List<Paper> papers= loadPapers();
       helper.getDemoDataLoader().loadDomain(domain.getUri(), domain.getName(), papers);


        LOG.info("Learning terms and relations from domain: " + domain + "src/main");
        helper.getLearner().learn(domain.getUri());


        LOG.info("Retrieving terms from domain..");
        List<Term> terms = new ArrayList<>(helper.getLearner().retrieveTerminology(domain.getUri()).getTerms());
        List<Term> orderTerms = new ArrayList<>();
        if ((terms == null) || (terms.isEmpty())){
            LOG.warn("No terms found in domain: " + domain.getUri());
            return;
        }
        LOG.info("Number of terms found in domain: " + terms.size());
        boolean flag = true;
        for(String noun : nouns){
            for(Term term: terms){
                if(noun.equals(term.getAnnotatedTerm().getWord()) && !orderTerms.contains(term)){
                    if(term.getAnnotatedTerm().getWord().length()>1){
                        orderTerms.add(term);
                        flag=false;
                    }
                }
            }
            if(flag==true){
                pw2.println(noun);
            }
               flag=true;
        }

        Collections.sort(orderTerms, new Term());
        for(Term term : orderTerms){
            pw.println(term.getAnnotatedTerm().getWord()+";"+term.getAnnotatedTerm().getAnnotation().getTermhood());
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
            file2.close();
        } catch (Exception ex) {
            System.out.println("Error: "+ex.getMessage());
        }
        assert (true);

    }


    private List<Paper> loadPapers(){
        List<Paper> papers=helper.getFilesystemHarvester().harvest("/home/dchaves/TFM/documents/");
        loadText(papers);
        /*
        for(int i=0; i<papers.size();i++) {
           helper.getFilesystemHarvester().addPaper(papers.get(i));
        }*/
        return papers;
    }


    private void loadText(List<Paper> papers) {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        for(Paper paper : papers) {
            String text = paper.getDescription();
            Annotation document = new Annotation(text);
            pipeline.annotate(document);
            List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
            for (CoreMap sentence : sentences) {
                for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                    if (token.get(CoreAnnotations.PartOfSpeechAnnotation.class).matches("NN.")) {
                        String n = token.get(CoreAnnotations.TextAnnotation.class);
                        if (!nouns.contains(n))
                            nouns.add(n);
                    }
                }
            }
        }
    }
}
