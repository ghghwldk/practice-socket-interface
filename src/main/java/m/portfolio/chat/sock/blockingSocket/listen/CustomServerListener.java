package m.portfolio.chat.sock.blockingSocket.listen;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import m.portfolio.chat.sock.blockingSocket.codec.decoder.Decodable;
import m.portfolio.chat.sock.blockingSocket.codec.endcoder.Encodable;
import m.portfolio.chat.sock.blockingSocket.codec.endcoder.LengthHeaderEncoder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

@Slf4j
@RequiredArgsConstructor
public class CustomServerListener implements Runnable{
    private final Socket client;
    private static final String welcomeMsg
            = "Welcome server!\r\n>";
    private final Decodable decodable;
    private final Encodable encodable;

    @Override
    public void run() {
        try (OutputStream os = client.getOutputStream();
             InputStream is = client.getInputStream()) {

            os.write(encodable.encode(welcomeMsg));

            waitAndEcho(is, os);
        } catch (Throwable e) {
            log.info(e.getMessage());
        } finally {
            log.info("Client disconnected IP address =" + client.getRemoteSocketAddress().toString());
        }
    }

    private void waitAndEcho(InputStream is, OutputStream os)
            throws IOException {
        while (true) {
            byte[] b = is.readAllBytes();
            String msg
                    = decodable.decode(b);

            os.write(
                    LengthHeaderEncoder.convert("echo : " + msg + ">")
            );
        }
    }
}