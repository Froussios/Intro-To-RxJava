package itrx.chapter1;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import rx.Subscription;
import rx.observers.TestSubscriber;
import rx.subjects.ReplaySubject;
import rx.subjects.Subject;
import rx.subscriptions.Subscriptions;

public class UnsubscribingTests {

	public void exampleUnsubscribe() {
		Subject<Integer, Integer> values = ReplaySubject.create();
		Subscription subscription = values.subscribe(
				v -> System.out.println(v), e -> System.err.println(e),
				() -> System.out.println("Done"));
		values.onNext(0);
		values.onNext(1);
		subscription.unsubscribe();
		values.onNext(2);

		// 0
		// 1
	}

	public void exampleIndependentSubscriptions() {
		Subject<Integer, Integer> values = ReplaySubject.create();
		Subscription subscription1 = values.subscribe(v -> System.out
				.println("First: " + v));
		values.subscribe(v -> System.out.println("Second: " + v));
		values.onNext(0);
		values.onNext(1);
		subscription1.unsubscribe();
		System.out.println("Unsubscribed first");
		values.onNext(2);

		// First: 0
		// Second: 0
		// First: 1
		// Second: 1
		// Unsubscribed first
		// Second: 2
	}

	public void exampleUnsubscribeAction() {
		Subscription s = Subscriptions
				.create(() -> System.out.println("Clean"));
		s.unsubscribe();

		// Clean
	}

	
	//
	// Tests
	//

	@Test
	public void testUnsubscribe() {
		TestSubscriber<Integer> tester = new TestSubscriber<Integer>();

		Subject<Integer, Integer> values = ReplaySubject.create();
		Subscription subscription = values.subscribe(tester);
		values.onNext(0);
		values.onNext(1);
		subscription.unsubscribe();
		values.onNext(2);

		tester.assertReceivedOnNext(Arrays.asList(0, 1));
		tester.assertUnsubscribed();
	}

	@Test
	public void testIndependentSubscriptions() {
		TestSubscriber<Integer> tester1 = new TestSubscriber<Integer>();
		TestSubscriber<Integer> tester2 = new TestSubscriber<Integer>();

		Subject<Integer, Integer> values = ReplaySubject.create();
		Subscription subscription1 = values.subscribe(tester1);
		Subscription subscription2 = values.subscribe(tester2);
		values.onNext(0);
		values.onNext(1);
		subscription1.unsubscribe();
		values.onNext(2);

		tester1.assertReceivedOnNext(Arrays.asList(0, 1));
		tester2.assertReceivedOnNext(Arrays.asList(0, 1, 2));
		tester1.assertUnsubscribed();
		assertFalse(tester2.isUnsubscribed());

		subscription2.unsubscribe();
	}

	@Test
	public void testUnsubscribeAction() {
		boolean[] ran = { false };

		Subscription s = Subscriptions.create(() -> ran[0] = true);
		s.unsubscribe();

		assertTrue(ran[0]);
	}

}
