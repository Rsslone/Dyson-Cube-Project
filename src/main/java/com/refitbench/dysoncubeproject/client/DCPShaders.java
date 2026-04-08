package com.refitbench.dysoncubeproject.client;

/**
 * Holds compiled shader programs. Null if loading failed.
 */
public class DCPShaders {
    public static DCPShaderHelper HOLOGRAM;
    public static DCPShaderHelper HOLO_HEX;
    public static DCPShaderHelper DYSON_SUN;
    public static DCPShaderHelper RAIL_ELECTRIC;
    public static DCPShaderHelper RAIL_BEAM;

    public static void loadAll() {
        HOLOGRAM = DCPShaderHelper.load("hologram");
        HOLO_HEX = DCPShaderHelper.load("holo_hex");
        DYSON_SUN = DCPShaderHelper.load("dyson_sun");
        RAIL_ELECTRIC = DCPShaderHelper.load("rail_electric");
        RAIL_BEAM = DCPShaderHelper.load("rail_beam");
    }
}
