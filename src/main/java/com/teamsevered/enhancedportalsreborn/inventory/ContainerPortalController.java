package com.teamsevered.enhancedportalsreborn.inventory;

import com.teamsevered.enhancedportalsreborn.EnhancedPortalsReborn;
import com.teamsevered.enhancedportalsreborn.client.gui.BaseGui;
import com.teamsevered.enhancedportalsreborn.client.gui.GuiPortalController;
import com.teamsevered.enhancedportalsreborn.network.packet.PacketGuiData;
import com.teamsevered.enhancedportalsreborn.portal.GlyphIdentifier;
import com.teamsevered.enhancedportalsreborn.tile.TileController;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.nbt.NBTTagCompound;

public class ContainerPortalController extends BaseContainer
{
    TileController controller;
    byte wasPublic = -100;
    String oldGlyphs = "EMPTY";

    public ContainerPortalController(TileController c, InventoryPlayer p)
    {
        super(null, p, GuiPortalController.CONTAINER_SIZE + BaseGui.bufferSpace + BaseGui.playerInventorySize);
        controller = c;
        hideInventorySlots();
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        byte isPublic = (byte) (controller.isPublic ? 1 : 0);
        String glyphs = controller.getIdentifierUnique() == null ? "" : controller.getIdentifierUnique().getGlyphString();

        for (int i = 0; i < this.listeners.size(); i++)
        {
            IContainerListener iContainerListener = (IContainerListener)this.listeners.get(i);

            if (isPublic != wasPublic)
            {
                //todo iContainerListener.sendProgressBarUpdate(this, 0, isPublic);
            }

            if (!glyphs.equals(oldGlyphs))
            {
                NBTTagCompound t = new NBTTagCompound();
                t.setString("uid", glyphs);
                EnhancedPortalsReborn.packetPipeline.sendTo(new PacketGuiData(t), (EntityPlayerMP) iContainerListener);
            }
        }

        oldGlyphs = glyphs;
        wasPublic = isPublic;
    }

    @Override
    public void handleGuiPacket(NBTTagCompound tag, EntityPlayer player)
    {
        if (tag.hasKey("public"))
        {
            controller.isPublic = !controller.isPublic;
        }

        if (tag.hasKey("uid"))
        {
            controller.setUID(new GlyphIdentifier(tag.getString("uid")));
        }
    }

    @Override
    public void updateProgressBar(int id, int val)
    {
        if (id == 0)
        {
            controller.isPublic = val == 1;
        }
    }
}
