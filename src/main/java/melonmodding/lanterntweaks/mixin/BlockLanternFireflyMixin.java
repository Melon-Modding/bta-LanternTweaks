package melonmodding.lanterntweaks.mixin;

import net.minecraft.core.block.*;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.piston.BlockPistonBase;
import net.minecraft.core.block.piston.BlockPistonHead;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BlockLanternFirefly.class, remap = false)
public abstract class BlockLanternFireflyMixin extends Block{

	public BlockLanternFireflyMixin(String key, int id, Material material) {
		super(key, id, material);
	}

	@Inject(at = @At("HEAD"), cancellable = true, method = "canBlockStay")
	private void fixCanBlockStay(World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
		int meta = world.getBlockMetadata(x, y, z);
		Block block;
		if(meta != 1) {
			block = Block.blocksList[world.getBlockId(x, y - 1, z)];
			if(checkBlockGrounded(world, block, x, y, z)) {
				cir.setReturnValue(true);
			}
		} else {
			block = Block.blocksList[world.getBlockId(x, y + 1, z)];
			if(checkBlockHanging(world, block, x, y, z)) {
				cir.setReturnValue(true);
			}
		}
	}


	@Inject(at = @At("HEAD"), cancellable = true, method = "onBlockAdded")
	private void fixOnBlockAdded(World world, int x, int y, int z, CallbackInfo ci) {
		Block block = Block.blocksList[world.getBlockId(x, y + 1, z)];
		if(checkBlockHanging(world, block, x, y, z)) {
			world.setBlockMetadataWithNotify(x, y, z, 1);
			ci.cancel();
		}

		if(checkBlockGrounded(world, block, x, y, z)) {
			world.setBlockMetadataWithNotify(x, y, z, 0);
			ci.cancel();
		}
	}

	@Inject(at = @At("HEAD"), cancellable = true, method = "canPlaceBlockAt")
	private void fixCanPlaceBlockAt(World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
		Block block = Block.blocksList[world.getBlockId(x, y + 1, z)];
		if(checkBlockHanging(world, block, x, y, z)) {
			cir.setReturnValue(true);
		}

		block = Block.blocksList[world.getBlockId(x, y-1, z)];
		if(checkBlockGrounded(world, block, x, y, z)) {
			cir.setReturnValue(true);
		}
	}

	//There must be a better way to write this, it SMELLS aaaa
	@Unique
	private boolean checkBlockHanging(World world, Block block, int x, int y, int z) {
		return checkBlockStairs(world, block, x, y, z) || checkBlockSlab(world, block, x, y, z)
			|| checkBlockTrapdoorHanging(world, block, x, y, z) || checkBlockPistonHanging(world, block, x, y, z)
			|| (block instanceof BlockGlass) || (block instanceof BlockMesh)
			|| (block instanceof BlockTNT) || (block instanceof BlockDirtPath)
			|| (block instanceof BlockMotionSensor) || (block instanceof BlockLeavesBase)
			|| (block instanceof BlockIce) || (block instanceof BlockSlippery);
	}

	@Unique
	private boolean checkBlockGrounded(World world, Block block, int x, int y, int z) {
		return checkBlockTrapdoorGrounded(world, block, x, y, z) || checkBlockPistonGrounded(world, block, x, y, z);
	}

	@Unique
	private boolean checkBlockTrapdoorGrounded(World world, Block block, int x, int y, int z) {
		if(!(block instanceof BlockTrapDoor)) {
			return false;
		}

		int meta = world.getBlockMetadata(x, y - 1, z);
		return meta >= 8 && meta <= 11;
	}

	@Unique
	private boolean checkBlockTrapdoorHanging(World world, Block block, int x, int y, int z) {
		if(!(block instanceof BlockTrapDoor)) {
			return false;
		}

		int meta = world.getBlockMetadata(x, y + 1, z);
		return meta <= 3;
	}

	@Unique
	private boolean checkBlockStairs(World world, Block block, int x, int y, int z) {
		if(!(block instanceof BlockStairs)) {
			return false;
		}

		int meta = world.getBlockMetadata(x, y + 1, z) & 8;
        return meta <= 3;
    }

	@Unique
	private boolean checkBlockSlab(World world, Block block, int x, int y, int z) {
		if(!(block instanceof BlockSlab)) {
			return false;
		}

		int meta = world.getBlockMetadata(x, y + 1, z) & 3;
		return meta == 0;
	}

	@Unique
	private boolean checkBlockPistonHanging(World world, Block block, int x, int y, int z) {
		if(!(block instanceof BlockPistonBase) && !(block instanceof BlockPistonHead)) {
			return false;
		}

		int meta = world.getBlockMetadata(x, y + 1, z);
		return meta == 0 || meta == 1 || meta == 8 || meta == 9;
	}

	@Unique
	private boolean checkBlockPistonGrounded(World world, Block block, int x, int y, int z) {
		if(!(block instanceof BlockPistonBase) && !(block instanceof BlockPistonHead)) {
			return false;
		}

		int meta = world.getBlockMetadata(x, y - 1, z);
		return (meta >= 0 && meta <= 5) || (meta >= 8 && meta <= 13);
	}

}
