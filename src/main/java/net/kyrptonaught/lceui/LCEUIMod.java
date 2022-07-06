package net.kyrptonaught.lceui;

import net.fabricmc.api.ClientModInitializer;
import net.kyrptonaught.kyrptconfig.config.ConfigManager;
import net.kyrptonaught.lceui.whatsThis.WhatsThisInit;


public class LCEUIMod implements ClientModInitializer {
    public static final String MOD_ID = "lceui";

    public static ConfigManager configManager = new ConfigManager.MultiConfigManager(MOD_ID);

    @Override
    public void onInitializeClient() {
        configManager.load();
        WhatsThisInit.init();
    }
}