package com.crawljax.plugins.csssuite.sass;

import com.crawljax.plugins.csssuite.util.SuiteStringBuilder;
import com.steadystate.css.dom.AbstractCSSRuleImpl;

/**
 * Created by axel on 6/15/2015.
 */
public class SassIgnoredRule extends SassRuleBase
{
    private final AbstractCSSRuleImpl _rule;

    public SassIgnoredRule(int lineNumber, AbstractCSSRuleImpl rule)
    {
        super(lineNumber);

        _rule = rule;
    }

    public void Print(SuiteStringBuilder builder, String prefix)
    {
        builder.append("%s%s", prefix, _rule);
    }
}