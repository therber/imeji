/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.vo.predefinedMetadata;

import java.net.URI;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.j2j.annotations.j2jDataType;
import de.mpg.j2j.annotations.j2jId;
import de.mpg.j2j.annotations.j2jLiteral;
import de.mpg.j2j.annotations.j2jResource;

/**
 * {@link Metadata} for number values
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
@j2jResource("http://imeji.org/terms/metadata")
@j2jDataType("http://imeji.org/terms/metadata#number")
@j2jId(getMethod = "getId", setMethod = "setId")
@XmlRootElement(name = "number", namespace = "http://imeji.org/terms/metadata#number")
public class Number extends Metadata
{
    @j2jLiteral("http://imeji.org/terms/number")
    private double number = Double.NaN;
    @j2jResource("http://imeji.org/terms/statement")
    private URI statement;

    public Number()
    {
    }

    public Number(double value)
    {
        setNumber(value);
    }

    public void setNumber(double number)
    {
        this.number = number;
    }

    @XmlElement(name = "number", namespace="http://imeji.org/terms/number")
    public double getNumber()
    {
        return number;
    }

    @Override
    @XmlElement(name = "statement", namespace="http://imeji.org/terms/statement")
    public URI getStatement()
    {
        return statement;
    }

    @Override
    public void setStatement(URI namespace)
    {
        this.statement = namespace;
    }

    @Override
    public void copy(Metadata metadata)
    {
        if (metadata instanceof Number)
        {
            this.number = ((Number)metadata).getNumber();
            this.statement = metadata.getStatement();
        }
    }

    @Override
    public String asFulltext()
    {
        return Double.toString(number);
    }
}
