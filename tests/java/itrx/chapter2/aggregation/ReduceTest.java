package itrx.chapter2.aggregation;

import java.util.Arrays;

import org.junit.Test;

import rx.Observable;
import rx.Subscriber;
import rx.observers.TestSubscriber;

public class ReduceTest {
	
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

	
	public void example() {
		Observable<Integer> values = Observable.range(0,5);

		values
		    .reduce((i1,i2) -> i1+i2)
		    .subscribe(new PrintSubscriber("Sum"));
		values
		    .reduce((i1,i2) -> (i1>i2) ? i2 : i1)
		    .subscribe(new PrintSubscriber("Min"));
		
//		Sum: 10
//		Sum: Completed
//		Min: 0
//		Min: Completed
	}
	
	public void exampleWithAccumulator() {
		Observable<String> values = Observable.just("Rx", "is", "easy");

		values
		    .reduce(0, (acc,next) -> acc + 1)
		    .subscribe(new PrintSubscriber("Count"));
		
//		Count: 3
//		Count: Completed
	}
	
	@Test
	public void test() {
		TestSubscriber<Integer> testerSum = new TestSubscriber<>();
		TestSubscriber<Integer> testerMin = new TestSubscriber<>();
		
		Observable<Integer> values = Observable.range(0,5);

		values
		    .reduce((i1,i2) -> i1+i2)
		    .subscribe(testerSum);
		values
		    .reduce((i1,i2) -> (i1>i2) ? i2 : i1)
		    .subscribe(testerMin);
		
		testerSum.assertReceivedOnNext(Arrays.asList(10));
		testerSum.assertTerminalEvent();
		testerSum.assertNoErrors();
		testerMin.assertReceivedOnNext(Arrays.asList(0));
		testerMin.assertTerminalEvent();
		testerMin.assertNoErrors();
	}
	
	@Test
	public void testWithAccumulator() {
		TestSubscriber<Integer> tester = new TestSubscriber<>();
		
		Observable<String> values = Observable.just("Rx", "is", "easy");

		values
		    .reduce(0, (acc,next) -> acc + 1)
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(3));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
}


