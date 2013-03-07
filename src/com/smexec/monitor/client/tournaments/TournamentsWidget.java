package com.smexec.monitor.client.tournaments;

import java.util.Date;
import java.util.Random;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.smexec.monitor.client.utils.ClientStringFormatter;
import com.smexec.monitor.client.widgets.AbstractMonitoringWidget;
import com.smexec.monitor.shared.LobbyChunkStats;
import com.smexec.monitor.shared.LobbySeverStats;
import com.smexec.monitor.shared.RefreshResult;

public class TournamentsWidget
    extends AbstractMonitoringWidget {

    private DateTimeFormat format = DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_SHORT);
    FlexTable tournamentStatus = new FlexTable();
    FlexTable interruptedTable = new FlexTable();
    FlowPanel rightPanel = new FlowPanel();
    FlowPanel left = new FlowPanel();

    private HTML regPlayers = new HTML();
    private HTML playingPlayers = new HTML();
    private HTML interrupedPlayers = new HTML();

    public TournamentsWidget() {
        super("Tournaments");
        addStyleName("tournamentsWidget");

        createLeft();
        createRight();

        ScrollPanel sp = new ScrollPanel(rightPanel);
        sp.setHeight("100%");
        FlowPanel fp = new FlowPanel();
        fp.add(left);
        fp.add(sp);
        fp.setStyleName("tournamentsWidgetInternal");
        getDataPanel().add(fp);

        regPlayers.setTitle("Currently registered players");
        playingPlayers.setTitle("Currently playing players, exclusding dropped");
        interrupedPlayers.setTitle("Number of players affected by interruption");
    }

    public void update(RefreshResult result) {

        Random r = new Random();
        int i = 1;
        LobbySeverStats lss = result.getLobbySeverStats();
        LobbyChunkStats lc = lss.getLastChunk();
        tournamentStatus.setText(i++, 1, ClientStringFormatter.formatNumber(lc.getRealTournamentsInRegisterStatus()));
        regPlayers.setText(ClientStringFormatter.formatNumber(lc.getRealRegisteredPlayers()));

        tournamentStatus.setText(i++, 1, ClientStringFormatter.formatNumber(lc.getRealActiveTournaments()));
        playingPlayers.setText(ClientStringFormatter.formatNumber(lc.getRealTournamentPlayers()));

        tournamentStatus.setText(i++, 1, ClientStringFormatter.formatNumber(r.nextInt(33)));
        interrupedPlayers.setText(ClientStringFormatter.formatNumber(r.nextInt(5000)));

        interruptedTable.removeFromParent();
        interruptedTable = new FlexTable();
        createRight();

        for (i = 1; i < 50; i++) {
            HTML code = new HTML("" + r.nextInt(1223343) + " (234)");
            code.setTitle("code");
            interruptedTable.setWidget(i, 0, code);
            interruptedTable.setText(i, 1, "Interrupted" + i);
            interruptedTable.setText(i, 2, format.format(new Date()));
            interruptedTable.setText(i++, 3, ClientStringFormatter.formatNumber(r.nextInt(5000)));
        }
    }

    private void createLeft() {
        left.setStyleName("left");
        tournamentStatus.getElement().setId("infoTable");
        left.add(tournamentStatus);

        int i = 0, j = 0;
        tournamentStatus.setText(i, j++, "Status");
        tournamentStatus.setText(i, j++, "Amount");
        tournamentStatus.setText(i, j++, "Players");
        tournamentStatus.getRowFormatter().getElement(i++).setId("th");
        tournamentStatus.setText(i, 0, "Registering:");
        tournamentStatus.setWidget(i++, 2, regPlayers);

        tournamentStatus.setText(i, 0, "Started:");
        tournamentStatus.setWidget(i++, 2, playingPlayers);

        tournamentStatus.setText(i, 0, "Interrupted (12h):");
        tournamentStatus.setWidget(i++, 2, interrupedPlayers);
    }

    private void createRight() {
        rightPanel.setStyleName("right");
        interruptedTable.getElement().setId("infoTable");
        rightPanel.add(interruptedTable);

        int i = 0, j = 0;
        interruptedTable.setText(i, j++, "Tr.Code");
        interruptedTable.setText(i, j++, "Name");
        interruptedTable.setText(i, j++, "Time");
        interruptedTable.setText(i, j++, "Players");
        interruptedTable.getRowFormatter().getElement(i).setId("th");

    }
}
