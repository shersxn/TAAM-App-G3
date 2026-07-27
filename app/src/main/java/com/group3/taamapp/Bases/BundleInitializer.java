package com.group3.taamapp.Bases;

import android.os.Bundle;

/**
 * Contain function about how to pass the data to fragment to go
 */
public interface BundleInitializer {
    /**
     * Pass the data to fragment to go
     * @param bundle bundle to be bound with the fragment
     */
    public abstract void initBundle(Bundle bundle);
}
