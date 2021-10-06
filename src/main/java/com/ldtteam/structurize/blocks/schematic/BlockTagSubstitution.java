package com.ldtteam.structurize.blocks.schematic;

import com.ldtteam.structurize.blockentities.BlockEntityTagSubstitution;
import com.ldtteam.structurize.blocks.interfaces.IAnchorBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * This block is a substitution block (it disappears on normal build) but stores blueprint
 * data (mostly tags) during scan.
 */
public class BlockTagSubstitution extends BlockSubstitution implements IAnchorBlock, EntityBlock
{
    @Nullable
    @Override
    public BlockEntity newBlockEntity(final @NotNull BlockPos blockPos, final @NotNull BlockState blockState)
    {
        return new BlockEntityTagSubstitution(blockPos, blockState);
    }
}