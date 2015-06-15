package com.crawljax.plugins.csssuite.plugins.merge;

import com.crawljax.plugins.csssuite.CssSuiteException;
import com.crawljax.plugins.csssuite.LogHandler;
import com.crawljax.plugins.csssuite.data.MCssFile;
import com.crawljax.plugins.csssuite.data.MCssRule;
import com.crawljax.plugins.csssuite.data.MSelector;
import com.crawljax.plugins.csssuite.data.properties.MProperty;
import com.crawljax.plugins.csssuite.interfaces.ICssPostCrawlPlugin;

import java.lang.reflect.Array;
import java.util.*;


/**
 * Created by axel on 6/8/2015.
 *
 * This class is responsible for merging split-up properties into their shorthand equivalents
 */
public class NormalizeAndMergePlugin implements ICssPostCrawlPlugin
{
    @Override
    public Map<String, MCssFile> Transform(Map<String, MCssFile> cssRules)
    {
        for (String file : cssRules.keySet())
        {
            LogHandler.info("[CssNormalizer] Start normalization of properties for file '%s'", file);

            for(MCssRule mRule : cssRules.get(file).GetRules())
            {
                for(MSelector mSelector : mRule.GetSelectors())
                {
                    MergePropertiesToShorthand(mSelector);
                }
            }
        }

        return cssRules;
    }


    /**
     * Split any shorthand margin, padding, border, border-radius, outline and background property into parts
     * @param mSelector
     */
    private static void MergePropertiesToShorthand(MSelector mSelector)
    {
        List<MProperty> newProperties = new ArrayList<>();
        List<MProperty> properties = mSelector.GetProperties();

        List<MProperty> margins = new ArrayList<>();
        List<MProperty> paddings = new ArrayList<>();
        List<MProperty> borderRadii = new ArrayList<>();
        List<MProperty> border = new ArrayList<>();
        List<MProperty> borderTop = new ArrayList<>();
        List<MProperty> borderRight = new ArrayList<>();
        List<MProperty> borderBottom = new ArrayList<>();
        List<MProperty> borderLeft = new ArrayList<>();
        List<MProperty> outline = new ArrayList<>();
        List<MProperty> background = new ArrayList<>();

        Set<MProperty> borderStyles = new HashSet<>();
        Set<MProperty> borderColors = new HashSet<>();
        Set<MProperty> borderWidths = new HashSet<>();

        for(int i = 0; i < properties.size(); i++)
        {
            MProperty mProperty = properties.get(i);

            if(mProperty.IsIgnored())
                continue;

            final String name = mProperty.GetName();
            final String value = mProperty.GetValue();
            final boolean isImportant = mProperty.IsImportant();

            if(name.contains("margin"))
            {
                margins.add(mProperty);
            }
            else if (name.contains("padding"))
            {
                paddings.add(mProperty);
            }
            else if(name.contains("border"))
            {
                if(name.contains("radius"))
                {
                    borderRadii.add(mProperty);
                }
                else if(name.contains("top"))
                {
                    if(name.contains("style"))
                        borderStyles.add(mProperty);
                    else if(name.contains("width"))
                        borderWidths.add(mProperty);
                    else
                        borderColors.add(mProperty);

                    borderTop.add(mProperty);
                }
                else if (name.contains("right"))
                {
                    if(name.contains("style"))
                        borderStyles.add(mProperty);
                    else if(name.contains("width"))
                        borderWidths.add(mProperty);
                    else
                        borderColors.add(mProperty);

                    borderRight.add(mProperty);
                }
                else if (name.contains("bottom"))
                {
                    if(name.contains("style"))
                        borderStyles.add(mProperty);
                    else if(name.contains("width"))
                        borderWidths.add(mProperty);
                    else
                        borderColors.add(mProperty);

                    borderBottom.add(mProperty);
                }
                else if (name.contains("left"))
                {
                    if(name.contains("style"))
                        borderStyles.add(mProperty);
                    else if(name.contains("width"))
                        borderWidths.add(mProperty);
                    else
                        borderColors.add(mProperty);

                    borderLeft.add(mProperty);
                }
                else
                {
                    border.add(mProperty);
                }
            }
            else if(name.contains("outline"))
            {
                outline.add(mProperty);
            }
            else if(name.contains("background"))
            {
                background.add(mProperty);
            }
            else
            {
                newProperties.add(mProperty);
            }
        }

        newProperties.addAll(MergeBoxProperties(margins, new BoxMerger("margin")));
        newProperties.addAll(MergeBoxProperties(paddings, new BoxMerger("padding")));
        newProperties.addAll(MergeBorderProperties(border, new BorderMerger("border")));

        if(borderWidths.size() == (borderTop.size() + borderBottom.size() + borderLeft.size() + borderRight.size()))
        {
            newProperties.addAll(MergeBoxProperties(new ArrayList<>(borderWidths), new BoxMerger("border-width")));
        }
        else if (borderStyles.size() == (borderTop.size() + borderBottom.size() + borderLeft.size() + borderRight.size()))
        {
            newProperties.addAll(MergeBoxProperties(new ArrayList<>(borderStyles), new BoxMerger("border-style")));
        }
        else if (borderColors.size() == (borderTop.size() + borderBottom.size() + borderLeft.size() + borderRight.size()))
        {
            newProperties.addAll(MergeBoxProperties(new ArrayList<>(borderColors), new BoxMerger("border-color")));
        }
        else
        {
            newProperties.addAll(MergeBorderProperties(borderTop, new BorderSideMerger("border-top")));
            newProperties.addAll(MergeBorderProperties(borderRight, new BorderSideMerger("border-right")));
            newProperties.addAll(MergeBorderProperties(borderBottom, new BorderSideMerger("border-bottom")));
            newProperties.addAll(MergeBorderProperties(borderLeft, new BorderSideMerger("border-left")));
        }

        newProperties.addAll(MergeBoxProperties(borderRadii, new BorderRadiusMerger("border-radius")));
        newProperties.addAll(MergeBorderProperties(outline, new OutlineMerger("outline")));
        newProperties.addAll(MergeBorderProperties(background, new BackgroundMerger("background")));

        mSelector.SetNewProperties(newProperties);
    }


