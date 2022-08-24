/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blacklistvalidator;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hcadavid
 */
public class HostBlackListsValidator {

    private static final int BLACK_LIST_ALARM_COUNT = 5;
    HostBlacklistsDataSourceFacade skds = HostBlacklistsDataSourceFacade.getInstance();
    LinkedList<MyThread> blackListThreadValidator = new LinkedList<>();
    private BlackListController lock;
    /**
     * Check the given host's IP address in all the available black lists, and
     * report it as NOT Trustworthy when such IP was reported in at least
     * BLACK_LIST_ALARM_COUNT lists, or as Trustworthy in any other case. The
     * search is not exhaustive: When the number of occurrences is equal to
     * BLACK_LIST_ALARM_COUNT, the search is finished, the host reported as NOT
     * Trustworthy, and the list of the five blacklists returned.
     *
     * @param ipaddress suspicious host's IP address.
     * @return Blacklists numbers where the given host's IP address was found.
     */
    public List<Integer> checkHost(String ipaddress) {

        LinkedList<Integer> blackListOcurrences = new LinkedList<>();

        int ocurrencesCount = 0;

        HostBlacklistsDataSourceFacade skds = HostBlacklistsDataSourceFacade.getInstance();

        int checkedListsCount = 0;

        for (int i = 0; i < skds.getRegisteredServersCount() && ocurrencesCount < BLACK_LIST_ALARM_COUNT; i++) {
            checkedListsCount++;

            if (skds.isInBlackListServer(i, ipaddress)) {

                blackListOcurrences.add(i);

                ocurrencesCount++;
            }
        }

        if (ocurrencesCount >= BLACK_LIST_ALARM_COUNT) {
            skds.reportAsNotTrustworthy(ipaddress);
        } else {
            skds.reportAsTrustworthy(ipaddress);
        }

        LOG.log(Level.INFO, "Checked Black Lists:{0} of {1}", new Object[]{checkedListsCount, skds.getRegisteredServersCount()});

        return blackListOcurrences;
    }

    /**
     * Check the given host's IP address in all the available black lists, and
     * report it as NOT Trustworthy when such IP was reported in at least
     * BLACK_LIST_ALARM_COUNT lists, or as Trustworthy in any other case. The
     * search is not exhaustive: When the number of occurrences is equal to
     * BLACK_LIST_ALARM_COUNT, the search is finished, the host reported as NOT
     * Trustworthy, and the list of the five blacklists returned.
     *
     * @param ipaddress suspicious host's IP address.
     * @return Blacklists numbers where the given host's IP address was found.
     */
    public List<Integer> checkHost(String ipaddress, Integer threadNumber) {

        int ocurrencesCount = 0;
        int checkedListsCount = 0;
        LinkedList<Integer> blackListOcurrences = new LinkedList<>();

        createThreads(ipaddress, threadNumber);

        for (MyThread thread : blackListThreadValidator) {
            thread.start();
        }

        for (MyThread thread : blackListThreadValidator) {
            try {
                thread.join();
                ocurrencesCount += thread.getOcurrencesCount();
                checkedListsCount += thread.getCheckedListCount();
                blackListOcurrences.addAll(thread.getServers());

            } catch (Exception e) {
                System.out.println("Error");
            }

        }

        if (ocurrencesCount >= BLACK_LIST_ALARM_COUNT) {
            skds.reportAsNotTrustworthy(ipaddress);
        } else {
            skds.reportAsTrustworthy(ipaddress);
        }

        LOG.log(Level.INFO, "Checked Black Lists:{0} of {1}", new Object[]{checkedListsCount, skds.getRegisteredServersCount()});

        return blackListOcurrences;
    }

    private static final Logger LOG = Logger.getLogger(HostBlackListsValidator.class.getName());

    private void createThreads(String ipaddress, int threadNumber) {

        int numThreadforServer = skds.getRegisteredServersCount() / threadNumber;
        int numServers = skds.getRegisteredServersCount();
        lock = new BlackListController();

        for (int i = 0; i < skds.getRegisteredServersCount(); i += (numThreadforServer)) {
            MyThread hilo;
            if ((i + (numThreadforServer * 2)) > numServers) {
                hilo = new MyThread(i + 1, numServers, skds, ipaddress, lock);
                blackListThreadValidator.add(hilo);
                break;

            } else if (i == 0) {
                hilo = new MyThread(i, i + numThreadforServer, skds, ipaddress, lock);
                blackListThreadValidator.add(hilo);

            } else {
                hilo = new MyThread(i + 1, i + numThreadforServer, skds, ipaddress, lock);
                blackListThreadValidator.add(hilo);
            }

        }
    }
}