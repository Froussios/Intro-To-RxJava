package itrx.chapter2.reducing;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import rx.Observable;
import rx.Subscription;
import rx.observers.TestSubscriber;

public class DistinctTest {

	@Test
	public void testDistinct() {
		TestSubscriber<Integer> tester = new TestSubscriber<Integer>();
		
		Observable<Integer> values = Observable.create(o -> {
		    o.onNext(1);
		    o.onNext(1);
		    o.onNext(2);
		    o.onNext(3);
		    o.onNext(2);
		    o.onCompleted();
		});

		Subscription subscription = values
		    .distinct()
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(1,2,3));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testDistinctKey() {
		TestSubscriber<String> tester = new TestSubscriber<String>();
		
		Observable<String> values = Observable.create(o -> {
		    o.onNext("First");
		    o.onNext("Second");
		    o.onNext("Third");
		    o.onNext("Fourth");
		    o.onNext("Fifth");
		    o.onCompleted();
		});

		Subscription subscription = values
		    .distinct(v -> v.charAt(0))
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList("First", "Second", "Third"));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testDistinctUntilChanged() {
		TestSubscriber<Integer> tester = new TestSubscriber<Integer>();
		
		Observable<Integer> values = Observable.create(o -> {
		    o.onNext(1);
		    o.onNext(1);
		    o.onNext(2);
		    o.onNext(3);
		    o.onNext(2);
		    o.onCompleted();
		});

		Subscription subscription = values
		    .distinctUntilChanged()
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(1,2,3,2));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testDistinctUntilChangedKey() {
		TestSubscriber<String> tester = new TestSubscriber<String>();
		
		Observable<String> values = Observable.create(o -> {
		    o.onNext("First");
		    o.onNext("Second");
		    o.onNext("Third");
		    o.onNext("Fourth");
		    o.onNext("Fifth");
		    o.onCompleted();
		});

		Subscription subscription = values
		    .distinctUntilChanged(v -> v.charAt(0))
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList("First", "Second", "Third", "Fourth"));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}

}
