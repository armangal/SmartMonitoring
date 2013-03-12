package com.smexec.monitor.client.poker.tournaments;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.smexec.monitor.client.utils.ClientStringFormatter;
import com.smexec.monitor.client.widgets.AbstractMonitoringWidget;
import com.smexec.monitor.shared.poker.LobbyChunkStats;
import com.smexec.monitor.shared.poker.LobbySeverStats;
import com.smexec.monitor.shared.poker.RefreshResultPoker;
import com.smexec.monitor.shared.poker.Tournament;

public class TournamentsWidget
    extends AbstractMonitoringWidget {

    private DateTimeFormat format = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss");

    FlexTable tournamentStatus = new FlexTable();
    FlexTable interruptedTable = new FlexTable();
    FlowPanel rightPanel = new FlowPanel();
    FlowPanel left = new FlowPanel();
    ScrollPanel spInterrupted = new ScrollPanel();

    private HTML regPlayers = new HTML();
    private HTML playingPlayers = new HTML();
    private HTML interrupedPlayers = new HTML();

    public TournamentsWidget() {
        super("Tournaments");
        addStyleName("tournamentsWidget");

        createLeft();
        createRight();

        FlowPanel fp = new FlowPanel();
        fp.add(left);
        fp.add(rightPanel);
        fp.setStyleName("tournamentsWidgetInternal");
        getDataPanel().add(fp);

        regPlayers.setTitle("Currently registered players");
        playingPlayers.setTitle("Currently playing players, exclusding dropped");
        interrupedPlayers.setTitle("Number of players affected by interruption");
    }

    public void update(RefreshResultPoker result) {

        int i = 1;
        LobbySeverStats lss = result.getLobbySeverStats();
        LobbyChunkStats lc = lss.getLastChunk();
        tournamentStatus.setText(i++, 1, ClientStringFormatter.formatNumber(lc.getRealTournamentsInRegisterStatus()));
        regPlayers.setText(ClientStringFormatter.formatNumber(lc.getRealRegisteredPlayers()));

        tournamentStatus.setText(i++, 1, ClientStringFormatter.formatNumber(lc.getRealActiveTournaments()));
        playingPlayers.setText(ClientStringFormatter.formatNumber(lc.getRealTournamentPlayers()));

        createInterruptedTable();

        LinkedList<Tournament> interrupted = result.getGameServerStats().getInterrupted();
        Collections.sort(interrupted, new Comparator<Tournament>() {

            @Override
            public int compare(Tournament o1, Tournament o2) {
                try {
                    Date d1 = format.parse(o1.getDate());
                    Date d2 = format.parse(o2.getDate());
                    return d2.compareTo(d1);
                } catch (Exception e) {
                    Log.error(e.getMessage());
                    return 0;
                }
            }
        });

        Iterator<Tournament> it = interrupted.iterator();
        int intPlayers = 0;
        for (i = 1; i < interrupted.size()+1; i++) {
            Tournament t = it.next();
            HTML code = new HTML("" + t.getCode() + " (" + t.getServerCode() + ")");
            code.setTitle("" + t.getCode() + " (" + t.getServerCode() + "), " + t.getName() + ", reason:" + t.getReason());
            interruptedTable.setWidget(i, 0, code);
            interruptedTable.setText(i, 1, t.getName());
            interruptedTable.setText(i, 2, t.getDate());
            interruptedTable.setText(i++, 3, ClientStringFormatter.formatNumber(t.getRegisteredPlayers()));
            intPlayers += t.getRegisteredPlayers();
        }

        tournamentStatus.setText(3, 1, ClientStringFormatter.formatNumber(interrupted.size()));
        interrupedPlayers.setText(ClientStringFormatter.formatNumber(intPlayers));

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

        tournamentStatus.setText(i, 0, "Interrupted (xxx):");
        tournamentStatus.setWidget(i++, 2, interrupedPlayers);
    }

    private void createRight() {
        rightPanel.setStyleName("right");
        rightPanel.add(interruptedTable);
        rightPanel.add(spInterrupted);
    }

    private void createInterruptedTable() {
        int i = 0, j = 0;
        interruptedTable.removeFromParent();
        interruptedTable = new FlexTable();
        interruptedTable.getElement().setId("infoTable");
        interruptedTable.setText(i, j++, "Tr.Code");
        interruptedTable.setText(i, j++, "Name");
        interruptedTable.setText(i, j++, "Time");
        interruptedTable.setText(i, j++, "Players");
        interruptedTable.getRowFormatter().getElement(i).setId("th");
        spInterrupted.add(interruptedTable);
        spInterrupted.setHeight("100%");
        
    }
}
