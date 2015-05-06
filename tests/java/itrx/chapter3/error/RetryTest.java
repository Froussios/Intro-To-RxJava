package itrx.chapter3.error;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;

public class RetryTest {

	public void exampleRetry() {
		Random random = new Random();
		Observable<Integer> values = Observable.create(o -> {
		    o.onNext(random.nextInt() % 20);
		    o.onNext(random.nextInt() % 20);
		    o.onError(new Exception());
		});

		values
		    .retry(1)
		    .subscribe(v -> System.out.println(v));
		
//		0
//		13
//		9
//		15
//		java.lang.Exception
	}
	
	
	//
	// Test
	//
	
	@Test
	public void testRetry() {
		TestSubscriber<Integer> tester = new TestSubscriber<>();
		Random random = new Random();
		Observable<Integer> values = Observable.create(o -> {
		    o.onNext(random.nextInt() % 20);
		    o.onNext(random.nextInt() % 20);
		    o.onError(new Exception());
		});

		values
		    .retry(1)
		    .subscribe(tester);
		
		assertEquals(tester.getOnNextEvents().size(), 4);
		tester.assertTerminalEvent();
		assertEquals(tester.getOnErrorEvents().size(), 1);
	}

}
