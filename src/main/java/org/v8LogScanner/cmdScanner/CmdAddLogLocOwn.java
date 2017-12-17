package org.v8LogScanner.cmdScanner;

import org.v8LogScanner.LocalTCPLogScanner.LanScanProfile;
import org.v8LogScanner.LocalTCPLogScanner.V8LogScannerClient;
import org.v8LogScanner.cmdAppl.CmdCommand;

import java.util.List;

public class CmdAddLogLocOwn implements CmdCommand {

    @Override
    public String getTip() {
        return "";
    }

    @Override
    public void execute() {

        V8LogScannerAppl appl = V8LogScannerAppl.instance();

        List<V8LogScannerClient> clients = appl.clientsManager.getClients();

        String userInput = appl.getConsole().askInputFromList("select host:", clients);

        if (userInput == null)
            return;

        V8LogScannerClient client = clients.get(Integer.parseInt(userInput));

        String loc = appl.askLocation(client);

        if (loc != null) {
            LanScanProfile profile = (LanScanProfile) client.getProfile();
            appl.addLogPath(profile, loc);
        }
    }

}
