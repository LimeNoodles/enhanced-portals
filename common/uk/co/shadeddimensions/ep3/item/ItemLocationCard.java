package uk.co.shadeddimensions.ep3.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import uk.co.shadeddimensions.ep3.lib.Reference;
import uk.co.shadeddimensions.ep3.tileentity.TileStabilizer;
import uk.co.shadeddimensions.ep3.tileentity.frame.TilePortalController;
import uk.co.shadeddimensions.ep3.util.WorldCoordinates;

public class ItemLocationCard extends ItemEnhancedPortals
{
    public ItemLocationCard(int id, String name)
    {
        super(id, true);
        setUnlocalizedName(name);
        setMaxStackSize(64);
    }
    
    public static boolean hasDBSLocation(ItemStack s)
    {
        return s.hasTagCompound();
    }
    
    public static WorldCoordinates getDBSLocation(ItemStack s)
    {
        if (hasDBSLocation(s))
        {
            NBTTagCompound t = s.getTagCompound();            
            return new WorldCoordinates(t.getInteger("X"), t.getInteger("Y"), t.getInteger("Z"), t.getInteger("D"));
        }
        
        return null;
    }

    public static void setDBSLocation(ItemStack s, WorldCoordinates w)
    {
        NBTTagCompound t = new NBTTagCompound();
        t.setInteger("X", w.posX);
        t.setInteger("Y", w.posY);
        t.setInteger("Z", w.posZ);
        t.setInteger("D", w.dimension);
        
        s.setTagCompound(t);
    }
    
    public static void clearDBSLocation(ItemStack s)
    {
        s.setTagCompound(null);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        WorldCoordinates w = getDBSLocation(stack);
        
        if (w != null)
        {
            list.add(EnumChatFormatting.GRAY + DimensionManager.getProvider(w.dimension).getDimensionName());
            list.add(EnumChatFormatting.DARK_GRAY + "X: " + w.posX);
            list.add(EnumChatFormatting.DARK_GRAY + "Y: " + w.posY);
            list.add(EnumChatFormatting.DARK_GRAY + "Z: " + w.posZ);
        }
    }
    
    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int par7, float par8, float par9, float par10)
    {
        TileEntity tile = world.getBlockTileEntity(x, y, z);
        
        if (tile != null && tile instanceof TileStabilizer && !hasDBSLocation(stack))
        {
            TileStabilizer bridge = (TileStabilizer) tile;
            
            if (bridge.hasConfigured && bridge.getMainBlock() != null)
            {
                player.inventory.getCurrentItem().stackSize--;

                if (player.inventory.getCurrentItem().stackSize == 0)
                {
                    player.inventory.mainInventory[player.inventory.currentItem] = null;
                }
                
                ItemStack s = stack.copy();
                s.stackSize = 1;
                
                setDBSLocation(s, bridge.getMainBlock().getWorldCoordinates());
                
                player.inventory.addItemStackToInventory(s);
                return true;
            }
        }
        else if (tile != null && tile instanceof TilePortalController && hasDBSLocation(stack))
        {
            TilePortalController controller = (TilePortalController) tile;
            
            if (!controller.hasConfigured && controller.waitingForCard)
            {
                WorldCoordinates w = ItemLocationCard.getDBSLocation(stack);
                TileEntity t = w.getBlockTileEntity();
                
                if (t != null && t instanceof TileStabilizer)
                {
                    controller.bridgeStabilizer = w;
                    controller.waitingForCard = false;
                    controller.hasConfigured = true;
                    
                    player.inventory.getCurrentItem().stackSize--;

                    if (player.inventory.getCurrentItem().stackSize == 0)
                    {
                        player.inventory.mainInventory[player.inventory.currentItem] = null;
                    }
                    
                    player.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey(Reference.SHORT_ID + ".chat.success"));
                }
                else
                {
                    clearDBSLocation(stack);
                    player.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey(Reference.SHORT_ID + ".chat.error.voidLinkCard"));
                }
            }
        }
        
        return super.onItemUse(stack, player, world, x, y, z, par7, par8, par9, par10);
    }
    
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if (player.isSneaking() && hasDBSLocation(stack))
        {
            clearDBSLocation(stack);
        }
        
        return super.onItemRightClick(stack, world, player);
    }
}