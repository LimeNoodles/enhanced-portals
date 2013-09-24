package uk.co.shadeddimensions.enhancedportals.container;

import net.minecraft.entity.player.EntityPlayer;
import uk.co.shadeddimensions.enhancedportals.tileentity.frame.TileNetworkInterface;

public class ContainerNetworkInterface extends ContainerEnhancedPortals
{
    public ContainerNetworkInterface(TileNetworkInterface t)
    {
        super(t.getSizeInventory(), t);
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer)
    {
        return ((TileNetworkInterface) tile).isUseableByPlayer(entityplayer);
    }
}
