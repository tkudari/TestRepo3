package com.dashwire.config.ui;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dashwire.base.debug.DashLogger;
import com.dashwire.config.ApplicationItem;
import com.dashwire.config.DownloadService;
import com.dashwire.config.R;
import com.dashwire.config.tracking.Tracker;

public class AppInstallationActivity extends ListActivity {

    protected static final String TAG = AppInstallationActivity.class.getCanonicalName();
    private ApplicationAdapter appListAdapter;
    private boolean isBound;
    private DownloadService downloadService;
    private Handler messageHandler;
    private Button stopAll;
    private TextView title;
    private Context context = this;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected( ComponentName className, IBinder service ) {
        	DashLogger.d(TAG, "Entering onServiceConnected.");
            if(downloadService != null) {
        		DashLogger.e(TAG, "Download service not null before onServiceConnected");
        	}
        	downloadService = ( ( DownloadService.DownloadBinder ) service ).getService();
            if ( downloadService != null ) {
                downloadService.setAppInstallHandle( AppInstallationActivity.this );
                setupMessageHandler();
                populateList();
                DashLogger.v( TAG, "download service connected" );
            } else {
                DashLogger.v( TAG, "Error on connecting download service" );
            }
        }

        public void onServiceDisconnected( ComponentName className ) {
        	DashLogger.d(TAG, "Entering onServiceDisconnected.");
            if ( downloadService != null ) {   	
	        	downloadService.setAppInstallHandle( null ); 
	            downloadService = null;
        	}
            DashLogger.v( TAG, "download service disconnected" );
        }
    };

    void doBindService() {
    	DashLogger.d(TAG, "Entering doBindService.");
        
        Intent downloadIntent = new Intent( this, DownloadService.class );
        bindService( downloadIntent, serviceConnection, Context.BIND_AUTO_CREATE );
        isBound = true;
    }

    protected void setupMessageHandler() {
        messageHandler = new Handler() {
            @Override
            public void handleMessage( Message message ) {
                invalidate( ( String ) message.obj );
                super.handleMessage( message );
            }
        };
        downloadService.setMessageHandler( messageHandler );
    }

    protected void invalidate( String packageName ) {
        appListAdapter.notifyDataSetChanged();
    }

    void doUnbindService() {
    	DashLogger.d(TAG, "Entering doUnbindService.");
        if ( isBound ) {
            unbindService( serviceConnection );
            isBound = false;
        } else
        {
        	DashLogger.e(TAG, "doUnbindService called while isBound is null");
        }
    }

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        setContentView( R.layout.app_installation );
        Object state = getLastNonConfigurationInstance();
        if ( state != null ) {
            appListAdapter = ( ApplicationAdapter ) state;
        } else {
            appListAdapter = new ApplicationAdapter( this, R.layout.application_row, R.id.app_installation_item_text );
        }
        title = ( TextView ) findViewById( R.id.app_installation_title );
        title.setText( context.getResources().getString( R.string.app_installed ) );

        stopAll = ( Button ) findViewById( R.id.stopAllButton );
        stopAll.setOnClickListener( new View.OnClickListener() {
            public void onClick( View v ) {
                // TODO Auto-generated method stub
                Tracker.track(context, "app_install/cancel_all_btn");
                AlertDialog.Builder builder = new AlertDialog.Builder( AppInstallationActivity.this );
                builder.setMessage( R.string.stop_all_confirm );
                builder.setPositiveButton( context.getResources().getString( R.string.button_yes ), new DialogInterface.OnClickListener() {
                    public void onClick( DialogInterface dialog, int id ) {
                        Tracker.track(context, "app_install/cancel_all_confirmed");
                        downloadService.cancelAll();
                        AppInstallationActivity.this.finish();
                    }
                } );
                builder.setNegativeButton( context.getResources().getString( R.string.button_no ), new DialogInterface.OnClickListener() {
                    public void onClick( DialogInterface dialog, int id ) {
                        // do nothing yet..
                        Tracker.track(context, "app_install/cancel_all_declined");
                    }
                } );
                AlertDialog cancelAllConfirm = builder.create();
                cancelAllConfirm.show();
            }
        } );
        stopAll.setVisibility( View.GONE );
        setListAdapter( appListAdapter );
        
    }

    @Override
    protected void onStart() {
    	super.onStart();
    	doBindService();
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	if ( downloadService != null && downloadService.isCompleted() ) {
            downloadService.clearDownloadService();
        }
    	doUnbindService();
    }
    

    @Override
    protected void onResume() {
        Tracker.track(context, "app_install/activity_viewed");
        updateList();
        updateScreenStatus();
        super.onResume();
    }

    public void updateList() {
        if ( downloadService != null ) {
            populateList();
        }
    }

    private void populateList() {
        ArrayList<ApplicationItem> items = downloadService.getApplicationList();
        if ( items != null && items.size()>0) {
            appListAdapter.clear();
            for ( ApplicationItem item : items ) {
                appListAdapter.add( item );
                if ( !item.isInstalled() ) {
                    title.setText( context.getResources().getString( R.string.app_installing ) );
                    stopAll.setVisibility( View.VISIBLE );
                }
            }
        } else {
            DashLogger.v( TAG, "Applicaton List is empty" );
        }
    }

    class ApplicationAdapter extends ArrayAdapter<ApplicationItem> {

        ApplicationAdapter( Context context, int textViewResourceId, int applicationRowText ) {
            super( context, textViewResourceId, applicationRowText );
        }

        public View getView( final int position, View convertView, ViewGroup parent ) {

            ApplicationItem item = getItem( position );
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate( R.layout.application_row, parent, false );

            RelativeLayout installedLayout = ( RelativeLayout ) row.findViewById( R.id.installedLayout );
            TextView itemProgressTitle = ( TextView ) row.findViewById( R.id.app_installation_item_text2 );
            itemProgressTitle.setText( item.getTitle() );
            ProgressBar itemProgress = ( ProgressBar ) row.findViewById( R.id.progress_bar );
            ImageView itemStopIcon = ( ImageView ) row.findViewById( R.id.app_installation_stop_icon );
            ProgressBar installProgress = ( ProgressBar ) row.findViewById( R.id.app_install_spinner );

            itemStopIcon.setOnClickListener( new View.OnClickListener() {
                public void onClick( View v ) {
                    ApplicationItem item = getItem( position );
                    Tracker.track(context, "app_install/download_cancelled/" + item.getPackageName());
                    downloadService.cancelDownload( item );
                    invalidate( item.getPackageName() );
                    updateScreenStatus();
                }
            } );

            RelativeLayout installingLayout = ( RelativeLayout ) row.findViewById( R.id.installingLayout );
            TextView itemLabel = ( TextView ) row.findViewById( R.id.app_installation_item_text );
            itemLabel.setText( item.getTitle() );
            ImageView itemStatusIcon = ( ImageView ) row.findViewById( R.id.app_installation_item_icon );

            if ( item.getStatus() == ApplicationItem.INSTALLING ) {
                itemProgressTitle.setSelected( true );
                itemProgress.setProgress( item.getDownloadProgress() );
                installingLayout.setVisibility( View.VISIBLE );
                installedLayout.setVisibility( View.GONE );
                installProgress.setVisibility( View.VISIBLE );
                itemStopIcon.setVisibility( View.GONE );
            } else if ( item.getStatus() == ApplicationItem.INSTALLED ) {
                installingLayout.setVisibility( View.GONE );
                installedLayout.setVisibility( View.VISIBLE );
                itemStatusIcon.setVisibility( View.VISIBLE );
                itemStatusIcon.setImageResource( R.drawable.checked );
            } else if ( item.getStatus() == ApplicationItem.DOWNLOADING ) {
                itemProgress.setProgress( item.getDownloadProgress() );
                installedLayout.setVisibility( View.GONE );
                installingLayout.setVisibility( View.VISIBLE );
            } else if ( item.getStatus() == ApplicationItem.CANCELED ) {
                installingLayout.setVisibility( View.GONE );
                installedLayout.setVisibility( View.VISIBLE );
                itemStatusIcon.setVisibility( View.GONE );
                itemLabel.setTextColor( 0xFF555555 );
            } else {
                itemProgressTitle.setText( item.getTitle() );
                itemProgress.setProgress( item.getDownloadProgress() );
                installedLayout.setVisibility( View.GONE );
                installingLayout.setVisibility( View.VISIBLE );
            }

            return ( row );
        }
    }
    
    public void updateScreenStatus() {
        if ( downloadService != null && downloadService.isCompleted() ) {
            title.setText( context.getResources().getString( R.string.app_installed ) );
            stopAll.setVisibility( View.GONE );
        }
    }
}
