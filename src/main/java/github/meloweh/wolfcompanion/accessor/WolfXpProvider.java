package github.meloweh.wolfcompanion.accessor;

public interface WolfXpProvider {
    void setXp(int value);
    int getXp();
    int getLevel();
    int getNextLevelXpRequirement(final int level);
    int getDeltaXp();
    void addXp(int value);
}
