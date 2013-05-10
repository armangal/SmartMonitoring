package com.smexec.monitor.client.widgets;

import com.google.gwt.user.client.ui.InlineLabel;
import com.smexec.monitor.shared.utils.RefreshProgress;

public class ProgressLabel
    extends InlineLabel {

    private RefreshProgress lastRefProg = RefreshProgress.ONE;

    public ProgressLabel() {
        setText(lastRefProg.getValue());
        setTitle("Refresh progress visualization.");
    }

    public void progress() {
        lastRefProg = lastRefProg.getNext();
        setText(lastRefProg.getValue());

    }

    public void reset() {
        lastRefProg = RefreshProgress.ONE;
        setText(lastRefProg.getValue());
    }

}
