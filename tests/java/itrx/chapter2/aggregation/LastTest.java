package itrx.chapter2.aggregation;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;
import rx.Subscriber;
import rx.observers.TestSubscriber;

public class LastTest {
	
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

	public void exampleLast() {
		Observable<Integer> values = Observable.range(0,10);

		values
		    .last()
		    .subscribe(new PrintSubscriber("Last"));
		
		// 9
	}
	
	public void exampleLastWithPredicate() {
		Observable<Integer> values = Observable.range(0,10);

		values
		    .last(v -> v<5)
		    .subscribe(new PrintSubscriber("Last"));
		
		// 4
	}
	
	public void exampleLastOrDefault() {
		Observable<Integer> values = Observable.empty();

		values
		    .lastOrDefault(-1)
		    .subscribe(new PrintSubscriber("Last"));
		
		// -1
	}
	
	public void exampleLastOrDefaultWithPredicate() {
		Observable<Integer> values = Observable.empty();

		values
		    .lastOrDefault(-1, v -> v>5)
		    .subscribe(new PrintSubscriber("Last"));
		
		// -1
	}

	@Test
	public void testLast() {
		TestSubscriber<Integer> tester = new TestSubscriber<>();
		
		Observable<Integer> values = Observable.range(0,10);

		values
		    .last()
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(9));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testLastWithPredicate() {
		TestSubscriber<Integer> tester = new TestSubscriber<>();
		
		Observable<Integer> values = Observable.range(0,10);

		values
		    .last(v -> v<5)
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(4));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testLastOrDefault() {
		TestSubscriber<Integer> tester = new TestSubscriber<>();
		
		Observable<Integer> values = Observable.empty();

		values
		    .lastOrDefault(-1)
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(-1));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testLastOrDefaultWithPredicate() {
		TestSubscriber<Integer> tester = new TestSubscriber<>();
		
		Observable<Integer> values = Observable.empty();

		values
		    .lastOrDefault(-1, v -> v<5)
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(-1));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
}


