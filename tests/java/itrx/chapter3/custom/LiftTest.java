package itrx.chapter3.custom;

import java.util.Arrays;

import org.junit.Test;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.observers.TestSubscriber;

public class LiftTest {

	public static class MyMap<T,R> implements Observable.Operator<R, T> {
	    private Func1<T,R> transformer;

	    public MyMap(Func1<T,R> transformer) {
	        this.transformer = transformer;
	    }

	    @Override
	    public Subscriber<? super T> call(Subscriber<? super R> subscriber) {
	        return new Subscriber<T>() {

	            @Override
	            public void onCompleted() {
	                if (!subscriber.isUnsubscribed())
	                    subscriber.onCompleted();
	            }

	            @Override
	            public void onError(Throwable e) {
	                if (!subscriber.isUnsubscribed())
	                    subscriber.onError(e);
	            }

	            @Override
	            public void onNext(T t) {
	                if (!subscriber.isUnsubscribed())
	                    subscriber.onNext(transformer.call(t));
	            }

	        };
	    }
	    
	    public static <T,R> MyMap<T,R> create(Func1<T,R> transformer) {
	    	return new MyMap<T, R>(transformer);
	    }
	}
	
	public void exampleLift() {
		Observable.range(0, 5)
		    .lift(MyMap.create(i -> i + "!"))
		    .subscribe(System.out::println);
		
//		0!
//		1!
//		2!
//		3!
//		4!
	}
	
	
	//
	// Tests
	//
	
	@Test
	public void testLift() {
		TestSubscriber<String> tester = new TestSubscriber<>();
		
		Observable.range(0, 5)
		    .lift(MyMap.create(i -> i + "!"))
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList("0!", "1!", "2!", "3!", "4!"));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
}
