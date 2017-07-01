package net.asfun.jangod.lib.tag;

/**
 * {% rcolor 'strName' %}
 * {% rcolor var_strName %}
 * @author join
 */
public class ResColorTag extends AbsResTag {

    final String TAGNAME = "rcolor";

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
        int id = getIdentifier(name, "color");
        return (String) app.getResources().getText(id);
    }

}
