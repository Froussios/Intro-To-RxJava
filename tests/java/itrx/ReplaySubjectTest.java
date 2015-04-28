package itrx;

import java.util.Arrays;

import org.junit.Test;

import rx.observers.TestSubscriber;
import rx.subjects.ReplaySubject;

public class ReplaySubjectTest {

	public static void main(String[] args) {
		ReplaySubject<Integer> s = ReplaySubject.create();  
	    s.subscribe(v -> System.out.println("Early:" + v));
	    s.onNext(0);
	    s.onNext(1);
	    s.subscribe(v -> System.out.println("Late: " + v)); 
	    s.onNext(2);
	}
	
	@Test
	public void test() {
		TestSubscriber<Integer> tester = new TestSubscriber<Integer>();
		
		ReplaySubject<Integer> s = ReplaySubject.create();  
	    s.subscribe(tester);
	    s.onNext(0);
	    s.onNext(1);
	    s.subscribe(tester); 
	    s.onNext(2);
	    
	    tester.assertReceivedOnNext(Arrays.asList(0, 1, 0, 1, 2, 2));
	}
	

}
