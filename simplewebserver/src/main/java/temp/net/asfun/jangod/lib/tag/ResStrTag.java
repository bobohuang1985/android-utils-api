package net.asfun.jangod.lib.tag;

/**
 * {% rstr 'strName' %}
 * {% rstr var_strName %}
 * @author join
 */
public class ResStrTag extends AbsResTag {

    final String TAGNAME = "rstr";

    @Override
    public String getEndTagName() {
        return null;
    }

    @Override
    public String getName() {
        return TAGNAME;
    }

    @Override
    public String getValue(String name) {
        int id = getIdentifier(name, "string");
        return app.getString(id);
    }

}
