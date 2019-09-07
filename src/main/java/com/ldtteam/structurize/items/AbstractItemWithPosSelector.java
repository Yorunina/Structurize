package com.ldtteam.structurize.items;

import com.ldtteam.structurize.util.LanguageHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static com.ldtteam.structurize.api.util.constant.NbtTagConstants.FIRST_POS_STRING;
import static com.ldtteam.structurize.api.util.constant.NbtTagConstants.SECOND_POS_STRING;
import com.ldtteam.structurize.api.util.Log;

/**
 * Abstract item mechanic for pos selecting
 */
public abstract class AbstractItemWithPosSelector extends Item
{
    private static final String NBT_START_POS = FIRST_POS_STRING;
    private static final String NBT_END_POS = SECOND_POS_STRING;
    private static final String START_POS_TKEY = "item.possetter.firstpos";
    private static final String END_POS_TKEY = "item.possetter.secondpos";

    /**
     * MC redirect.
     *
     * @param properties item properties
     */
    public AbstractItemWithPosSelector(final Properties properties)
    {
        super(properties);
    }

    /**
     * Is called when player air-right-clicks with item.
     *
     * @param start    first pos
     * @param end      second pos
     * @param worldIn  event world
     * @param playerIn event player
     * @return event result, typically success
     */
    public abstract ActionResultType onAirRightClick(BlockPos start, BlockPos end, World worldIn, PlayerEntity playerIn);

    /**
     * Uses to search for correct itemstack in both hands.
     *
     * @return item reference from {@link ModItems}
     */
    public abstract AbstractItemWithPosSelector getRegisteredItemInstance();

    /**
     * <p>
     * Structurize: Calls {@link AbstractItemWithPosSelector#onAirRightClick()}.
     * <p/>
     * {@inheritDoc}
     */
    @Override
    public ActionResult<ItemStack> onItemRightClick(final World worldIn, final PlayerEntity playerIn, final Hand handIn)
    {
        final ItemStack itemstack = playerIn.getHeldItem(handIn);
        final CompoundNBT compound = itemstack.getOrCreateTag();
        return new ActionResult<>(
            onAirRightClick(
                NBTUtil.readBlockPos(compound.getCompound(NBT_START_POS)),
                NBTUtil.readBlockPos(compound.getCompound(NBT_END_POS)),
                worldIn,
                playerIn),
            itemstack);
    }

    /**
     * <p>
     * Structurize: Captures second position.
     * <p/>
     * {@inheritDoc}
     */
    @Override
    public ActionResultType onItemUse(final ItemUseContext context)
    {
        final BlockPos pos = context.getPos();
        if (context.getWorld().isRemote())
        {
            LanguageHandler.sendMessageToPlayer(context.getPlayer(), END_POS_TKEY, pos.getX(), pos.getY(), pos.getZ());
        }
        else
        {
            context.getItem().getOrCreateTag().put(NBT_END_POS, NBTUtil.writeBlockPos(pos));
        }
        return ActionResultType.SUCCESS;
    }

    /**
     * <p>
     * Structurize: Prevent block breaking client side. Captures first position client side.
     * <p/>
     * {@inheritDoc}
     */
    @Override
    public boolean onBlockStartBreak(final ItemStack itemstack, final BlockPos pos, final PlayerEntity player)
    {
        itemstack.getOrCreateTag().put(NBT_START_POS, NBTUtil.writeBlockPos(pos));
        return true;
    }

    /**
     * <p>
     * Structurize: Prevent block breaking server side. Captures first position server side.
     * <p/>
     * {@inheritDoc}
     */
    @Override
    public boolean canPlayerBreakBlockWhileHolding(final BlockState state, final World worldIn, final BlockPos pos, final PlayerEntity player)
    {
        ItemStack itemstack = player.getHeldItemMainhand();
        if (!itemstack.getItem().equals(getRegisteredItemInstance()))
        {
            itemstack = player.getHeldItemOffhand();
        }
        itemstack.getOrCreateTag().put(NBT_START_POS, NBTUtil.writeBlockPos(pos));
        if (player.getEntityWorld().isRemote())
        {
            LanguageHandler.sendMessageToPlayer(player, START_POS_TKEY, pos.getX(), pos.getY(), pos.getZ());
        }
        return false;
    }

    /**
     * <p>
     * Structurize: Prevent block breaking.
     * <p/>
     * {@inheritDoc}
     */
    @Override
    public float getDestroySpeed(final ItemStack stack, final BlockState state)
    {
        return 20.0f;
    }
}
