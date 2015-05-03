package itrx.chapter1;

import java.util.Arrays;

import org.junit.Test;

import rx.observers.TestSubscriber;
import rx.subjects.BehaviorSubject;

public class BehaviorSubjectTest {

	@Test
	public void testLate() {
		TestSubscriber<Integer> tester = new TestSubscriber<Integer>();
		
		BehaviorSubject<Integer> s = BehaviorSubject.create();
		s.onNext(0);
		s.onNext(1);
		s.onNext(2);
		s.subscribe(tester); 
		s.onNext(3);
		
		tester.assertReceivedOnNext(Arrays.asList(2,3));
	}
	
	@Test
	public void testCompleted() {
		TestSubscriber<Integer> tester = new TestSubscriber<Integer>();
		
		BehaviorSubject<Integer> s = BehaviorSubject.create();
		s.onNext(0);
		s.onNext(1);
		s.onNext(2);
		s.onCompleted();
		s.subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList());
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testInitialvalue() {
		TestSubscriber<Integer> tester = new TestSubscriber<Integer>();
		
		BehaviorSubject<Integer> s = BehaviorSubject.create(0);
		s.subscribe(tester);
		s.onNext(1);
		
		tester.assertReceivedOnNext(Arrays.asList(0,1));
		tester.assertNoErrors();
	}

}
