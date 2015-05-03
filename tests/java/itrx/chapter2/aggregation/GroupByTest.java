package itrx.chapter2.aggregation;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.Test;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;
import rx.subjects.ReplaySubject;
import rx.subjects.Subject;

public class GroupByTest {
	
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

	public void exampleGroupBy() {
		Observable<String> values = Observable.just(
		        "first",
		        "second",
		        "third",
		        "forth",
		        "fifth",
		        "sixth"
		);

		values.groupBy(word -> word.charAt(0))
		    .flatMap(group -> 
		        group.last().map(v -> group.getKey() + ": " + v)
		    )
		    .subscribe(v -> System.out.println(v));
		
//		s: sixth
//		t: third
//		f: fifth
	}
	
	
	//
	// Tests
	//
	
	@Test
	public void testGroupBy() {
		TestSubscriber<Object> tester = new TestSubscriber<>();
		
		Observable<String> values = Observable.just(
		        "first",
		        "second",
		        "third",
		        "forth",
		        "fifth",
		        "sixth"
		);

		values.groupBy(word -> word.charAt(0))
		    .flatMap(group -> 
		        group.last().map(v -> group.getKey() + ": " + v)
		    )
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList("s: sixth", "t: third", "f: fifth"));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
}


