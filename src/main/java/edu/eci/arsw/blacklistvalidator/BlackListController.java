/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.eci.arsw.blacklistvalidator;

/**
 *
 * @author jaime.cacuna
 */
public class BlackListController {

    private int ocurrencesCount;
    private static final int BLACK_LIST_ALARM_COUNT = 5;

    public BlackListController() {
        this.ocurrencesCount = 0;
    }

    public synchronized boolean canIncrementOcurrencesCount() {
        boolean valid = false;
        if (ocurrencesCount < BLACK_LIST_ALARM_COUNT) {
            valid = true;
            ocurrencesCount++;
        }
        return valid;
    }

    public synchronized boolean validar() {
        boolean valid = false;
        if (ocurrencesCount == BLACK_LIST_ALARM_COUNT) {
            valid = true;
        }
        return valid;
    }

    public int getOcurrencesCount() {
        return ocurrencesCount;
    }

}