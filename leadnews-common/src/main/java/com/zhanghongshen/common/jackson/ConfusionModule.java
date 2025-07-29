package com.zhanghongshen.common.jackson;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;

public class ConfusionModule extends Module {

    private final static String MODULE_NAME = "jackson-confusion-encryption";
    private final static Version VERSION = new Version(1,0,0,null,"zhanghognshen",MODULE_NAME);

    @Override
    public void setupModule(com.fasterxml.jackson.databind.Module.SetupContext context) {
        context.addBeanSerializerModifier(new ConfusionSerializerModifier());
        context.addBeanDeserializerModifier(new ConfusionDeserializerModifier());
    }

    @Override
    public String getModuleName() {
        return MODULE_NAME;
    }

    @Override
    public Version version() {
        return VERSION;
    }

}
