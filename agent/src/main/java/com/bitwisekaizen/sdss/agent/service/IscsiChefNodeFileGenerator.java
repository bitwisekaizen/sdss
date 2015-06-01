package com.bitwisekaizen.sdss.agent.service;

import com.bitwisekaizen.sdss.agentclient.AccessibleIscsiTarget;
import com.bitwisekaizen.sdss.agentclient.IscsiTarget;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * Generate the Chef solo node json file used for creating/deleting ISCSI targets.
 */
@Component
public class IscsiChefNodeFileGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(IscsiChefNodeFileGenerator.class);

    /**
     * Generate the chef solo node json file for creating/deleting the specified targets.
     *
     * @param iscsiTargets list of targets to create/delete.
     * @return node json file.
     */
    public File generateFile(List<AccessibleIscsiTarget> iscsiTargets) {
        Map<String, Object> scopes = new HashMap<>();
        scopes.put("decoratedIscsiTargets", convert(iscsiTargets));

        MustacheFactory mustacheFactory = new DefaultMustacheFactory();
        Mustache mustache = mustacheFactory.compile("node.mustache");

        try {
            File outputFile = getOutputFile();
            writeOutMustacheScopesToFile(mustache, scopes, outputFile);
            return outputFile;
        } catch (IOException e) {
            throw new UnhandledException(e);
        }
    }

    private DecoratedList convert(List<AccessibleIscsiTarget> accessibleIscsiTargets) {
        List<AccessibleIscsiTarget> newAccessibleIscsiTargets = new ArrayList<>();

        for (AccessibleIscsiTarget accessibleIscsiTarget : accessibleIscsiTargets) {
            List<String> decoratedIqns = new DecoratedList(
                    accessibleIscsiTarget.getIscsiTarget().getHostIscsiQualifiedNames());
            IscsiTarget oldIscsiTarget = accessibleIscsiTarget.getIscsiTarget();
            IscsiTarget newIscsiTarget = new IscsiTarget(decoratedIqns,
                    oldIscsiTarget.getCapacityInMb(), oldIscsiTarget.getTargetName());
            newAccessibleIscsiTargets.add(
                    new AccessibleIscsiTarget(newIscsiTarget, accessibleIscsiTarget.getStorageNetworkAddresses()));
        }
        DecoratedList decoratedList = new DecoratedList(newAccessibleIscsiTargets);

        return decoratedList;
    }

    private File getOutputFile() throws IOException {
        File nodeJsonOutput = File.createTempFile("agent-node", ".json");
        nodeJsonOutput.deleteOnExit();
        return nodeJsonOutput;
    }

    private void writeOutMustacheScopesToFile(Mustache mustache, Map<String, Object> scopes, File outputFile)
            throws IOException {
        LOGGER.info("Writing node json File to out to " + outputFile.getAbsolutePath());
        Writer writer = null;
        try {
            writer = new FileWriter(outputFile);
            mustache.execute(writer, scopes);
        } finally {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        }
    }

    private static class DecoratedList<T> extends ArrayList<DecoratedList.Element<T>> implements List<DecoratedList.Element<T>> {
        public DecoratedList(List<T> inputList) {
            for (int index = 0; index < inputList.size(); index++) {
                boolean first = false;
                boolean last = false;

                if (index == 0) {
                    first = true;
                }

                if (index == inputList.size() -1) {
                    last = true;
                }

                add(new Element<>(index, first, last, inputList.get(index)));
            }
        }

        public static class Element<T> {
            public final int index;
            public final boolean first;
            public final boolean last;
            public final T value;

            public Element(int index, boolean first, boolean last, T value) {
                this.index = index;
                this.first = first;
                this.last = last;
                this.value = value;
            }
        }
    }
}
