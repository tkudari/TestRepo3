package com.pantech.modetestapp.homeswitch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.PatternMatcher;
import android.util.Log;

public class PackageMgr {
	private PackageManager mPm;
	private ArrayList<DisplayResolveInfo> mList;
	private Intent mIntent;
	///////////////////////////////////////////////////////////////////////////
	public PackageMgr(Context context) {
		mPm = context.getPackageManager();
	} // End of Constructor
	///////////////////////////////////////////////////////////////////////////
	public PackageManager getPackageManager() {
		return mPm;
	} // End of getPackageManager
	///////////////////////////////////////////////////////////////////////////
	public int getPackageSize() {
		if (null == mList)
			return 0;
		return mList.size();
	} // End of getPackageSize
	///////////////////////////////////////////////////////////////////////////
	public DisplayResolveInfo getPackageInfo(int iIndex) {
		if (null == mList)
			return null;		
		return mList.get(iIndex);
	} // End of getPackageInfo
	///////////////////////////////////////////////////////////////////////////
    public Intent intentForPosition(int position) {
        if (null == mList) {
            return null;
        } // End of intentForPosition

        DisplayResolveInfo dri = mList.get(position);
        
        Intent intent = new Intent(dri.origIntent != null
                ? dri.origIntent : mIntent);
        intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT
                |Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
        ActivityInfo ai = dri.ri.activityInfo;
        intent.setComponent(new ComponentName(
                ai.applicationInfo.packageName, ai.name));
        return intent;
    } // End of 	intentForPosition
	///////////////////////////////////////////////////////////////////////////
    public int findPantechHome(boolean isSimple) {
    	final String szHome[] = {"com.pantech.simplehome", "com.pantech.launcher2"};
    	int iIndex = 0;
		int mode = isSimple ? 0 : 1;
    	
    	for (DisplayResolveInfo info : mList) {
    		if ((info.ri != null) &&
    			(info.ri.activityInfo != null) && 
    			(info.ri.activityInfo.packageName != null))
    		{
	    		if (szHome[mode].equals(info.ri.activityInfo.packageName)) {
	    			return iIndex;
	    		} // End of if
    		} // End of if
    		iIndex++;
    	} // End of for
    	return -1;
    } // End of findPantechHome
	///////////////////////////////////////////////////////////////////////////
	public void getPackageList(Intent intent, Intent[] initialIntents) {
		List<ResolveInfo> rList;
		
		rList = mPm.queryIntentActivities(
                intent, PackageManager.MATCH_DEFAULT_ONLY | PackageManager.GET_RESOLVED_FILTER);		
		
		mIntent = new Intent(intent);
		
        int N;
        if ((rList != null) && ((N = rList.size()) > 0)) {
            // Only display the first matches that are either of equal
            // priority or have asked to be default options.
            ResolveInfo r0 = rList.get(0);
            for (int i=1; i<N; i++) {
                ResolveInfo ri = rList.get(i);
               if (r0.priority != ri.priority ||
            	   r0.isDefault != ri.isDefault)
               {
                    while (i < N) {
                        rList.remove(i);
                        N--;
                    } // End of while
                } // End of if
            } // End of for
            if (N > 1) {
                ResolveInfo.DisplayNameComparator rComparator =
                        new ResolveInfo.DisplayNameComparator(mPm);
                Collections.sort(rList, rComparator);
            } // End of if
            
            mList = new ArrayList<DisplayResolveInfo>();
            
            // First put the initial items at the top.
            if (initialIntents != null) {
                for (int i=0; i<initialIntents.length; i++) {
                    Intent ii = initialIntents[i];
                    if (ii == null) {
                        continue;
                    } // End of if
                    ActivityInfo ai = ii.resolveActivityInfo(mPm, 0);
                    if (ai == null) {
                        Log.w("ResolverActivity", "No activity found for "
                                + ii);
                        continue;
                    } // End of if
                    ResolveInfo ri = new ResolveInfo();
                    ri.activityInfo = ai;
                    if (ii instanceof LabeledIntent) {
                        LabeledIntent li = (LabeledIntent)ii;
                        ri.resolvePackageName = li.getSourcePackage();
                        ri.labelRes = li.getLabelResource();
                        ri.nonLocalizedLabel = li.getNonLocalizedLabel();
                        ri.icon = li.getIconResource();
                    } // End of if
                    mList.add(new DisplayResolveInfo(ri,
                            ri.loadLabel(mPm), null, ii));
                } // End of for
            } // End of if
            
            // Check for applications with same name and use application name or
            // package name if necessary
            r0 = rList.get(0);
            int start = 0;
            CharSequence r0Label =  r0.loadLabel(mPm);
            for (int i = 1; i < N; i++) {
                if (r0Label == null) {
                    r0Label = r0.activityInfo.packageName;
                } // End of if
                ResolveInfo ri = rList.get(i);
                CharSequence riLabel = ri.loadLabel(mPm);
                if (riLabel == null) {
                    riLabel = ri.activityInfo.packageName;
                } // End of if
                if (riLabel.equals(r0Label)) {
                    continue;
                } // End of if
                processGroup(rList, start, (i-1), r0, r0Label);
                r0 = ri;
                r0Label = riLabel;
                start = i;
            } // End of for
            // Process last group
            processGroup(rList, start, (N-1), r0, r0Label);
        } // End of if
        
        findDefult();
	} // End of getPackageList
	///////////////////////////////////////////////////////////////////////////
    private void processGroup(List<ResolveInfo> rList, int start, int end, ResolveInfo ro,
            CharSequence roLabel) {
        // Process labels from start to i
        int num = end - start+1;
        if (num == 1) {
            // No duplicate labels. Use label for entry at start
            mList.add(new DisplayResolveInfo(ro, roLabel, null, null));
        } else {
            boolean usePkg = false;
            CharSequence startApp = ro.activityInfo.applicationInfo.loadLabel(mPm);
            if (startApp == null) {
                usePkg = true;
            }
            if (!usePkg) {
                // Use HashSet to track duplicates
                HashSet<CharSequence> duplicates = new HashSet<CharSequence>();
                duplicates.add(startApp);
                for (int j = start+1; j <= end ; j++) {
                    ResolveInfo jRi = rList.get(j);
                    CharSequence jApp = jRi.activityInfo.applicationInfo.loadLabel(mPm);
                    if ( (jApp == null) || (duplicates.contains(jApp))) {
                        usePkg = true;
                        break;
                    } else {
                        duplicates.add(jApp);
                    } // End of if
                } // End of if
                // Clear HashSet for later use
                duplicates.clear();
            } // End of if
            for (int k = start; k <= end; k++) {
                ResolveInfo add = rList.get(k);
                if (usePkg) {
                    // Use application name for all entries from start to end-1
                    mList.add(new DisplayResolveInfo(add, roLabel,
                            add.activityInfo.packageName, null));
                } else {
                    // Use package name for all entries from start to end-1
                    mList.add(new DisplayResolveInfo(add, roLabel,
                            add.activityInfo.applicationInfo.loadLabel(mPm), null));
                } // End of if
            } // End of for
        } // End of if        
    } // End of processGroup
	///////////////////////////////////////////////////////////////////////////
    protected void findDefult() {
    	for (DisplayResolveInfo info : mList) {
    		info.bDefault = findDefault(info);
    	} // End of for
    } // End of setDefult
	///////////////////////////////////////////////////////////////////////////
    protected boolean findDefault(DisplayResolveInfo info) {
        List<IntentFilter> intentList = new ArrayList<IntentFilter>();
        List<ComponentName> prefActList = new ArrayList<ComponentName>();
        final String packageName = info.ri.activityInfo.packageName;
        
        mPm.getPreferredActivities(intentList, prefActList, packageName);
        if (prefActList.size() <= 0) {
        	return false;
        } // End of if
       	return true;
    } // End of findDefault
	///////////////////////////////////////////////////////////////////////////
    public void clearDefault() {
    	for (DisplayResolveInfo info : mList) {
    		if (true == info.bDefault) {
    			mPm.clearPackagePreferredActivities(info.ri.activityInfo.packageName);
    			info.bDefault = false;
    		} // End of if
    	} // End of for    	
    } // End of clearDefault
	///////////////////////////////////////////////////////////////////////////
    public ResolveInfo resolveInfoForPosition(int position) {
        if (null == mList) {
            return null;
        } // End of if
        return mList.get(position).ri;
    } // End of resolveInfoForPosition
	///////////////////////////////////////////////////////////////////////////
    public void setDefault(int iIndex, Context context) {
        ResolveInfo ri = resolveInfoForPosition(iIndex);
        Intent intent = intentForPosition(iIndex);

        if (true) {
            // Build a reasonable intent filter, based on what matched.
            IntentFilter filter = new IntentFilter();

            if (intent.getAction() != null) {
                filter.addAction(intent.getAction());
            }
            Set<String> categories = intent.getCategories();
            if (categories != null) {
                for (String cat : categories) {
                    filter.addCategory(cat);
                }
            }
            filter.addCategory(Intent.CATEGORY_DEFAULT);

            int cat = ri.match&IntentFilter.MATCH_CATEGORY_MASK;
            Uri data = intent.getData();
            if (cat == IntentFilter.MATCH_CATEGORY_TYPE) {
                String mimeType = intent.resolveType(context);
                if (mimeType != null) {
                    try {
                        filter.addDataType(mimeType);
                    } catch (IntentFilter.MalformedMimeTypeException e) {
                        Log.w("ResolverActivity", e);
                        filter = null;
                    }
                }
            }
            if (data != null && data.getScheme() != null) {
                // We need the data specification if there was no type,
                // OR if the scheme is not one of our magical "file:"
                // or "content:" schemes (see IntentFilter for the reason).
                if (cat != IntentFilter.MATCH_CATEGORY_TYPE
                        || (!"file".equals(data.getScheme())
                                && !"content".equals(data.getScheme()))) {
                    filter.addDataScheme(data.getScheme());
    
                    // Look through the resolved filter to determine which part
                    // of it matched the original Intent.
                    Iterator<IntentFilter.AuthorityEntry> aIt = ri.filter.authoritiesIterator();
                    if (aIt != null) {
                        while (aIt.hasNext()) {
                            IntentFilter.AuthorityEntry a = aIt.next();
                            if (a.match(data) >= 0) {
                                int port = a.getPort();
                                filter.addDataAuthority(a.getHost(),
                                        port >= 0 ? Integer.toString(port) : null);
                                break;
                            }
                        }
                    }
                    Iterator<PatternMatcher> pIt = ri.filter.pathsIterator();
                    if (pIt != null) {
                        String path = data.getPath();
                        while (path != null && pIt.hasNext()) {
                            PatternMatcher p = pIt.next();
                            if (p.match(path)) {
                                filter.addDataPath(p.getPath(), p.getType());
                                break;
                            }
                        }
                    }
                }
            }

            if (filter != null) {
                final int N = mList.size();
                ComponentName[] set = new ComponentName[N];
                int bestMatch = 0;
                for (int i=0; i<N; i++) {
                    ResolveInfo r = mList.get(i).ri;
                    set[i] = new ComponentName(r.activityInfo.packageName,
                            r.activityInfo.name);
                    if (r.match > bestMatch) bestMatch = r.match;
                }
                getPackageManager().addPreferredActivity(filter, bestMatch, set,
                        intent.getComponent());
            }
        }
        if (intent != null) {
            context.startActivity(intent);
        } // End of if
    } // End of setDefault
    ///////////////////////////////////////////////////////////////////////////
    
	public String getCurrentHome(){
    	for (DisplayResolveInfo info : mList) {
    		if (true == info.bDefault) {
    			mPm.clearPackagePreferredActivities(info.ri.activityInfo.packageName);
				
				return info.ri.activityInfo.packageName;
    		} 
    	}   	
		return null;
	}
}
