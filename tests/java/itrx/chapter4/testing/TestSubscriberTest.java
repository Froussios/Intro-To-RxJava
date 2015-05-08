package itrx.chapter4.testing;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.TestScheduler;

public class TestSubscriberTest {

	@Test
	public void test() {
	    TestScheduler scheduler = new TestScheduler();
	    TestSubscriber<Long> subscriber = new TestSubscriber<>();
	    List<Long> expected = Arrays.asList(0L, 1L, 2L, 3L, 4L);
	    Observable
	        .interval(1, TimeUnit.SECONDS, scheduler)
	        .take(5)
	        .subscribe(subscriber);
	    assertTrue(subscriber.getOnNextEvents().isEmpty());
	    scheduler.advanceTimeBy(5, TimeUnit.SECONDS);
	    subscriber.assertReceivedOnNext(expected);
	    subscriber.assertTerminalEvent();
	    subscriber.assertNoErrors();
	    subscriber.assertUnsubscribed();
	}
}
