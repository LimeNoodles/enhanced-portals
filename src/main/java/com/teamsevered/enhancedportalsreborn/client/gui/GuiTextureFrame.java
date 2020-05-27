package com.teamsevered.enhancedportalsreborn.client.gui;

import com.teamsevered.enhancedportalsreborn.EnhancedPortalsReborn;
import com.teamsevered.enhancedportalsreborn.client.gui.button.GuiRGBSlider;
import com.teamsevered.enhancedportalsreborn.client.gui.elements.ElementFakeItemSlot;
import com.teamsevered.enhancedportalsreborn.client.gui.elements.ElementScrollFrameIcons;
import com.teamsevered.enhancedportalsreborn.client.gui.tabs.TabColour;
import com.teamsevered.enhancedportalsreborn.client.gui.tabs.TabTip;
import com.teamsevered.enhancedportalsreborn.client.gui.tabs.TabTipSecondary;
import com.teamsevered.enhancedportalsreborn.inventory.ContainerTextureFrame;
import com.teamsevered.enhancedportalsreborn.proxy.ClientProxy;
import com.teamsevered.enhancedportalsreborn.network.packet.PacketGuiData;
import com.teamsevered.enhancedportalsreborn.network.packet.PacketRequestGui;
import com.teamsevered.enhancedportalsreborn.portal.PortalTextureManager;
import com.teamsevered.enhancedportalsreborn.tile.TileController;
import com.teamsevered.enhancedportalsreborn.util.GuiEnums;
import com.teamsevered.enhancedportalsreborn.util.IFakeSlotHandler;
import com.teamsevered.enhancedportalsreborn.util.Localization;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import java.awt.*;

public class GuiTextureFrame extends BaseGui implements IFakeSlotHandler
{
    public static final int CONTAINER_SIZE = 92, CONTAINER_WIDTH = 190;
    protected TileController controller;
    protected GuiRGBSlider sliderR, sliderG, sliderB;
    protected GuiButton buttonReset, buttonSave;
    protected int particleFrameType = -1, particleFrame, particleFrameCycle;
    protected int[] particleFrames = new int[]{0};

    public GuiTextureFrame(TileController c, EntityPlayer p)
    {
        super(new ContainerTextureFrame(c, p.inventory), CONTAINER_SIZE);
        controller = c;
        xSize = CONTAINER_WIDTH;
        name = "gui.frame";
        texture = new ResourceLocation("enhancedportals", "textures/gui/textures.png");
        leftNudge = 7;
        hasSingleTexture = true;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
        String s = Localization.get("gui.facade");
        getFontRenderer().drawString(s, xSize - 30 - getFontRenderer().getStringWidth(s), containerSize - 12, 0x404040);

        //itemRenderer.renderWithColor = false;
        ItemStack portal = new ItemStack(net.minecraft.init.Blocks.PORTAL, 0, 0);
        Color portalColour = new Color(getPTM().getPortalColour()), particleColour = new Color(0x0077D8);
        int particleType = 0;

        if (getPTM() != null)
        {
            portalColour = new Color(getPTM().getPortalColour());
            particleColour = new Color(getPTM().getParticleColour());
            particleType = getPTM().getParticleType();

            if (getPTM().getPortalItem() != null)
            {
                portal = getPTM().getPortalItem();
            }

            if (particleFrameType != particleType)
            {
                particleFrameType = particleType;
                particleFrame = 0;
                particleFrameCycle = 0;
                particleFrames = ClientProxy.particleSets.get(getPTM().getParticleType()).frames;
            }
        }

        GL11.glColor3f(portalColour.getRed() / 255F, portalColour.getGreen() / 255F, portalColour.getBlue() / 255F);

        //todo drawIconNoReset

        /*if (getPTM().hasCustomPortalTexture())
        {
            drawIconNoReset(ClientProxy.customPortalTextures.get(getPTM().getCustomPortalTexture()), 9, containerSize - 16, 0);
        }
        else
        {
            drawItemStack(portal, 9, containerSize - 16);
        }*/

        GL11.glColor3f(particleColour.getRed() / 255F, particleColour.getGreen() / 255F, particleColour.getBlue() / 255F);
        getTextureManager().bindTexture(new ResourceLocation("textures/particle/particles.png"));
        drawTexturedModalRect(30, containerSize - 16, particleFrames[particleFrame] % 16 * 16, particleFrames[particleFrame] / 16 * 16, 16, 16);

        GL11.glColor3f(1f, 1f, 1f);
        super.drawGuiContainerForegroundLayer(par1, par2);
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        if (button.id == buttonSave.id || button.id == buttonReset.id)
        {
            if (button.id == buttonSave.id)
            {
                int hex = Integer.parseInt(String.format("%02x%02x%02x", sliderR.getValue(), sliderG.getValue(), sliderB.getValue()), 16);
                getPTM().setFrameColour(hex);
            }
            else if (button.id == buttonReset.id)
            {
                int colour = 0xffffff;
                getPTM().setFrameColour(colour);

                Color c = new Color(colour);
                sliderR.sliderValue = c.getRed() / 255f;
                sliderG.sliderValue = c.getGreen() / 255f;
                sliderB.sliderValue = c.getBlue() / 255f;
            }

            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("colour", Integer.parseInt(String.format("%02x%02x%02x", sliderR.getValue(), sliderG.getValue(), sliderB.getValue()), 16));
            EnhancedPortalsReborn.packetPipeline.sendToServer(new PacketGuiData(tag));
        }
        else if (button.id == 500)
        {
            EnhancedPortalsReborn.packetPipeline.sendToServer(new PacketRequestGui(controller, GuiEnums.GUI_TEXTURE.TEXTURE_B));
        }
        else if (button.id == 501)
        {
            EnhancedPortalsReborn.packetPipeline.sendToServer(new PacketRequestGui(controller, GuiEnums.GUI_TEXTURE.TEXTURE_C));
        }
    }

