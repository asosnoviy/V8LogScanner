package org.v8LogScanner.LocalTCPLogScanner;

import org.v8LogScanner.commonly.ProcessEvent;
import org.v8LogScanner.rgx.IRgxSelector.SelectDirections;
import org.v8LogScanner.rgx.ScanProfile;
import org.v8LogScanner.rgx.SelectorEntry;

import java.util.List;

public interface V8LogScannerClient {

    public String getHostIP();

    public String getFinalInfo();

    public String getHostName();

    public boolean logPathExist(String path);

    public void reset();

    public void resetResult();

    public boolean pingServer();

    public List<String> scanLogsInCfgFile();

    public void startRgxOp();

    public ScanProfile getProfile();

    public void setProfile(ScanProfile profile);

    public int cursorIndex();

    public List<SelectorEntry> select(int count, SelectDirections forward);

    public void addListener(ProcessEvent e);

    public List<String> getCfgPaths();
}
