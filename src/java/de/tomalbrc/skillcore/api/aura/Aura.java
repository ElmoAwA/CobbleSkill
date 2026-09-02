package de.tomalbrc.skillcore.api.aura;

public interface Aura {
    void onStart();
    boolean asyncTick();
    void onAsyncTick();
    void onEnd(boolean runEnd);
    void onEntityHit();

    void addStack();
    void removeStack();
    int getStacks();
    boolean canStack();
    int maxStacks();

    void cancel();
}
