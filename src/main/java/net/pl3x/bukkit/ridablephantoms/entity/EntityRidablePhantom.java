package net.pl3x.bukkit.ridablephantoms.entity;

import net.minecraft.server.v1_13_R1.BlockPosition;
import net.minecraft.server.v1_13_R1.ControllerMove;
import net.minecraft.server.v1_13_R1.DamageSource;
import net.minecraft.server.v1_13_R1.Entity;
import net.minecraft.server.v1_13_R1.EntityPhantom;
import net.minecraft.server.v1_13_R1.EntityPlayer;
import net.minecraft.server.v1_13_R1.EnumMoveType;
import net.minecraft.server.v1_13_R1.IBlockData;
import net.minecraft.server.v1_13_R1.MathHelper;
import net.minecraft.server.v1_13_R1.SoundEffectType;
import net.minecraft.server.v1_13_R1.Vec3D;
import net.minecraft.server.v1_13_R1.World;
import net.pl3x.bukkit.ridablephantoms.configuration.Config;

import java.lang.reflect.Field;

public class EntityRidablePhantom extends EntityPhantom {
    private static Field field_b;

    public EntityRidablePhantom(World world) {
        super(world);
        moveController = new PhantomMoveController(this);

        if (field_b == null) {
            try {
                field_b = EntityPhantom.class.getDeclaredField("b");
                field_b.setAccessible(true);
            } catch (NoSuchFieldException ignore) {
            }
        }
    }

    // travel(strafe, vertical, forward)
    @Override
    public void a(float strafe, float vertical, float forward) {
        EntityPlayer rider = getRider();
        if (rider != null) {
            // do not target anything while being ridden
            setGoalTarget(null, null, false);

            // eject rider if in water or lava
            if (isInWater() || ax()) {
                ejectPassengers();
                return;
            }

            // set rotation
            setYawPitch(lastYaw = yaw = rider.yaw, pitch = -(rider.pitch * 0.75F));
            aS = aQ = yaw;

            // controls
            forward = Math.max(0, rider.bj); // forward motion
            vertical = forward == 0 ? 0 : -(rider.pitch / 45); // vertical motion
            strafe = rider.bh; // sideways motion

            if (forward == 0) {
                motY -= Config.DEAD_STICK_GRAVITY;
            } else {
                fallDistance = 0;
            }

            // move
            a(strafe, vertical, forward, 0.025F * Config.SPEED_MODIFIER); // moveRelative
            move(EnumMoveType.PLAYER, motX, motY, motZ);
            return;
        }
        fallDistance = 0;
        super.a(strafe, vertical, forward);
    }

    public EntityPlayer getRider() {
        if (passengers != null && !passengers.isEmpty()) {
            Entity entity = passengers.get(0); // only care about first rider
            if (entity instanceof EntityPlayer) {
                return (EntityPlayer) entity;
            }
        }
        return null; // aww, lonely phantom is lonely
    }

    // onLivingUpdate
    public void k() {
        super.k();
        if (Config.IMMUNE_TO_SUNLIGHT) {
            extinguish(); // dont burn in sunlight
        }
    }

    // updateFallState
    protected void a(double d0, boolean flag, IBlockData iblockdata, BlockPosition blockposition) {
        if (flag) {
            if (Config.FALLING_HURTS && fallDistance > 0.0F) {
                iblockdata.getBlock().fallOn(world, blockposition, this, fallDistance);
            }
            fallDistance = 0.0F;
        } else if (d0 < 0.0D && motY <= -0.3F) {
            fallDistance = (float) ((double) fallDistance - (d0 / 2));
        }
    }

    // fall
    public void c(float f, float f1) {
        super.c(f, f1);
        int i = (int) (MathHelper.f((f - 3.0F) * f1) * -(motY));
        if (i > 0) {
            if (!damageEntity(DamageSource.FALL, (float) i)) {
                return;
            }
            a(n(i), 1.0F, 1.0F);
            int j = MathHelper.floor(locX);
            int k = MathHelper.floor(locY - 0.2D);
            int l = MathHelper.floor(locZ);
            IBlockData iblockdata = world.getType(new BlockPosition(j, k, l));
            if (!iblockdata.isAir()) {
                SoundEffectType soundeffecttype = iblockdata.getBlock().getStepSound();
                a(soundeffecttype.g(), soundeffecttype.a() * 0.5F, soundeffecttype.b() * 0.75F);
            }
        }
    }

    class PhantomMoveController extends ControllerMove {
        private EntityRidablePhantom phantom;
        private float j = 0.1F;

        public PhantomMoveController(EntityRidablePhantom phantom) {
            super(phantom);
            this.phantom = phantom;
        }

        public void a() {
            if (getRider() != null) {
                motY *= onGround ? 0.75D : 0.95F;
                motX *= onGround ? 0.75D : 0.95F;
                motZ *= onGround ? 0.75D : 0.95F;
                return;
            }

            if (phantom.positionChanged) {
                phantom.yaw += 180.0F;
                j = 0.1F;
            }

            Vec3D field_b;
            try {
                field_b = (Vec3D) EntityRidablePhantom.field_b.get(phantom);
            } catch (IllegalAccessException e) {
                return;
            }

            float f = (float) (field_b.x - phantom.locX);
            float f1 = (float) (field_b.y - phantom.locY);
            float f2 = (float) (field_b.z - phantom.locZ);
            double d0 = (double) MathHelper.c(f * f + f2 * f2);
            double d1 = 1.0D - (double) MathHelper.e(f1 * 0.7F) / d0;

            f = (float) ((double) f * d1);
            f2 = (float) ((double) f2 * d1);
            d0 = (double) MathHelper.c(f * f + f2 * f2);
            double d2 = (double) MathHelper.c(f * f + f2 * f2 + f1 * f1);
            float f3 = phantom.yaw;
            float f4 = (float) MathHelper.c((double) f2, (double) f);
            float f5 = MathHelper.g(phantom.yaw + 90.0F);
            float f6 = MathHelper.g(f4 * 57.295776F);

            phantom.yaw = MathHelper.c(f5, f6, 4.0F) - 90.0F;
            phantom.aQ = phantom.yaw;
            if (MathHelper.d(f3, phantom.yaw) < 3.0F) {
                j = MathHelper.b(j, 1.8F, 0.005F * (1.8F / j));
            } else {
                j = MathHelper.b(j, 0.2F, 0.025F);
            }

            float f7 = (float) (-(MathHelper.c((double) (-f1), d0) * 57.2957763671875D));

            phantom.pitch = f7;
            float f8 = phantom.yaw + 90.0F;
            double d3 = (double) (j * MathHelper.cos(f8 * 0.017453292F)) * Math.abs((double) f / d2);
            double d4 = (double) (j * MathHelper.sin(f8 * 0.017453292F)) * Math.abs((double) f2 / d2);
            double d5 = (double) (j * MathHelper.sin(f7 * 0.017453292F)) * Math.abs((double) f1 / d2);

            phantom.motX += (d3 - phantom.motX) * 0.2D;
            phantom.motY += (d5 - phantom.motY) * 0.2D;
            phantom.motZ += (d4 - phantom.motZ) * 0.2D;
        }
    }
}