    @Override
    public void initGui()
    {
        super.initGui();

        Color c = new Color(getPTM().getFrameColour());
        sliderR = new GuiRGBSlider(100, guiLeft + xSize + 4, guiTop + 25, Localization.get("gui.red"), c.getRed() / 255f, 105);
        sliderG = new GuiRGBSlider(101, guiLeft + xSize + 4, guiTop + 46, Localization.get("gui.green"), c.getGreen() / 255f, 105);
        sliderB = new GuiRGBSlider(102, guiLeft + xSize + 4, guiTop + 67, Localization.get("gui.blue"), c.getBlue() / 255f, 105);

        buttonList.add(sliderR);
        buttonList.add(sliderG);
        buttonList.add(sliderB);

        buttonSave = new GuiButton(110, guiLeft + xSize + 4, guiTop + 88, 53, 20, Localization.get("gui.save"));
        buttonReset = new GuiButton(111, guiLeft + xSize + 57, guiTop + 88, 53, 20, Localization.get("gui.reset"));

        buttonList.add(buttonSave);
        buttonList.add(buttonReset);

        buttonList.add(new GuiButton(500, guiLeft + 7, guiTop + containerSize - 18, 20, 20, ""));
        buttonList.add(new GuiButton(501, guiLeft + 28, guiTop + containerSize - 18, 20, 20, ""));

        addTab(new TabColour(this, sliderR, sliderG, sliderB, buttonSave, buttonReset));
        addTab(new TabTip(this, "colourTip"));
        addTab(new TabTipSecondary(this, "frameCustomTexture"));
        addElement(new ElementScrollFrameIcons(this, 7, 17, texture));
        addElement(new ElementFakeItemSlot(this, xSize - 24, containerSize - 16, getPTM().getFrameItem()));
    }


    //todo Mouse moved
/*    @Override
    protected void mouseMovedOrUp(int par1, int par2, int par3)
    {
        super.mouseMovedOrUp(par1, par2, par3);

        if (par3 == 0)
        {
            for (Object o : buttonList)
            {
                if (o instanceof GuiBetterSlider)
                {
                    GuiBetterSlider slider = (GuiBetterSlider) o;
                    slider.mouseReleased(par1, par2);
                }
            }
        }
    }*/

    public void iconSelected(int icon)
    {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("custom", icon);
        EnhancedPortalsReborn.packetPipeline.sendToServer(new PacketGuiData(tag));
    }

    @Override
    public void onItemChanged(ItemStack newItem)
    {
        NBTTagCompound tag = new NBTTagCompound();

        if (newItem != null)
        {
            newItem.writeToNBT(tag);
        }
        else
        {
            tag.setBoolean("removeItem", true);
        }

        EnhancedPortalsReborn.packetPipeline.sendToServer(new PacketGuiData(tag));
    }

    @Override
    public boolean isItemValid(ItemStack s)
    {
        if (s == null)
        {
            return true;
        }

        Block b = Block.getBlockFromItem(s.getItem());

        if (b == net.minecraft.init.Blocks.AIR)
        {
            return false;
        }

        return true;
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();

        if (particleFrameCycle >= 20)
        {
            particleFrame++;
            particleFrameCycle = 0;

            if (particleFrame >= particleFrames.length)
            {
                particleFrame = 0;
            }
        }

        particleFrameCycle++;
    }

    public PortalTextureManager getPTM()
    {
        return controller.activeTextureData;
    }
}
