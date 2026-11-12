package com.oidioomstudio.fasttree.client;

import com.oidioomstudio.fasttree.FastTreeMod;
import com.oidioomstudio.olib.client.OModMenuApi;

public class ModMenuIntegration extends OModMenuApi {
    @Override
    protected String getModId() {
        return FastTreeMod.MOD_ID;
    }
}
