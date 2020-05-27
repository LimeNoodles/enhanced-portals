package com.teamsevered.enhancedportalsreborn.inventory;

import com.teamsevered.enhancedportalsreborn.items.ItemAddressBook;
import com.teamsevered.enhancedportalsreborn.network.packet.PacketGui;
import com.teamsevered.enhancedportalsreborn.network.packet.PacketTextureData;
import com.teamsevered.enhancedportalsreborn.portal.GlyphElement;
import com.teamsevered.enhancedportalsreborn.util.GuiEnums;
import com.teamsevered.enhancedportalsreborn.EnhancedPortalsReborn;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;

public class ContainerAddressBook extends BaseContainer
{
    ItemAddressBook addressBook;
    ArrayList<String> list = new ArrayList<String>();

    public ContainerAddressBook(ItemAddressBook b, InventoryPlayer p)
    {
        super(null, p);
        addressBook = b;
        hideInventorySlots();
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        if (list.size() != addressBook.glyphList.size())
        {
            updateAll();
        }
        else
        {
            for (int i = 0; i < list.size(); i++)
            {
                if (!list.get(i).equals(addressBook.glyphList.get(i).name))
                {
                    updateAll();
                    break;
                }
            }
        }

        list.clear();

        for (GlyphElement e : addressBook.glyphList)
        {
            list.add(e.name);
        }
    }

    @Override
    public void handleGuiPacket(NBTTagCompound tag, EntityPlayer player)
    {
        if (tag.hasKey("edit"))
        {
            int id = tag.getInteger("edit");

            if (addressBook.glyphList.size() > id)
            {
                GlyphElement e = addressBook.glyphList.get(id);
                player.openGui(EnhancedPortalsReborn.instance, GuiEnums.GUI_ADDRESS_BOOK.ADDRESS_BOOK_D.ordinal(), Minecraft.getMinecraft().world, (int) player.posX, (int) player.posY, (int) player.posZ);
                EnhancedPortalsReborn.packetPipeline.sendTo(new PacketTextureData(e.name, e.identifier.getGlyphString(), e.texture), (EntityPlayerMP) player);
            }
        }
        else if (tag.hasKey("delete"))
        {
            int id = tag.getInteger("delete");

            if (addressBook.glyphList.size() > id)
            {
                addressBook.glyphList.remove(id);
            }

            EnhancedPortalsReborn.packetPipeline.sendTo(new PacketGui(addressBook), (EntityPlayerMP) player);
        }
    }

    void updateAll()
    {
        for (int i = 0; i < this.listeners.size(); i++)
        {
            IContainerListener iContainerListener = (IContainerListener) listeners.get(i);
            EnhancedPortalsReborn.packetPipeline.sendTo(new PacketGui(addressBook), (EntityPlayerMP) iContainerListener);
        }
    }

}
