package github.meloweh.wolfcompanion.util;

import java.util.ArrayList;
import java.util.List;

public class WolfConfig {
    public boolean canShakeOffPoison = true;
    public boolean canShakeOffFire = true;
    public boolean canTeleportSitting = true;
    public boolean canRespawn = true;
    public boolean keepWolfBag = true;
    public boolean keepWolfInventory = false;
    public boolean keepWolfArmor = false;
    public boolean canPickupFood = true;
    public int maxPickupFood = 10;
    public boolean pickAllRottenFlesh = true;
    public boolean shouldCarePlayerFood = true;
    public int requiredPlayerFood = 10;
    public List<String> doNotAttackMobs = new ArrayList<>(List.of());
    public boolean keepXp = false;
    public boolean allowTeleport = true;
    public double teleportAtDistance = 3300f;
    public double attackAcceleration = 1.2f;
    public double maxSpeed = 3f;
    public boolean allowPassiveRegeneration = true;
    public int passiveRegenerationRate = 15;

    public WolfConfig copy() {
        WolfConfig copy = new WolfConfig();
        copy.canShakeOffPoison = this.canShakeOffPoison;
        copy.canShakeOffFire = this.canShakeOffFire;
        copy.canTeleportSitting = this.canTeleportSitting;
        copy.canRespawn = this.canRespawn;
        copy.keepWolfBag = this.keepWolfBag;
        copy.keepWolfInventory = this.keepWolfInventory;
        copy.keepWolfArmor = this.keepWolfArmor;
        copy.canPickupFood = this.canPickupFood;
        copy.maxPickupFood = this.maxPickupFood;
        copy.pickAllRottenFlesh = this.pickAllRottenFlesh;
        copy.shouldCarePlayerFood = this.shouldCarePlayerFood;
        copy.requiredPlayerFood = this.requiredPlayerFood;
        copy.doNotAttackMobs = this.doNotAttackMobs == null ? new ArrayList<>() : new ArrayList<>(this.doNotAttackMobs);
        copy.keepXp = this.keepXp;
        copy.allowTeleport = this.allowTeleport;
        copy.teleportAtDistance = this.teleportAtDistance;
        copy.attackAcceleration = this.attackAcceleration;
        copy.maxSpeed = this.maxSpeed;
        copy.allowPassiveRegeneration = this.allowPassiveRegeneration;
        copy.passiveRegenerationRate = this.passiveRegenerationRate;
        return copy;
    }

    public void normalize() {
        if (this.doNotAttackMobs == null) {
            this.doNotAttackMobs = new ArrayList<>();
        }
    }
}
