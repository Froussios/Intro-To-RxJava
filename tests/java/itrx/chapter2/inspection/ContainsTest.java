package itrx.chapter2.inspection;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;
import rx.Subscription;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

public class ContainsTest {

	public void exampleContains() {
		Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS);

		Subscription subscription = values
		    .contains(4L)
		    .subscribe(
		        v -> System.out.println(v),
		        e -> System.out.println("Error: " + e),
		        () -> System.out.println("Completed")
		    );
		
//		true
//		Completed
	}
	
	@Test
	public void test() {
		TestSubscriber<Boolean> tester = new TestSubscriber<Boolean>();
		TestScheduler scheduler = Schedulers.test();
		
		Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS, scheduler);

		Subscription subscription = values
		    .contains(4L)
		    .subscribe(tester);
		
		scheduler.advanceTimeBy(1000, TimeUnit.MILLISECONDS);
		
		tester.assertReceivedOnNext(Arrays.asList(true));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}

}
