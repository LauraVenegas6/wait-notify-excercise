/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.primefinder;

import java.io.IOException;

/**
 *
 */
public class Control extends Thread {
    
    private final static int NTHREADS = 3;
    private final static int MAXVALUE = 30000000;
    private final static int TMILISECONDS = 5000;
    private final Object lock = new Object();
    private volatile boolean paused = false;
    private int pausedThreads;

    private final int NDATA = MAXVALUE / NTHREADS;

    private PrimeFinderThread pft[];
    
    private Control() {
        super();
        this.pft = new  PrimeFinderThread[NTHREADS];
        int i;
        for(i = 0;i < NTHREADS - 1; i++) {
            PrimeFinderThread elem = new PrimeFinderThread(i*NDATA, (i+1)*NDATA, lock, this);
            pft[i] = elem;
        }
        pft[i] = new PrimeFinderThread(i*NDATA, MAXVALUE + 1, lock, this);
    }
    
    public static Control newControl() {
        return new Control();
    }

    public Object getPauseLock() {
        return lock;
    }

    public boolean isPaused(){
        return paused;
    }

    public void incrementPausedThreads() {
        synchronized(lock) {
            pausedThreads++;
            lock.notifyAll();
        }
    }

    public void restartPausedThreads() {
        synchronized(lock) {
            pausedThreads = 0;
        }
    }

    @Override
    public void run() {
        
        for(int i = 0; i < NTHREADS; i++) {
            pft[i].start();
        }
        
        try {
            while(true) {
                Thread.sleep(TMILISECONDS);
                synchronized(lock) {
                    paused = true;
                    pausedThreads = 0;
                }
    
                synchronized(lock) {
                    while (pausedThreads < NTHREADS) {
                        lock.wait();
                    }
                }

                int totalPrimes = 0;
                for(int i = 0; i < NTHREADS; i++) {
                    totalPrimes += pft[i].getPrimes().size();
                }
                System.out.println("\nPAUSA");
                System.out.println("Primos encontrados hasta ahora: " + totalPrimes);
                System.out.println("Presione ENTER para reanudar");
    
                System.in.read();
                synchronized(lock) {
                    pausedThreads = 0;
                    paused = false;
                    lock.notifyAll();
                }
                System.out.println("REANUDANDO\n");
            }
        } catch(IOException e) {
            e.printStackTrace();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }
    
}
