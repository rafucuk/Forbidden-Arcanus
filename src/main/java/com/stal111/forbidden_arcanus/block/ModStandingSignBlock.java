package com.stal111.forbidden_arcanus.block;

import com.stal111.forbidden_arcanus.block.tileentity.ModSignTileEntity;
import net.minecraft.block.StandingSignBlock;
import net.minecraft.block.WoodType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class ModStandingSignBlock extends StandingSignBlock {

    public ModStandingSignBlock(Properties properties, WoodType woodType) {
        super(properties, woodType);
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader world) {
        return new ModSignTileEntity();
    }
}
