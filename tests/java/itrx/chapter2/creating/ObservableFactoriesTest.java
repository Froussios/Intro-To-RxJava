package itrx.chapter2.creating;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

public class ObservableFactoriesTest {

	@Test
	public void testJust() {
		TestSubscriber<String> tester = new TestSubscriber<String>();
		
		Observable<String> values = Observable.just("one", "two", "three");
		values.subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList("one", "two", "three"));
		tester.assertNoErrors();
		tester.assertTerminalEvent();
	}
	
	@Test
	public void testEmpty() {
		TestSubscriber<String> tester = new TestSubscriber<String>();
		
		Observable<String> values = Observable.empty();
		values.subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList());
		tester.assertNoErrors();
		tester.assertTerminalEvent();
	}
	
	@Test
	public void testNever() {
		TestSubscriber<String> tester = new TestSubscriber<String>();
		
		Observable<String> values = Observable.never();
		values.subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList());
		tester.assertNoErrors();
		assertEquals(tester.getOnCompletedEvents().size(), 0);
	}
	
	@Test
	public void testError() {
		TestSubscriber<String> tester = new TestSubscriber<String>();
		
		Observable<String> values = Observable.error(new Exception("Oops"));
		values.subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList());
		tester.assertTerminalEvent();
		assertEquals(tester.getOnErrorEvents().size(), 1);
		assertEquals(tester.getOnCompletedEvents().size(), 0);
	}
	
	@Test
	public void testDefer() {
		TestScheduler scheduler = Schedulers.test();
		TestSubscriber<Long> tester1 = new TestSubscriber<>();
		TestSubscriber<Long> tester2 = new TestSubscriber<>();
		
		Observable<Long> now = Observable.defer(() ->
			Observable.just(scheduler.now()));

		now.subscribe(tester1);
		scheduler.advanceTimeBy(1000, TimeUnit.MILLISECONDS);
		now.subscribe(tester2);
		
		assertTrue(tester1.getOnNextEvents().get(0) <
					tester2.getOnNextEvents().get(0));
	}
	
	@Test
	public void testCreate() {
		TestSubscriber<String> tester = new TestSubscriber<String>();
		
		Observable<String> values = Observable.create(o -> {
		    o.onNext("Hello");
		    o.onCompleted();
		});
		values.subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList("Hello"));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
}
