package itrx.chapter2.aggregation;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import rx.Observable;
import rx.Subscriber;
import rx.observers.TestSubscriber;

public class CountTest {
	
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
		Observable<Integer> values = Observable.range(0, 3);

		values
		    .subscribe(new PrintSubscriber("Values"));
		values
		    .count()
		    .subscribe(new PrintSubscriber("Count"));
		
//		Values: 0
//		Values: 1
//		Values: 2
//		Values: Completed
//		Count: 3
//		Count: Completed
	}
	
	public void exampleCountLong() {
		Observable<Integer> values = Observable.range(0, 3);

		values
		    .subscribe(new PrintSubscriber("Values"));
		values
		    .countLong()
		    .subscribe(new PrintSubscriber("Count"));
		
//		Values: 0
//		Values: 1
//		Values: 2
//		Values: Completed
//		Count: 3
//		Count: Completed
	}
	
	@Test
	public void test() {
		TestSubscriber<Integer> testerSource = new TestSubscriber<>();
		TestSubscriber<Integer> testerCount = new TestSubscriber<>();
		
		Observable<Integer> values = Observable.range(0, 3);

		values
		    .subscribe(testerSource);
		values
		    .count()
		    .subscribe(testerCount);
		
		testerSource.assertReceivedOnNext(Arrays.asList(0,1,2));
		testerSource.assertTerminalEvent();
		testerSource.assertNoErrors();
		
		testerCount.assertReceivedOnNext(Arrays.asList(3));
		testerCount.assertTerminalEvent();
		testerCount.assertNoErrors();
	}
	
	@Test
	public void testCountLong() {
		TestSubscriber<Integer> testerSource = new TestSubscriber<>();
		TestSubscriber<Long> testerCount = new TestSubscriber<>();
		
		Observable<Integer> values = Observable.range(0, 3);

		values
		    .subscribe(testerSource);
		values
		    .countLong()
		    .subscribe(testerCount);
		
		testerSource.assertReceivedOnNext(Arrays.asList(0,1,2));
		testerSource.assertTerminalEvent();
		testerSource.assertNoErrors();
		
		testerCount.assertReceivedOnNext(Arrays.asList(3L));
		testerCount.assertTerminalEvent();
		testerCount.assertNoErrors();
	}
	
	

}
