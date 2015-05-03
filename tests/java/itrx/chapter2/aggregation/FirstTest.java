package itrx.chapter2.aggregation;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;
import rx.Subscriber;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

public class FirstTest {
	
	private class PrintSubscriber extends Subscriber<Object>{
	    private final String name;
	    public PrintSubscriber(String name) {
	        this.name = name;
	    }
	    @Override
	    public void onCompleted() {
	        System.out.println(name + ": Completed");
	    }
	    @Override
	    public void onError(Throwable e) {
	        System.out.println(name + ": Error: " + e);
	    }
	    @Override
	    public void onNext(Object v) {
	        System.out.println(name + ": " + v);
	    }
	}

	public void exampleFirst() {
		Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS);

		values
		    .first()
		    .subscribe(new PrintSubscriber("First"));
		
		// 0
	}
	
	public void exampleFirstWithPredicate() {
		Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS);

		values
		    .first(v -> v>5)
		    .subscribe(new PrintSubscriber("First"));
		
		// 6
	}
	
	public void exampleFirstOrDefault() {
		Observable<Long> values = Observable.empty();

		values
		    .firstOrDefault(-1L)
		    .subscribe(new PrintSubscriber("First"));
		
		// -1
	}
	
	public void exampleFirstOrDefaultWithPredicate() {
		Observable<Long> values = Observable.empty();

		values
		    .firstOrDefault(-1L, v -> v>5)
		    .subscribe(new PrintSubscriber("First"));
		
		// -1
	}

	@Test
	public void testFirst() {
		TestSubscriber<Long> tester = new TestSubscriber<>();
		TestScheduler scheduler = Schedulers.test();
		
		Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS, scheduler);

		values
		    .first()
		    .subscribe(tester);
		
		scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
		
		tester.assertReceivedOnNext(Arrays.asList(0L));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testFirstWithPredicate() {
		TestSubscriber<Long> tester = new TestSubscriber<>();
		TestScheduler scheduler = Schedulers.test();
		
		Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS, scheduler);

		values
		    .first(v -> v>5)
		    .subscribe(tester);
		
		scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
		
		tester.assertReceivedOnNext(Arrays.asList(6L));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testFirstOrDefault() {
		TestSubscriber<Long> tester = new TestSubscriber<>();
		
		Observable<Long> values = Observable.empty();

		values
		    .firstOrDefault(-1L)
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(-1L));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testFirstOrDefaultWithPredicate() {
		TestSubscriber<Long> tester = new TestSubscriber<>();
		
		Observable<Long> values = Observable.empty();

		values
		    .firstOrDefault(-1L, v -> v>5)
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(-1L));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
}


