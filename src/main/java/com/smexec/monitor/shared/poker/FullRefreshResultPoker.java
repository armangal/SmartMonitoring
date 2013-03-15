package com.smexec.monitor.shared.poker;

import java.io.Serializable;

import com.smexec.monitor.shared.ConnectedServer;
import com.smexec.monitor.shared.FullRefreshResult;
import com.smexec.monitor.shared.RefreshResult;

public class FullRefreshResultPoker<R extends RefreshResult<CS>, CS extends ConnectedServer>
    extends FullRefreshResult<R, CS>
    implements Serializable {

    private static final long serialVersionUID = 1L;

}
