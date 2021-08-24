package external.webinterface;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoWSD;

import java.io.*;

public class WebInterface extends NanoHTTPD {

    private static final File webpage = new File("src/main/resources/index.html");
    private static WebInterface self;

    private NanoWSD socket;

    public static PrintStream out = System.out;

    public WebInterface(int port) throws IOException {
        super(port);

        if (self != null) {
            throw new ExceptionInInitializerError("WebInterface is a singleton, not allowed");
        } else {
            self = this;
        }

        socket = new Socket(8020);
        socket.start();

        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);

    }

    @Override
    public Response serve(IHTTPSession session) {
        try {
            return newChunkedResponse(
                    Response.Status.OK,
                    "text/html",
                    new FileInputStream(webpage)
            );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return newFixedLengthResponse(
                    Response.Status.INTERNAL_ERROR,
                    "text/text",
                    "Couldn't find webpage"
            );
        }
    }
}

class Socket extends NanoWSD {

    public Socket(int port) {
        super(port);
    }

    @Override
    protected WebSocket openWebSocket(IHTTPSession handshake) {
        return new SocketImpl(handshake);


    }

    static class SocketImpl extends WebSocket {

        public SocketImpl(IHTTPSession handshakeRequest) {
            super(handshakeRequest);
        }

        @Override
        protected void onOpen() {
            // ignore
        }

        @Override
        protected void onClose(WebSocketFrame.CloseCode code, String reason, boolean initiatedByRemote) {
            // TODO: kill program here
        }

        @Override
        protected void onMessage(WebSocketFrame message) {
            System.out.println(message.toString());
        }

        @Override
        protected void onPong(WebSocketFrame pong) {

        }

        @Override
        protected void onException(IOException exception) {

        }
    }
}

class WebPrintStream extends PrintStream {

    // TODO: Actually forward the input to websocket
    public WebPrintStream(OutputStream out) {
        super(out);
    }
}