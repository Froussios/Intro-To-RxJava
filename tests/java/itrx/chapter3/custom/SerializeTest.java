package itrx.chapter3.custom;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import rx.Observable;
import rx.Subscriber;
import rx.observers.TestSubscriber;

public class SerializeTest {

	public void exampleSafeSubscribe() {
		Observable<Integer> source = Observable.create(o -> {
		    o.onNext(1);
		    o.onNext(2);
		    o.onCompleted();
		    o.onNext(3);
		    o.onCompleted();
		});

		source.doOnUnsubscribe(() -> System.out.println("Unsubscribed"))
			.subscribe(
		        System.out::println,
		        System.out::println,
		        () -> System.out.println("Completed"));
		
		// 1
		// 2
		// Completed
		// Unsubscribed
	}
	
	public void exampleUnsafeSubscribe() {
		Observable<Integer> source = Observable.create(o -> {
		    o.onNext(1);
		    o.onNext(2);
		    o.onCompleted();
		    o.onNext(3);
		    o.onCompleted();
		});

		source.doOnUnsubscribe(() -> System.out.println("Unsubscribed"))
		    .unsafeSubscribe(new Subscriber<Integer>() {
		        @Override
		        public void onCompleted() {
		            System.out.println("Completed");
		        }

		        @Override
		        public void onError(Throwable e) {
		            System.out.println(e);
		        }

		        @Override
		        public void onNext(Integer t) {
		            System.out.println(t);
		        }
		});
		
		// 1
		// 2
		// Completed
		// 3
		// Completed
	}
	
	public void exampleSerialize() {
		Observable<Integer> source = Observable.create(o -> {
		        o.onNext(1);
		        o.onNext(2);
		        o.onCompleted();
		        o.onNext(3);
		        o.onCompleted();
		    })
		    .cast(Integer.class)
		    .serialize();;


		source.doOnUnsubscribe(() -> System.out.println("Unsubscribed"))
		    .unsafeSubscribe(new Subscriber<Integer>() {
		        @Override
		        public void onCompleted() {
		            System.out.println("Completed");
		        }
	
		        @Override
		        public void onError(Throwable e) {
		            System.out.println(e);
		        }
	
		        @Override
		        public void onNext(Integer t) {
		            System.out.println(t);
		        }
		});
		
//		1
//		2
//		Completed
	}
	
	
	//
	// Tests
	//
	
	@Test
	public void testSafeSubscribe() {
		TestSubscriber<Integer> tester = new TestSubscriber<>();
		
		Observable<Integer> source = Observable.create(o -> {
		    o.onNext(1);
		    o.onNext(2);
		    o.onCompleted();
		    o.onNext(3);
		    o.onCompleted();
		});

		source.subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(1, 2));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
		tester.assertUnsubscribed();
	}
	
	@Test
	public void testUnsafeSubscribe() {
		TestSubscriber<Integer> tester = new TestSubscriber<>();
		
		Observable<Integer> source = Observable.create(o -> {
		    o.onNext(1);
		    o.onNext(2);
		    o.onCompleted();
		    o.onNext(3);
		    o.onCompleted();
		});

		source.doOnUnsubscribe(() -> System.out.println("Unsubscribed"))
		    .unsafeSubscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(1, 2, 3));
		assertEquals(2, tester.getOnCompletedEvents().size());
		tester.assertNoErrors();
		assertFalse(tester.isUnsubscribed());
	}
	
	@Test
	public void testSerialize() {
		TestSubscriber<Integer> tester = new TestSubscriber<>();
		
		Observable<Integer> source = Observable.create(o -> {
		        o.onNext(1);
		        o.onNext(2);
		        o.onCompleted();
		        o.onNext(3);
		        o.onCompleted();
		    })
		    .cast(Integer.class)
		    .serialize();;


		source.doOnUnsubscribe(() -> System.out.println("Unsubscribed"))
		    .unsafeSubscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(1, 2));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
		assertFalse(tester.isUnsubscribed());
	}

}
