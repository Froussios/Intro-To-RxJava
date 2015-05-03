package itrx.chapter2.transforming;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import rx.Observable;
import rx.Subscriber;
import rx.observers.TestSubscriber;

public class CastTypeOfTest {
	
	private static class PrintSubscriber extends Subscriber<Object>{
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

	
	public void exampleCast() {
		Observable<Object> values = Observable.just(0, 1, 2, 3);

		values
		    .cast(Integer.class)
		    .subscribe(new PrintSubscriber("Map"));
		
//		Map: 0
//		Map: 1
//		Map: 2
//		Map: 3
//		Map: Completed
	}
	
	public void exampleCastFail() {
		Observable<Object> values = Observable.just(0, 1, 2, "3");

		values
		    .cast(Integer.class)
		    .subscribe(new PrintSubscriber("Map"));
		
//		Map: 0
//		Map: 1
//		Map: 2
//		Map: Error: java.lang.ClassCastException: Cannot cast java.lang.String to java.lang.Integer
	}
	
	public void exampleTypeOf() {
		Observable<Object> values = Observable.just(0, 1, "2", 3);

		values
		    .ofType(Integer.class)
		    .subscribe(new PrintSubscriber("Map"));
		
//		Map: 0
//		Map: 1
//		Map: 3
//		Map: Completed
	}
	
	
	//
	// Tests
	//
	
	@Test
	public void testCast() {
		TestSubscriber<Integer> tester = new TestSubscriber<>();
		
		Observable<Object> values = Observable.just(0, 1, 2, 3);

		values
		    .cast(Integer.class)
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(0,1,2,3));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testCastFail() {
		TestSubscriber<Integer> tester = new TestSubscriber<>();
		
		Observable<Object> values = Observable.just(0, 1, 2, "3");

		values
		    .cast(Integer.class)
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(0,1,2));
		tester.assertTerminalEvent();
		assertEquals(tester.getOnErrorEvents().size(), 1); // received 1 error
	}
	
	@Test
	public void testTypeOf() {
		TestSubscriber<Integer> tester = new TestSubscriber<>();
		
		Observable<Object> values = Observable.just(0, 1, "2", 3);

		values
		    .ofType(Integer.class)
		    .subscribe(tester);

		tester.assertReceivedOnNext(Arrays.asList(0,1,3));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
}
