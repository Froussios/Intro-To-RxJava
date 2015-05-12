package itrx.chapter1;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;
import rx.subjects.ReplaySubject;

public class ReplaySubjectTest {
	
	public void exampleEarlyLate() {
		ReplaySubject<Integer> s = ReplaySubject.create();  
		s.subscribe(v -> System.out.println("Early:" + v));
		s.onNext(0);
		s.onNext(1);
		s.subscribe(v -> System.out.println("Late: " + v)); 
		s.onNext(2);
		
		// Early:0
		// Early:1
		// Late: 0
		// Late: 1
		// Early:2
		// Late: 2
	}
	
	public void exampleWithSize() {
		ReplaySubject<Integer> s = ReplaySubject.createWithSize(2); 
		s.onNext(0);
		s.onNext(1);
		s.onNext(2);
		s.subscribe(v -> System.out.println("Late: " + v)); 
		s.onNext(3);
		
		// Late: 1
		// Late: 2
		// Late: 3
	}
	
	public void exampleWithTime() throws InterruptedException {
		ReplaySubject<Integer> s = ReplaySubject.createWithTime(150, TimeUnit.MILLISECONDS, Schedulers.immediate());
		s.onNext(0);
		Thread.sleep(100);
		s.onNext(1);
		Thread.sleep(100);
		s.onNext(2);
		s.subscribe(v -> System.out.println("Late: " + v)); 
		s.onNext(3);
		
		// Late: 1
		// Late: 2
		// Late: 3
	}
	
	
	//
	// Test
	//
	
	@Test
	public void testEarlyLate() {
		TestSubscriber<Integer> tester = new TestSubscriber<Integer>();
		
		ReplaySubject<Integer> s = ReplaySubject.create();  
	    s.subscribe(tester);
	    s.onNext(0);
	    s.onNext(1);
	    s.subscribe(tester); 
	    s.onNext(2);
	    
	    tester.assertReceivedOnNext(Arrays.asList(0, 1, 0, 1, 2, 2));
	}
	
	@Test
	public void testWithSize() {
		TestSubscriber<Integer> tester = new TestSubscriber<Integer>();
		
		ReplaySubject<Integer> s = ReplaySubject.createWithSize(2); 
		s.onNext(0);
		s.onNext(1);
		s.onNext(2);
		s.subscribe(tester); 
		s.onNext(3);
		
		tester.assertReceivedOnNext(Arrays.asList(1,2,3));
	}
	
	@Test 
	public void testWithTime() {
		TestSubscriber<Integer> tester = new TestSubscriber<Integer>();
		TestScheduler scheduler = Schedulers.test();
		
		ReplaySubject<Integer> s = ReplaySubject.createWithTime(150, TimeUnit.MILLISECONDS, scheduler);
		s.onNext(0);
		scheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);
		s.onNext(1);
		scheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);
		s.onNext(2);
		s.subscribe(tester); 
		s.onNext(3);
		
		tester.assertReceivedOnNext(Arrays.asList(1,2,3));
	}
	

}
