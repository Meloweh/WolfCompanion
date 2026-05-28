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
    public int extraHealth = 0;
    public boolean allowTeleport = true;
    public double teleportAtDistance = 544f;
    public double attackAcceleration = 1.2f;
    public double maxSpeed = 3f;
    public boolean allowPassiveRegeneration = true;
    public int passiveRegenerationRate = 15;
}
