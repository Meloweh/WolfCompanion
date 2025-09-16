package github.meloweh.wolfcompanion.util;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;


import java.util.function.Predicate;

@Deprecated
public class ScanlineHelper {
    public static boolean allTouchedMatchExtrudedY(BlockView world, Vec3d start, Vec3d end, int down, Predicate<BlockState> test) {
        if (down < 0) down = 0;

        double dx = end.x - start.x, dz = end.z - start.z;
        int x = MathHelper.floor(start.x), z = MathHelper.floor(start.z);
        int ex = MathHelper.floor(end.x),   ez = MathHelper.floor(end.z);
        int yTop = MathHelper.floor(start.y);
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

        while (true) {
            for (int y = yTop; y >= yMin; y--) {
                pos.set(x, y, z);
                if (!test.test(world.getBlockState(pos))) return false;
            }
            if (x == ex && z == ez) break;

            if (tMaxX <= tMaxZ) { x += sx; tMaxX += tDeltaX; }
            else                { z += sz; tMaxZ += tDeltaZ; }
        }
        return true;
    }

    public static boolean allTouchedMatch(BlockView world, Vec3d start, Vec3d end, Predicate<BlockState> test) {
        if (start.equals(end)) {
            BlockPos p = BlockPos.ofFloored(start);
            return test.test(world.getBlockState(p));
        }

        double dx = end.x - start.x;
        double dy = end.y - start.y;
        double dz = end.z - start.z;

        int x = MathHelper.floor(start.x);
        int y = MathHelper.floor(start.y);
        int z = MathHelper.floor(start.z);
        int endX = MathHelper.floor(end.x);
        int endY = MathHelper.floor(end.y);
        int endZ = MathHelper.floor(end.z);

        int stepX = dx > 0 ? 1 : dx < 0 ? -1 : 0;
        int stepY = dy > 0 ? 1 : dy < 0 ? -1 : 0;
        int stepZ = dz > 0 ? 1 : dz < 0 ? -1 : 0;

        double invDx = dx != 0 ? 1.0 / Math.abs(dx) : Double.POSITIVE_INFINITY;
        double invDy = dy != 0 ? 1.0 / Math.abs(dy) : Double.POSITIVE_INFINITY;
        double invDz = dz != 0 ? 1.0 / Math.abs(dz) : Double.POSITIVE_INFINITY;

        double nextBoundaryX = x + (stepX > 0 ? 1.0 : 0.0);
        double nextBoundaryY = y + (stepY > 0 ? 1.0 : 0.0);
        double nextBoundaryZ = z + (stepZ > 0 ? 1.0 : 0.0);

        double tMaxX = stepX != 0 ? (nextBoundaryX - start.x) / dx : Double.POSITIVE_INFINITY;
        double tMaxY = stepY != 0 ? (nextBoundaryY - start.y) / dy : Double.POSITIVE_INFINITY;
        double tMaxZ = stepZ != 0 ? (nextBoundaryZ - start.z) / dz : Double.POSITIVE_INFINITY;

        double tDeltaX = invDx;
        double tDeltaY = invDy;
        double tDeltaZ = invDz;

        BlockPos.Mutable pos = new BlockPos.Mutable();

        // include start voxel
        pos.set(x, y, z);
        if (!test.test(world.getBlockState(pos))) return false;

        while (x != endX || y != endY || z != endZ) {
            if (tMaxX <= tMaxY && tMaxX <= tMaxZ) {
                x += stepX; tMaxX += tDeltaX;
            } else if (tMaxY <= tMaxZ) {
                y += stepY; tMaxY += tDeltaY;
            } else {
                z += stepZ; tMaxZ += tDeltaZ;
            }
            pos.set(x, y, z);
            if (!test.test(world.getBlockState(pos))) return false;
        }
        return true;
    }

    public static boolean isLavaOnWay(final LivingEntity wolf) {
        final Vec3d lookUnit = Vec3d.fromPolar(0, wolf.getYaw());
        final Vec3d lookDistance = lookUnit.multiply(2);
        final Vec3d origin = wolf.getBoundingBox().getCenter();
        final Vec3d target = origin.add(lookDistance);

        final World world = wolf.getWorld();

        final boolean optimizeY = true;

        if (optimizeY) {
            final boolean noLava = ScanlineHelper.allTouchedMatchExtrudedY(world, origin, target, 10, state -> !state.getFluidState().isIn(FluidTags.LAVA));
            return !noLava;
        }

        for (int i = 1; i <= 4; i++) {
            final Vec3d start = origin.subtract(0, i, 0);
            final Vec3d end = target.subtract(0, i, 0);

            final boolean noLava = ScanlineHelper.allTouchedMatch(world, start, end, state -> !state.getFluidState().isIn(FluidTags.LAVA));

            if (!noLava) return true;
        }
        return false;
    }
}
