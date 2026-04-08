package com.refitbench.dysoncubeproject.client.gui;

import com.refitbench.dysoncubeproject.Config;
import com.refitbench.dysoncubeproject.Reference;
import com.refitbench.dysoncubeproject.block.tile.RayReceiverTileEntity;
import com.refitbench.dysoncubeproject.network.ClientSubscribeSphereMessage;
import com.refitbench.dysoncubeproject.network.DCPNetworkHandler;
import com.refitbench.dysoncubeproject.util.NumberUtils;
import com.refitbench.dysoncubeproject.world.ClientDysonSphere;
import com.refitbench.dysoncubeproject.world.DysonSphereStructure;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.text.DecimalFormat;

public class RayReceiverGui extends GuiContainer {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/gui/ray_receiver.png");
    private static final int CYAN = 0xFF00FFFF;

    private final RayReceiverTileEntity tile;

    // Subscribe button bounds
    private static final int SUB_X = 9, SUB_Y = 84, SUB_W = 16, SUB_H = 16;

    public RayReceiverGui(RayReceiverContainer container) {
        super(container);
        this.tile = container.getTile();
        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1f, 1f, 1f, 1f);
        mc.getTextureManager().bindTexture(TEXTURE);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        // Energy bar (vertical, 16x52 at x=19, y=14)
        int maxEnergy = tile.getEnergyStorage().getMaxEnergyStored();
        if (maxEnergy > 0) {
            int stored = tile.getEnergyStorage().getEnergyStored();
            int barHeight = (int) (52 * ((float) stored / maxEnergy));
            drawRect(guiLeft + 19, guiTop + 14 + 52 - barHeight, guiLeft + 35, guiTop + 14 + 52, 0xFFCC0000);
        }

        // Subscribe button outline
        drawHorizontalLine(guiLeft + SUB_X, guiLeft + SUB_X + SUB_W, guiTop + SUB_Y, CYAN);
        drawHorizontalLine(guiLeft + SUB_X, guiLeft + SUB_X + SUB_W, guiTop + SUB_Y + SUB_H, CYAN);
        drawVerticalLine(guiLeft + SUB_X, guiTop + SUB_Y, guiTop + SUB_Y + SUB_H, CYAN);
        drawVerticalLine(guiLeft + SUB_X + SUB_W, guiTop + SUB_Y, guiTop + SUB_Y + SUB_H, CYAN);
        fontRenderer.drawString("S", guiLeft + SUB_X + 5, guiTop + SUB_Y + 4, CYAN);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        var sphere = ClientDysonSphere.DYSON_SPHERE_PROGRESS.getSpheres()
                .computeIfAbsent(tile.getDysonSphereId(), s -> new DysonSphereStructure());
        int x = 56;
        int y = 10;
        int lineH = fontRenderer.FONT_HEIGHT + 1;
        int color = 0x55FFFF;

        fontRenderer.drawString("Dyson Information", x, y, color);
        y += lineH;
        fontRenderer.drawString("Progress: " + new DecimalFormat().format(sphere.getProgress() * 100) + "%", x, y, color);
        y += lineH;
        fontRenderer.drawString("Power Gen: " + NumberUtils.getFormattedBigNumber((double) sphere.getSolarPanels() * Config.POWER_PER_SAIL) + " FE", x, y, color);
        y += lineH;
        fontRenderer.drawString("Power Con: " + NumberUtils.getFormattedBigNumber(sphere.getLastConsumedPower()) + " FE", x, y, color);
        y += lineH;
        fontRenderer.drawString("Beams: " + NumberUtils.getFormattedBigNumber(sphere.getBeams()), x, y, color);
        y += lineH;
        fontRenderer.drawString("Sails: " + NumberUtils.getFormattedBigNumber(sphere.getSolarPanels()) + "/" + NumberUtils.getFormattedBigNumber(sphere.getMaxSolarPanels()), x, y, color);
        y += lineH;
        if (sphere.getSolarPanels() >= sphere.getMaxSolarPanels()) {
            fontRenderer.drawString("Needs more beams", x, y, 0xFF5555);
            y += lineH;
        }

        // Border
        int infoX = x - 4;
        int infoY = 6;
        int infoW = 112;
        int infoH = y - 6 + 4;
        drawHorizontalLine(infoX, infoX + infoW, infoY, CYAN);
        drawHorizontalLine(infoX, infoX + infoW, infoY + infoH, CYAN);
        drawVerticalLine(infoX, infoY, infoY + infoH, CYAN);
        drawVerticalLine(infoX + infoW, infoY, infoY + infoH, CYAN);

        // Energy tooltip
        if (mouseX >= guiLeft + 19 && mouseX <= guiLeft + 35 && mouseY >= guiTop + 14 && mouseY <= guiTop + 66) {
            drawHoveringText(
                    NumberUtils.getFormattedBigNumber(tile.getEnergyStorage().getEnergyStored()) + " / " +
                            NumberUtils.getFormattedBigNumber(tile.getEnergyStorage().getMaxEnergyStored()) + " FE",
                    mouseX - guiLeft, mouseY - guiTop);
        }

        // Subscribe tooltip
        if (mouseX >= guiLeft + SUB_X && mouseX <= guiLeft + SUB_X + SUB_W
                && mouseY >= guiTop + SUB_Y && mouseY <= guiTop + SUB_Y + SUB_H) {
            drawHoveringText("Subscribe to this sphere", mouseX - guiLeft, mouseY - guiTop);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseX >= guiLeft + SUB_X && mouseX <= guiLeft + SUB_X + SUB_W
                && mouseY >= guiTop + SUB_Y && mouseY <= guiTop + SUB_Y + SUB_H) {
            DCPNetworkHandler.INSTANCE.sendToServer(new ClientSubscribeSphereMessage(tile.getDysonSphereId()));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
    }
}
