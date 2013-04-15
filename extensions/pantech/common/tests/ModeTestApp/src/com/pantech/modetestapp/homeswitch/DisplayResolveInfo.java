package com.pantech.modetestapp.homeswitch;


import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

public class DisplayResolveInfo {
    ResolveInfo ri;
    CharSequence displayLabel;
    Drawable displayIcon;
    CharSequence extendedInfo;
    Intent origIntent;
    boolean bDefault;

    DisplayResolveInfo(ResolveInfo pri, CharSequence pLabel,
            CharSequence pInfo, Intent pOrigIntent) {
        ri = pri;
        displayLabel = pLabel;
        extendedInfo = pInfo;
        origIntent = pOrigIntent;
    }
}
