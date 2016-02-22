package org.epnoi.harvester.legacy.wikipedia;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by cbadenes on 20/02/16.
 */
public class FolderUtils {

    private static final Logger LOG = LoggerFactory.getLogger(FolderUtils.class);


    public static List<String> listPaths(File directory) {
        return listFiles(directory).stream().map(file -> file.getAbsolutePath()).collect(Collectors.toList());
    }


    public static List<File> listFiles(File directory) {

        List<File> files = new ArrayList<>();
        if (directory.isDirectory()) {
            search(directory,files);
        } else {
            LOG.warn(directory.getAbsoluteFile() + " is not a directory!");
        }
        return files;
    }

    private static void search(File file, List<File> files) {

        if (file.isDirectory()) {
            LOG.debug("Searching directory ... " + file.getAbsoluteFile());

            //do you have permission to read this directory?
            if (file.canRead()) {
                for (File temp : file.listFiles()) {
                    if (temp.isDirectory()) {
                        search(temp,files);
                    } else {
                        if (!temp.getName().startsWith(".")){
                            files.add(temp);
                        }
                    }
                }
            } else {
                LOG.warn(file.getAbsoluteFile() + "Permission Denied");
            }
        }

    }

}