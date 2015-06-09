package com.crawljax.plugins.csssuite.plugins.sass;

import com.crawljax.plugins.csssuite.data.MSelector;
import com.crawljax.plugins.csssuite.data.properties.MProperty;
import com.crawljax.plugins.csssuite.util.SuiteStringBuilder;
import com.steadystate.css.parser.media.MediaQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by axel on 6/8/2015.
 */
public class SassSelector
{
    private String _selectorText;
    private MSelector _original;

    private List<MProperty> _properties;
    private List<SassTemplate> _extensions;

    public SassSelector(MSelector original)
    {
        _original = original;

        _selectorText = original.GetSelectorText();
        _properties = original.GetProperties();

        _extensions = new ArrayList<>();
    }

    public void AddExtension(SassTemplate sassTemplate)
    {
        _extensions.add(sassTemplate);
    }

    public void PrintContents(SuiteStringBuilder builder, String prefix)
    {
        for(SassTemplate sassTemplate : _extensions)
        {
            builder.appendLine("%s\t@extend: %%extend_%d;", prefix, sassTemplate.GetNumber());
        }

        for(MProperty mProperty : _properties)
        {
            builder.appendLine("%s\t%s", prefix, mProperty);
        }
    }

    public String GetSelectorText()
    {
        return _selectorText;
    }

    public int GetRuleNumber()
    {
        return _original.GetRuleNumber();
    }

    public List<MediaQuery> GetMediaQueries() { return _original.GetMediaQueries();}

    public boolean HasEqualDeclarationsByText(SassSelector other)
    {
        List<String> sorted = GetSortedProperties();
        List<String> otherSorted = other.GetSortedProperties();

        if(sorted.size() != otherSorted.size())
        {
            return false;
        }

        for(int i = 0; i < sorted.size(); i++)
        {
            if(!sorted.get(i).equals(otherSorted.get(i)))
            {
                return false;
            }
        }

        return true;
    }

    public List<String> GetSortedProperties()
    {
        List<String> result = _extensions.stream().sorted((e1, e2) -> Integer.compare(e1.GetNumber(), e2.GetNumber())).map(e -> e.toString()).collect(Collectors.toList());
        result.addAll(_properties.stream().sorted((p1, p2) -> p1.toString().compareTo(p2.toString())).map(p -> p.toString()).collect(Collectors.toList()));
        return result;
    }
}
