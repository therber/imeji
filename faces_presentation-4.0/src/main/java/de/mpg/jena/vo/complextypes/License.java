package de.mpg.jena.vo.complextypes;

import java.io.Serializable;
import java.text.SimpleDateFormat;

import thewebsemantic.Embedded;
import thewebsemantic.Namespace;
import thewebsemantic.RdfType;

import de.mpg.jena.vo.ComplexType;

@Namespace("http://imeji.mpdl.mpg.de/metadata/")
@RdfType("license")
@Embedded
public class License extends ComplexType implements Serializable
{
    private SimpleDateFormat date;
    private String dateFormat = "dd/mm/yyyy";

    public License()
    {
        super(ComplexTypes.LICENCE);
    }

    public License(SimpleDateFormat date)
    {
        super(ComplexTypes.LICENCE);
        this.date = date;
        date.applyPattern(dateFormat);
    }

    public String getDateString()
    {
        return date.format(date);
    }
}
