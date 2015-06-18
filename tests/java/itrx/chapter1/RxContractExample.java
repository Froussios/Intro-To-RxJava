package itrx.chapter1;

import java.util.Arrays;

import org.junit.Test;

import rx.observers.TestSubscriber;
import rx.subjects.ReplaySubject;
import rx.subjects.Subject;

public class RxContractExample {

	public void example() {
		Subject<Integer, Integer> s = ReplaySubject.create();
		s.subscribe(v -> System.out.println(v));
		s.onNext(0);
		s.onCompleted();
		s.onNext(1);
		s.onNext(2);

		// 0
	}
	
	public void examplePrintCompletion() {
		Subject<Integer, Integer>  values = ReplaySubject.create();
		values.subscribe(
		    v -> System.out.println(v),
		    e -> System.out.println(e),
		    () -> System.out.println("Completed")
		);
		values.onNext(0);
		values.onNext(1);
		values.onCompleted();
		values.onNext(2);
		
		// 0
		// 1
	}

	
	//
	// Test
	//

	@Test
	public void test() {
		TestSubscriber<Integer> tester = new TestSubscriber<Integer>();

		Subject<Integer, Integer> s = ReplaySubject.create();
		s.subscribe(tester);
		s.onNext(0);
		s.onCompleted();
		s.onNext(1);
		s.onNext(2);

		tester.assertReceivedOnNext(Arrays.asList(0));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testPrintCompletion() {
		TestSubscriber<Integer> tester = new TestSubscriber<Integer>();
		
		Subject<Integer, Integer>  values = ReplaySubject.create();
		values.subscribe(tester);
		values.onNext(0);
		values.onNext(1);
		values.onCompleted();
		values.onNext(2);
		
		tester.assertReceivedOnNext(Arrays.asList(0,1));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}

}
