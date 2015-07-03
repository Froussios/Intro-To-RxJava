/*******************************************************************************
 * Copyright (c) 2015 Christos Froussios
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 *******************************************************************************/
package itrx.chapter3.sideeffects;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.subjects.BehaviorSubject;
import rx.subjects.Subject;

public class AsObservableExample {

	public static class BrakeableService {
	    public BehaviorSubject<String> items = BehaviorSubject.create("Greet");
	    
	    public void play() {
	        items.onNext("Hello");
	        items.onNext("and");
	        items.onNext("goodbye");
	    }
	}
	
	public static class BrakeableService2 {
	    private final BehaviorSubject<String> items = BehaviorSubject.create("Greet");

	    public BehaviorSubject<String> getValuesUnsafe() {
	        return items;
	    }
	    
	    public Observable<String> getValuesUnsafe2() {
	        return items;
	    }
	    
	    public void play() {
	        items.onNext("Hello");
	        items.onNext("and");
	        items.onNext("goodbye");
	    }
	}
	
	public static class SafeService {
		private final BehaviorSubject<String> items = BehaviorSubject.create("Greet");
		 
		public Observable<String> getValues() {
	        return items.asObservable();
	    }
		
		public void play() {
	        items.onNext("Hello");
	        items.onNext("and");
	        items.onNext("goodbye");
	    }
	}
	
	public void exampleModifyReference() {
		BrakeableService service = new BrakeableService();
		service.items.subscribe((i) -> System.out.println("Before: " + i));
		service.items = BehaviorSubject.create("Later");
		service.items.subscribe((i) -> System.out.println("After: " + i));
		service.play();
		
		// Before: Greet
		// After: Greet
		// After: Hello
		// After: and
		// After: goodbye
	}
	
	public void examplePush() {
		BrakeableService2 service = new BrakeableService2();
		
		service.getValuesUnsafe().subscribe(System.out::println);
		service.getValuesUnsafe().onNext("GARBAGE");
		service.play();
		
		// Greet
		// GARBAGE
		// Hello
		// and
		// goodbye
	}
	
	public void examplePush2() {
		BrakeableService2 service = new BrakeableService2();
		
		service.getValuesUnsafe2().subscribe(System.out::println);
		if (service.getValuesUnsafe2() instanceof Subject<?, ?>) {
			@SuppressWarnings("unchecked")
			Subject<String, String> subject = (Subject<String, String>) service.getValuesUnsafe2();
			subject.onNext("GARBAGE");
		}
		service.play();
		
		// Greet
		// GARBAGE
		// Hello
		// and
		// goodbye
	}
	
	public void exampleSafe() {
		SafeService service = new SafeService();
		
		service.getValues().subscribe(System.out::println);
		if (service.getValues() instanceof Subject<?, ?>) {
			System.out.println("Not safe!");
		}
		service.play();
		
		// Greet
		// Hello
		// and
		// goodbye
	}
	
	
	//
	// Tests
	//
	
	@Test
	public void testModifyReference() {
		TestSubscriber<String> testerBefore = new TestSubscriber<>();
		TestSubscriber<String> testerAfter = new TestSubscriber<>();
		
		BrakeableService service = new BrakeableService();
		service.items.subscribe(testerBefore);
		service.items = BehaviorSubject.create("Later");
		service.items.subscribe(testerAfter);
		service.play();
		
		testerBefore.assertReceivedOnNext(Arrays.asList("Greet"));
		testerAfter.assertReceivedOnNext(Arrays.asList(
			"Later",
			"Hello",
			"and",
			"goodbye"
		));
	}
	
	@Test
	public void testPush() {
		TestSubscriber<String> tester = new TestSubscriber<>();
		
		BrakeableService2 service = new BrakeableService2();
		service.getValuesUnsafe().subscribe(tester);
		service.getValuesUnsafe().onNext("GARBAGE");
		service.play();
		
		tester.assertReceivedOnNext(Arrays.asList(
			"Greet",
			"GARBAGE",
			"Hello",
			"and",
			"goodbye"
		));
	}
	
	@Test
	public void testPush2() {
		BrakeableService2 service = new BrakeableService2();
		
		assertTrue(service.getValuesUnsafe2() instanceof Subject<?,?>);
	}
	
	@Test
	public void testSafe() {
		SafeService service = new SafeService();
		
		assertFalse(service.getValues() instanceof Subject<?,?>);
	}
	
}
