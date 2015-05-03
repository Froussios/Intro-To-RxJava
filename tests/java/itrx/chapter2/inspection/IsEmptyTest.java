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

public class IsEmptyTest {

	public void exampleIsEmpty() {
		Observable<Long> values = Observable.timer(1000, TimeUnit.MILLISECONDS);

		Subscription subscription = values
		    .isEmpty()
		    .subscribe(
		        v -> System.out.println(v),
		        e -> System.out.println("Error: " + e),
		        () -> System.out.println("Completed")
		    );
		
//		false
//		Completed
	}
	
	@Test
	public void testIsEmpty() {
		TestSubscriber<Boolean> tester = new TestSubscriber<Boolean>();
		TestScheduler scheduler = Schedulers.test();
		
		Observable<Long> values = Observable.timer(1000, TimeUnit.MILLISECONDS, scheduler);

		Subscription subscription = values
		    .isEmpty()
		    .subscribe(tester);
		
		scheduler.advanceTimeBy(1000, TimeUnit.MILLISECONDS);

		tester.assertReceivedOnNext(Arrays.asList(false));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}

}
