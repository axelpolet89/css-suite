package com.crawljax.plugins.csssuite.generator;

import com.crawljax.plugins.csssuite.LogHandler;
import com.crawljax.plugins.csssuite.data.MCssRule;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by axel on 5/17/2015.
 */
public class CssWriter
{
    private final static Logger LOGGER = LogManager.getLogger("css.suite.logger");

    public void Generate(String fileName, List<MCssRule> rules) throws IOException, URISyntaxException
    {
        if(!fileName.contains(".css"))
            return;

        URI uri = new URI(fileName);

        String root = "output\\cssfiles\\" + uri.getAuthority().replace(uri.getScheme(), "");
        File file = new File(root + uri.getPath());
        File dir = new File((root + uri.getPath()).replace(file.getName(), ""));

        if(!dir.exists())
            dir.mkdirs();

        if(!file.exists())
            file.createNewFile();

        FileWriter writer = new FileWriter(file);

        Collections.sort(rules, new Comparator<MCssRule>() {
            @Override
            public int compare(MCssRule mCssRule, MCssRule t1) {
                return Integer.compare(mCssRule.GetLocator().getLineNumber(), t1.GetLocator().getLineNumber());
            }
        });

        int totalSelectors = 0;
        final int[] totalProperties = {0};

        for (MCssRule rule : rules)
        {
            totalSelectors += rule.GetSelectors().size();
             rule.GetSelectors().forEach((s) -> totalProperties[0] += s.GetProperties().size());
            writer.write(rule.Print());
        }

        LogHandler.info("File: " + fileName);
        LogHandler.info("# Selectors: %d", totalSelectors);
        LogHandler.info("# Properties: %d", totalProperties[0]);

        writer.flush();
        writer.close();
    }
}