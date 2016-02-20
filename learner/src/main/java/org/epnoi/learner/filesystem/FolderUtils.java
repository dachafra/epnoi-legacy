package org.epnoi.learner.filesystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cbadenes on 20/02/16.
 */
public class FolderUtils {

    private static final Logger LOG = LoggerFactory.getLogger(FolderUtils.class);


    public static List<String> listFiles(File directory) {

        List<String> files = new ArrayList<>();
        if (directory.isDirectory()) {
            search(directory,files);
        } else {
            LOG.warn(directory.getAbsoluteFile() + " is not a directory!");
        }
        return files;
    }

    private static void search(File file, List<String> files) {

        if (file.isDirectory()) {
            LOG.debug("Searching directory ... " + file.getAbsoluteFile());

            //do you have permission to read this directory?
            if (file.canRead()) {
                for (File temp : file.listFiles()) {
                    if (temp.isDirectory()) {
                        search(temp,files);
                    } else {
                        files.add(temp.getAbsoluteFile().toString());
                    }
                }
            } else {
                LOG.warn(file.getAbsoluteFile() + "Permission Denied");
            }
        }

    }

}