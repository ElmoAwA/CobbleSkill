package de.tomalbrc.skillcore.ext;

import de.tomalbrc.skillcore.util.ThreatTable;

public interface ThreatTracker {
    ThreatTable getThreatTable();
    boolean isThreatTableEnabled();
    void setThreatTableEnabled(boolean enabled);
}