package github.meloweh.wolfcompanion.util;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
// 1.20+: import net.minecraft.registry.tag.FluidTags;
// older:  import net.minecraft.tag.FluidTags;

public final class LineScan {
    public static final class ExtrudedResult {
        /** Any column where the first hit (from top) is lava before any solid. */
        public final boolean anyLavaFirst;
        /** Any column where the first hit (from top) is solid before any lava. */
        public final boolean anySolidFirst;
        /** No lava anywhere in the scanned prism (fluids or lava cauldrons). */
        public final boolean allNonLava;
        public ExtrudedResult(boolean anyLavaFirst, boolean anySolidFirst, boolean allNonLava) {
            this.anyLavaFirst = anyLavaFirst;
            this.anySolidFirst = anySolidFirst;
            this.allNonLava = allNonLava;
        }
    }

    /** Scan blocks touched by [start,end] extruded downward by `down` blocks. */
    public static ExtrudedResult scanSolidsAndLavaExtrudedY(BlockView world, Vec3d start, Vec3d end, int down) {
        if (down < 0) down = 0;

        double dx = end.x - start.x, dz = end.z - start.z;
        int x = MathHelper.floor(start.x), z = MathHelper.floor(start.z);
        int ex = MathHelper.floor(end.x),   ez = MathHelper.floor(end.z);

        // Use the higher of the two as the top; typical use has start.y == end.y.
        int yTop = MathHelper.floor(Math.max(start.y, end.y));
        int yMin = yTop - down;

        int sx = dx > 0 ? 1 : dx < 0 ? -1 : 0;
        int sz = dz > 0 ? 1 : dz < 0 ? -1 : 0;

        double invDx = dx != 0 ? 1.0 / Math.abs(dx) : Double.POSITIVE_INFINITY;
        double invDz = dz != 0 ? 1.0 / Math.abs(dz) : Double.POSITIVE_INFINITY;

        double nbx = x + (sx > 0 ? 1.0 : 0.0);
        double nbz = z + (sz > 0 ? 1.0 : 0.0);
        double tMaxX = sx != 0 ? (nbx - start.x) / dx : Double.POSITIVE_INFINITY;
        double tMaxZ = sz != 0 ? (nbz - start.z) / dz : Double.POSITIVE_INFINITY;
        double tDeltaX = invDx, tDeltaZ = invDz;

        BlockPos.Mutable pos = new BlockPos.Mutable();
        boolean anyLavaFirst = false, anySolidFirst = false, allNonLava = true;

        while (true) {
            boolean columnSawSolid = false;

            for (int y = yTop; y >= yMin; y--) {
                pos.set(x, y, z);
                BlockState s = world.getBlockState(pos);

                // Lava presence (treat cauldrons as lava for "non-lava" aggregate, but not as "first lava" ground)
                boolean isLavaFluid = s.getFluidState().isIn(FluidTags.LAVA);
                boolean isLavaCauldron = s.isOf(Blocks.LAVA_CAULDRON);
                if (isLavaFluid) {
                    anyLavaFirst = true;
                    allNonLava = false;
                    break; // lava before any solid in this column
                }
                if (isLavaCauldron) {
                    allNonLava = false; // hazard present, but rim is solid
                    // fall through to solidity check; the cauldron itself is solid ground
                }

                if (isSolid(world, pos, s)) {
                    anySolidFirst = true;
                    columnSawSolid = true;
                    break; // solid before any lava ⇒ skip the rest of this column
                }
            }

            // Optional global early-exit: if you only care “is any column lava-first?”
            // if (anyLavaFirst) return new ExtrudedResult(true, anySolidFirst, allNonLava);

            if (x == ex && z == ez) break;

            if (tMaxX <= tMaxZ) { x += sx; tMaxX += tDeltaX; }
            else                { z += sz; tMaxZ += tDeltaZ; }
        }
        return new ExtrudedResult(anyLavaFirst, anySolidFirst, allNonLava);
    }

    private static boolean isSolid(BlockView world, BlockPos pos, BlockState s) {
        return s.isOpaqueFullCube(); // cheap, close to “ground” semantics
        // Alternative per-version: Block.isSolidBlock(world, pos)
    }

    public static boolean hasFloorLava(final LivingEntity wolf) {
        final Vec3d lookUnit = Vec3d.fromPolar(0, wolf.getYaw());
        final Vec3d lookDistance = lookUnit.multiply(2);
        final Vec3d origin = wolf.getBoundingBox().getCenter();
        final Vec3d target = origin.add(lookDistance);

        final World world = wolf.getWorld();

        ExtrudedResult result = scanSolidsAndLavaExtrudedY(world, origin, target, 10);

        return !result.allNonLava;
    }
}