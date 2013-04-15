package com.dashwire.asset.server;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**

 */
public class InterfaceServer extends NanoHTTPD {

    public InterfaceServer( int port ) throws IOException {
        super( port, new File("/") );
    }


    public Response serve( String uri, String method, Properties header, Properties parms, Properties files ) {

        return new Response("200", "text/html", "<html></html>");
    }
}
