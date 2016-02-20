package org.epnoi.learner;

import es.cbadenes.lab.test.IntegrationTest;
import org.epnoi.learner.filesystem.FilesystemHarvester;
import org.epnoi.learner.service.rest.TrainerResource;
import org.epnoi.model.Paper;
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
import java.io.File;
import java.util.List;

/**
 * Created by rgonzalez on 3/12/15.
 */
@Category(IntegrationTest.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = LearnerConfig.class)
@ActiveProfiles("develop")
@TestPropertySource(properties = {"learner.task.terms.extract = false", "learner.task.terms.store = false", "learner.task.relations.parallel = true"})
public class HarvesterTest {

    private static final Logger LOG = LoggerFactory.getLogger(HarvesterTest.class);

    @Autowired
    FilesystemHarvester harvester;

    @Test
    public void byFile() {

        String filePath = "src/test/resources/2002/p1169-wang.pdf";

        File file = new File(filePath);

        if (!file.exists()){
            LOG.error("File not exists! " + file.getAbsolutePath());
            System.exit(-1);
        }

        String fileName = "The Cartoon animation filter";

        Paper paper = harvester._harvestFile(file.getAbsolutePath(), fileName);

        LOG.info("Paper: " + paper);
    }


    @Test
    public void byFolder(){

        String folderPath = "src/test/resources";

        File file = new File(folderPath);

        if (!file.exists()){
            LOG.error("File not exists! " + file.getAbsolutePath());
            System.exit(-1);
        }

        List<Paper> papers = harvester.harvest(file.getAbsolutePath());

        papers.forEach(paper -> LOG.info("Paper: " + papers));

    }

}
