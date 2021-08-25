package external.webinterface;

import external.util.InputExtractor;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoWSD;

import java.io.*;
import java.util.Locale;

public class WebInterface extends NanoHTTPD {

    private static final File webpage = new File("src/main/resources/index.html");
    private static WebInterface self;

    private final Socket socket;

    public static PrintStream out;
    public static VirtualGamepad vgp;

    public WebInterface(int port) throws IOException {
        super(port);

        vgp = new VirtualGamepad();
        socket = new Socket(8020);
        out = new WebPrintStream(socket.active);

        if (self != null) {
            throw new ExceptionInInitializerError("WebInterface is a singleton, not allowed");
        } else {
            self = this;
        }

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

    WebSocket a;
    final InputExtractor<WebSocket> active = () -> a;

    public Socket(int port) {
        super(port);
    }

    @Override
    protected WebSocket openWebSocket(IHTTPSession handshake) {
        a = new SocketImpl(handshake);
        return a;
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
//            System.out.println(message.getTextPayload());
            String payload = message.getTextPayload();
            String command = payload.substring(0, 4);
            String arg = payload.substring(4);

            if (command.equals("DOWN")) { // key down
                WebInterface.vgp.keyHandler(arg.toLowerCase(), true);
            } else if (command.equals("KYUP")) { // key up
                WebInterface.vgp.keyHandler(arg.toLowerCase(), false);
            }
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

    public WebPrintStream(InputExtractor<NanoWSD.WebSocket> socket) {
        super(new BufferedOutputStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                try {
                    socket.getValue().send(String.valueOf((char) (b & 0xFF)));
                } catch (NullPointerException e) {
                    System.out.println("The socket has not been connected yet. Given data has been dropped.");
                }
            }
        }));

        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                flush();
            }
        }).start();

    }
}