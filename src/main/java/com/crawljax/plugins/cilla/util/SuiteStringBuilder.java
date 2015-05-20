package com.crawljax.plugins.cilla.util;

/**
 * Created by axel on 5/19/2015.
 */
public class SuiteStringBuilder
{
    private StringBuilder _builder;

    public SuiteStringBuilder()
    {
        _builder = new StringBuilder();
    }

    public void append(String string)
    {
        _builder.append(string);
    }

    public void appendLine(String string)
    {
        _builder.append("\n" + string);
    }

    @Override
    public String toString()
    {
        return _builder.toString();
    }
}
