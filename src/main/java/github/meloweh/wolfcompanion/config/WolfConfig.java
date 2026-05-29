package github.meloweh.wolfcompanion.config;

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
    public boolean limitTamedWolves = false;
    public int maxTamedWolves = 8;
    public boolean limitWolfBags = false;
    public int maxWolfBags = 8;
    public boolean limitWolfBagInventoryStackSize = false;
    public int wolfBagInventoryStackSize = 32;

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
        copy.limitTamedWolves = this.limitTamedWolves;
        copy.maxTamedWolves = this.maxTamedWolves;
        copy.limitWolfBags = this.limitWolfBags;
        copy.maxWolfBags = this.maxWolfBags;
        copy.limitWolfBagInventoryStackSize = this.limitWolfBagInventoryStackSize;
        copy.wolfBagInventoryStackSize = this.wolfBagInventoryStackSize;
        return copy;
    }

    public void normalize() {
        if (this.doNotAttackMobs == null) {
            this.doNotAttackMobs = new ArrayList<>();
        }
        this.maxTamedWolves = Math.max(0, this.maxTamedWolves);
        this.maxWolfBags = Math.max(0, this.maxWolfBags);
        if (this.wolfBagInventoryStackSize != 8 && this.wolfBagInventoryStackSize != 16 && this.wolfBagInventoryStackSize != 32) {
            this.wolfBagInventoryStackSize = 32;
        }
    }

    public int wolfBagInventoryStackLimit() {
        return this.limitWolfBagInventoryStackSize ? this.wolfBagInventoryStackSize : 64;
    }
}
