package github.meloweh.wolfcompanion.client.screen.config;

import github.meloweh.wolfcompanion.config.WolfConfig;

enum ConfigPage {
    SURVIVAL("Survival") {
        @Override
        void build(WolfConfigScreen screen, WolfConfig draft, int left, int y, int width) {
            y = screen.addBoolean(left, y, width, "Respawn wolves", draft.canRespawn, value -> draft.canRespawn = value);
            y = screen.addBoolean(left, y, width, "Keep bag on wolf death", draft.keepWolfBag, value -> draft.keepWolfBag = value);
            y = screen.addBoolean(left, y, width, "Keep inventory on wolf death", draft.keepWolfInventory, value -> draft.keepWolfInventory = value);
            y = screen.addBoolean(left, y, width, "Keep armor on wolf death", draft.keepWolfArmor, value -> draft.keepWolfArmor = value);
            screen.addBoolean(left, y, width, "Keep XP on wolf death", draft.keepXp, value -> draft.keepXp = value);
        }
    },
    RECOVERY("Recovery") {
        @Override
        void build(WolfConfigScreen screen, WolfConfig draft, int left, int y, int width) {
            y = screen.addBoolean(left, y, width, "Shake off poison", draft.canShakeOffPoison, value -> draft.canShakeOffPoison = value);
            y = screen.addBoolean(left, y, width, "Shake off fire", draft.canShakeOffFire, value -> draft.canShakeOffFire = value);
            y = screen.addBoolean(left, y, width, "Passive regeneration", draft.allowPassiveRegeneration, value -> draft.allowPassiveRegeneration = value);
            screen.addInteger(left, y, width, "Passive regen rate", draft.passiveRegenerationRate, 1, value -> draft.passiveRegenerationRate = value);
        }
    },
    TELEPORT("Teleport") {
        @Override
        void build(WolfConfigScreen screen, WolfConfig draft, int left, int y, int width) {
            y = screen.addBoolean(left, y, width, "Allow teleport", draft.allowTeleport, value -> draft.allowTeleport = value);
            y = screen.addBoolean(left, y, width, "Teleport sitting wolves", draft.canTeleportSitting, value -> draft.canTeleportSitting = value);
            screen.addDouble(left, y, width, "Teleport distance", draft.teleportAtDistance, 0.0, value -> draft.teleportAtDistance = value);
        }
    },
    FOOD("Food") {
        @Override
        void build(WolfConfigScreen screen, WolfConfig draft, int left, int y, int width) {
            y = screen.addBoolean(left, y, width, "Pick up food", draft.canPickupFood, value -> draft.canPickupFood = value);
            y = screen.addInteger(left, y, width, "Max pickup food", draft.maxPickupFood, 0, value -> draft.maxPickupFood = value);
            y = screen.addBoolean(left, y, width, "Pick all rotten flesh", draft.pickAllRottenFlesh, value -> draft.pickAllRottenFlesh = value);
            y = screen.addBoolean(left, y, width, "Reserve player food", draft.shouldCarePlayerFood, value -> draft.shouldCarePlayerFood = value);
            screen.addInteger(left, y, width, "Required player food", draft.requiredPlayerFood, 0, value -> draft.requiredPlayerFood = value);
        }
    },
    COMBAT("Combat") {
        @Override
        void build(WolfConfigScreen screen, WolfConfig draft, int left, int y, int width) {
            y = screen.addDouble(left, y, width, "Attack acceleration", draft.attackAcceleration, 0.0, value -> draft.attackAcceleration = value);
            y = screen.addDouble(left, y, width, "Max attack speed", draft.maxSpeed, 0.0, value -> draft.maxSpeed = value);
            y = screen.addBoolean(left, y, width, "Whistle mob targeting", draft.allowWhistleMobTargeting, value -> draft.allowWhistleMobTargeting = value);
            screen.addStringList(left, y, width, "Do not attack mobs/tags", draft.doNotAttackMobs, value -> draft.doNotAttackMobs = value);
        }
    },
    LIMITS("Limits") {
        @Override
        void build(WolfConfigScreen screen, WolfConfig draft, int left, int y, int width) {
            y = screen.addBoolean(left, y, width, "Limit tamed wolves", draft.limitTamedWolves, value -> draft.limitTamedWolves = value);
            y = screen.addInteger(left, y, width, "Max tamed wolves", draft.maxTamedWolves, 0, value -> draft.maxTamedWolves = value);
            y = screen.addBoolean(left, y, width, "Limit equipped bags", draft.limitWolfBags, value -> draft.limitWolfBags = value);
            y = screen.addInteger(left, y, width, "Max equipped bags", draft.maxWolfBags, 0, value -> draft.maxWolfBags = value);
            y = screen.addBoolean(left, y, width, "Limit bag stack size", draft.limitWolfBagInventoryStackSize, value -> draft.limitWolfBagInventoryStackSize = value);
            screen.addIntegerChoice(left, y, width, "Bag stack size", draft.wolfBagInventoryStackSize, value -> draft.wolfBagInventoryStackSize = value, 32, 16, 8);
        }
    };

    private final String title;

    ConfigPage(String title) {
        this.title = title;
    }

    String title() {
        return title;
    }

    abstract void build(WolfConfigScreen screen, WolfConfig draft, int left, int y, int width);
}
