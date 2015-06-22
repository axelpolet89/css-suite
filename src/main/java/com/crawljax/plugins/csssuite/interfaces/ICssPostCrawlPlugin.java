package com.crawljax.plugins.csssuite.interfaces;

import com.crawljax.plugins.csssuite.data.MCssFile;
import com.crawljax.plugins.csssuite.plugins.analysis.MatchedElements;

import java.util.Map;

/**
 * Created by axel on 5/18/2015.
 */
public interface ICssPostCrawlPlugin
{
    public Map<String, MCssFile> Transform(Map<String, MCssFile> cssRules, MatchedElements matchedElements);
}