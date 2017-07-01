package net.asfun.jangod.lib.tag;

import java.util.UUID;

import net.asfun.jangod.interpret.InterpretException;
import net.asfun.jangod.interpret.JangodInterpreter;
import net.asfun.jangod.lib.Tag;
import net.asfun.jangod.tree.NodeList;

/**
 * {% uuid %}
 * @author join
 */
public class UUIDTag implements Tag {

    final String TAGNAME = "uuid";

    @Override
    public String getEndTagName() {
        return null;
    }

    @Override
    public String getName() {
        return TAGNAME;
    }

    @Override
    public String interpreter(NodeList carries, String helpers, JangodInterpreter interpreter)
            throws InterpretException {
        return UUID.randomUUID().toString();
    }

}