    /**
     *
     * @param properties
     * @param merger
     * @return
     */
    private static List<MProperty> MergeBoxProperties(List<MProperty> properties, MergerBase merger)
    {
        if(properties.size() == 0)
        {
            return new ArrayList<>();
        }

        if(properties.size() == 4)
        {
            List<MProperty> result = new ArrayList<>();
            for (MProperty mProperty : properties)
            {
                try
                {
                    merger.Parse(mProperty.GetName(), mProperty.GetValue(), mProperty.IsImportant());
                }
                catch (CssSuiteException e)
                {
                    result.add(mProperty);
                    LogHandler.warn("Cannot parse single property %s into shorthand equivalent, just add it to result", mProperty);
                }
            }

            result.addAll(merger.BuildMProperties());
            return result;
        }

        return properties;
    }


    /**
     *
     * @param properties
     * @param merger
     * @return
     */
    private static List<MProperty> MergeBorderProperties(List<MProperty> properties, MergerBase merger)
    {
        if (properties.size() == 0)
        {
            return new ArrayList<>();
        }

        if (properties.size() == 1)
        {
            return properties;
        }

        List<MProperty> result = new ArrayList<>();

        for (MProperty property : properties)
        {
            try
            {
                merger.Parse(property.GetName(), property.GetValue(), property.IsImportant());
            }
            catch (CssSuiteException e)
            {
                result.add(property);
                LogHandler.warn("Cannot parse single property %s into shorthand equivalent, just add it to result", property);
            }
        }

        result.addAll(merger.BuildMProperties());
        return result;
    }
}