package m.portfolio.chat.sock.sock;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import m.portfolio.chat.sock.handler.Handleable;
import m.portfolio.chat.sock.listen.CustomClientListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.Executors;

@Slf4j
public class ClientSockExtended extends BaseSock implements ClientSock{
    @Getter
    private OutputStream os = null;
    private final String hostname;
    private final Handleable handleable;
    private Socket client = new Socket();

    public ClientSockExtended(int port, String hostname, Handleable handleable) {
        super(port, Executors.newSingleThreadExecutor());

        this.hostname = hostname;
        this.handleable = handleable;
    }

    @Override
    public void start() {
        try{
            InetSocketAddress ipep
                    = new InetSocketAddress(this.hostname, this.port);

            client.connect(ipep);

            try{
                os = client.getOutputStream();
                InputStream is = client.getInputStream();

                log.info("Client connected IP address =" + client.getRemoteSocketAddress().toString());


                this.executorService.execute(new CustomClientListener(handleable, is));
            } catch (Exception e){
                log.info(e.getMessage());
            }
        } catch (Throwable e) {
            log.info(e.getMessage());
        }
    }

    @Override
    public void clear() throws IOException {
        client.close();
    }
}
