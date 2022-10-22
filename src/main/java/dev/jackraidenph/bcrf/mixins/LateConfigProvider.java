package dev.jackraidenph.bcrf.mixins;

import com.google.common.collect.Lists;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.List;

public class LateConfigProvider implements ILateMixinLoader {

    @Override
    public List<String> getMixinConfigs() {
        return Lists.newArrayList(
                "mixins.bcrf.json"
        );
    }
}
