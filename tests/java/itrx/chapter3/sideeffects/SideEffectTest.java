package itrx.chapter3.sideeffects;

import java.util.Arrays;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;

public class SideEffectTest {
	
	private static class Inc {
	    private int count = 0;
	    public void inc() { 
	        count++;
	    }
	    public int getCount() {
	        return count;
	    }
	}
	
	private static class Indexed <T> {
	    public final int index;
	    public final T item;
	    public Indexed(int index, T item) {
	        this.index = index;
	        this.item = item;
	    }
	    
	    @Override
	    public boolean equals(Object obj) {
	    	if (obj instanceof Indexed<?>) {
	    		Indexed<?> other = (Indexed<?>) obj;
	    		return this.index == other.index &&
	    				this.item.equals(other.item);
	    	}
	    	return false;
	    }
	}

	public void exampleBadIndex() {
		Observable<String> values = Observable.just("No", "side", "effects", "please");

		Inc index = new Inc();
		Observable<String> indexed = 
		        values.map(w -> {
		            index.inc();
		            return w;
		        });
		indexed.subscribe(w -> System.out.println(index.getCount() + ": " + w));
		
		// 1: No
		// 2: side
		// 3: effects
		// 4: please
	}
	
	public void exampleBadIndexFail() {
		Observable<String> values = Observable.just("No", "side", "effects", "please");

		Inc index = new Inc();
		Observable<String> indexed = 
		        values.map(w -> {
		            index.inc();
		            return w;
		        });
		indexed.subscribe(w -> System.out.println("1st observer: " + index.getCount() + ": " + w));
		indexed.subscribe(w -> System.out.println("2nd observer: " + index.getCount() + ": " + w));
		
		// 1st observer: 1: No
		// 1st observer: 2: side
		// 1st observer: 3: effects
		// 1st observer: 4: please
		// 2nd observer: 5: No
		// 2nd observer: 6: side
		// 2nd observer: 7: effects
		// 2nd observer: 8: please
	}
	
	public void exampleSafeIndex() {
		Observable<String> values = Observable.just("No", "side", "effects", "please");

		Observable<Indexed<String>> indexed = 
		    values.scan(
		            new Indexed<String>(0, null), 
		            (prev,v) -> new Indexed<String>(prev.index+1, v))
		        .skip(1);
		indexed.subscribe(w -> System.out.println("1st observer: " + w.index + ": " + w.item));
		indexed.subscribe(w -> System.out.println("2nd observer: " + w.index + ": " + w.item));
		
		// 1st observer: 1: No
		// 1st observer: 2: side
		// 1st observer: 3: effects
		// 1st observer: 4: please
		// 2nd observer: 1: No
		// 2nd observer: 2: side
		// 2nd observer: 3: effects
		// 2nd observer: 4: please
	}
	
	
	//
	// Tests
	//
	
	@Test
	public void testBadIndex() {
		TestSubscriber<Integer> tester = new TestSubscriber<>();
		
		Observable<String> values = Observable.just("No", "side", "effects", "please");

		Inc index = new Inc();
		Observable<Integer> indexed = 
		        values
		        	.map(w -> {
			            index.inc();
			            return w;
			        })
			        .map(w -> index.getCount());
		indexed.subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(1,2,3,4));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testBadIndexFail() {
		TestSubscriber<Integer> tester1 = new TestSubscriber<>();
		TestSubscriber<Integer> tester2 = new TestSubscriber<>();
		
		Observable<String> values = Observable.just("No", "side", "effects", "please");

		Inc index = new Inc();
		Observable<Integer> indexed = 
		        values.map(w -> {
		            index.inc();
		            return w;
		        })
		        .map(w -> index.getCount());
		indexed.subscribe(tester1);
		indexed.subscribe(tester2);
		
		tester1.assertReceivedOnNext(Arrays.asList(1,2,3,4));
		tester1.assertTerminalEvent();
		tester1.assertNoErrors();
		tester2.assertReceivedOnNext(Arrays.asList(5,6,7,8));
		tester2.assertTerminalEvent();
		tester2.assertNoErrors();
	}
	
	@Test
	public void testSafeIndex() {
		TestSubscriber<Integer> tester1 = new TestSubscriber<>();
		TestSubscriber<Integer> tester2 = new TestSubscriber<>();
		
		Observable<String> values = Observable.just("No", "side", "effects", "please");

		Observable<Integer> indexed = 
		    values.scan(
		            new Indexed<String>(0, null), 
		            (prev,v) -> new Indexed<String>(prev.index+1, v))
		        .skip(1)
		        .map(i -> i.index);
		indexed.subscribe(tester1);
		indexed.subscribe(tester2);
		
		tester1.assertReceivedOnNext(Arrays.asList(1,2,3,4));
		tester1.assertTerminalEvent();
		tester1.assertNoErrors();
		tester2.assertReceivedOnNext(Arrays.asList(1,2,3,4));
		tester2.assertTerminalEvent();
		tester2.assertNoErrors();
	}

}
