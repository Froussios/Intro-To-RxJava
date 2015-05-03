package itrx.chapter2.aggregation;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;
import rx.subjects.ReplaySubject;
import rx.subjects.Subject;

public class ScanTest {
	
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

	
	public void exampleRunningSum() {
		Observable<Integer> values = Observable.range(0,5);

		values
		    .scan((i1,i2) -> i1+i2)
		    .subscribe(new PrintSubscriber("Sum"));
		
//		Sum: 0
//		Sum: 1
//		Sum: 3
//		Sum: 6
//		Sum: 10
//		Sum: Completed
	}
	
	public void exampleRunningMin() {
		Subject<Integer, Integer> values = ReplaySubject.create();

		values
		    .subscribe(new PrintSubscriber("Values"));
		values
		    .scan((i1,i2) -> (i1<i2) ? i1 : i2)
		    .distinctUntilChanged()
		    .subscribe(new PrintSubscriber("Min"));

		values.onNext(2);
		values.onNext(3);
		values.onNext(1);
		values.onNext(4);
		values.onCompleted();
		
//		Values: 2
//		Min: 2
//		Values: 3
//		Values: 1
//		Min: 1
//		Values: 4
//		Values: Completed
//		Min: Completed
	}
	
	@Test
	public void testRunningSum() {
		TestSubscriber<Integer> tester = new TestSubscriber<>();
		
		Observable<Integer> values = Observable.range(0,5);

		values
		    .scan((i1,i2) -> i1+i2)
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(0,1,3,6,10));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testRunningMin() {
		TestSubscriber<Integer> testerSource = new TestSubscriber<>();
		TestSubscriber<Integer> testerScan = new TestSubscriber<>();
		
		Subject<Integer, Integer> values = ReplaySubject.create();

		values
		    .subscribe(testerSource);
		values
		    .scan((i1,i2) -> (i1<i2) ? i1 : i2)
		    .distinctUntilChanged()
		    .subscribe(testerScan);

		values.onNext(2);
		values.onNext(3);
		values.onNext(1);
		values.onNext(4);
		values.onCompleted();
		
		testerSource.assertReceivedOnNext(Arrays.asList(2,3,1,4));
		testerSource.assertTerminalEvent();
		testerSource.assertNoErrors();
		testerScan.assertReceivedOnNext(Arrays.asList(2,1));
		testerScan.assertTerminalEvent();
		testerScan.assertNoErrors();
	}
}


