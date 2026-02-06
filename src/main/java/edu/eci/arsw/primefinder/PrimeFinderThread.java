package edu.eci.arsw.primefinder;

import java.util.LinkedList;
import java.util.List;

public class PrimeFinderThread extends Thread{

	
	int a,b;
	private Object pauseLock;
	private Control control;
	private List<Integer> primes;
	private boolean reportedPause = false;
    
	
	public PrimeFinderThread(int a, int b, Object lock, Control control) {
		super();
        this.primes = new LinkedList<>();
		this.a = a;
		this.b = b;
		this.pauseLock = lock;
		this.control = control;
	}

        @Override
	public void run(){
            for (int i= a;i < b;i++){
                synchronized(pauseLock){
                    while (control.isPaused()){
                        try {
                            if (!reportedPause) {
                                control.incrementPausedThreads();
                                reportedPause = true;
                            }
                            pauseLock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    reportedPause = false;
                }
                if (isPrime(i)){
                    primes.add(i);
                    System.out.println(i);
                }
            }
	}
	
	boolean isPrime(int n) {
	    boolean ans;
            if (n > 2) { 
                ans = n%2 != 0;
                for(int i = 3;ans && i*i <= n; i+=2 ) {
                    ans = n % i != 0;
                }
            } else {
                ans = n == 2;
            }
	    return ans;
	}

	public List<Integer> getPrimes() {
		return primes;
	}
	
}
