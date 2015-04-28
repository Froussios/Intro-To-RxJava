package itrx;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;
import rx.subjects.ReplaySubject;

public class ReplaySubjectTest {
	
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
	public void testWithTime() throws InterruptedException {
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
