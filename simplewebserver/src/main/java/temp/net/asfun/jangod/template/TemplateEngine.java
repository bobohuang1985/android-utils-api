/**********************************************************************
Copyright (c) 2010 Asfun Net.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
**********************************************************************/
package net.asfun.jangod.template;

import static net.asfun.jangod.util.logging.JangodLogger;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import net.asfun.jangod.base.Application;
import net.asfun.jangod.base.Configuration;
import net.asfun.jangod.cache.ConcurrentHashPool;
import net.asfun.jangod.cache.StatefulObjectPool;

public class TemplateEngine {

    StatefulObjectPool<Processor> pool;
    Application application;

    public TemplateEngine() {
        application = new Application();
        initProcessorPool();
    }

    public TemplateEngine(Application application) {
        this.application = application;
        initProcessorPool();
    }

    /*
     * Using ConcurrentHashPool instead of ConcurrentListPool.
     * Caused by: ConcurrentSkipListMap is added in API level 9.
     * Modified by: join.
     */
    @SuppressWarnings("unchecked")
    protected void initProcessorPool() {
        String poolClass = application.getConfiguration().getProperty("processor.pool");
        if (poolClass == null) {
            pool = new ConcurrentHashPool<Processor>();
        } else {
            try {
                pool = (StatefulObjectPool<Processor>) Class.forName(poolClass).newInstance();
            } catch (Exception e) {
                pool = new ConcurrentHashPool<Processor>();
                JangodLogger.warning("Can't instance processor pool(use default) >>> " + poolClass);
            }
        }
    }

    public void setEngineBindings(Map<String, Object> bindings) {
        if (bindings == null) {
            application.getGlobalBindings().clear();
        } else {
            application.setGlobalBindings(bindings);
        }
    }

    public String process(String templateFile, Map<String, Object> bindings) throws IOException {
        Processor processor = pool.pop();
        if (processor == null) {
            processor = new Processor(application);
        }
        String result = processor.render(templateFile, bindings);
        pool.push(processor);
        return result;
    }

    public void process(String templateFile, Map<String, Object> bindings, Writer out)
            throws IOException {
        Processor processor = pool.pop();
        if (processor == null) {
            processor = new Processor(application);
        }
        processor.render(templateFile, bindings, out);
        pool.push(processor);
    }

    public Configuration getConfiguration() {
        return application.getConfiguration();
    }
}
