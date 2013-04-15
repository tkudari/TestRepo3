package com.dashwire.config.resources;

import android.content.Context;

import java.util.List;

/**
 * User: tbostelmann
 * Date: 1/11/13
 */
public interface FeedUtilities {
    public String getFeedCurEditionId( Context context ) throws IllegalArgumentException;
    public List<String> getFeedCurTagIds( Context context ) throws IllegalArgumentException;
}
