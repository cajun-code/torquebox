package org.torquebox.common.pool;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class ManagedPoolTest extends AbstractPoolTestCase {

	private StringInstanceFactory factory;

	@Before
	public void setUp() throws Exception {
		this.factory = new StringInstanceFactory();
	}

	@Test
	public void testInitializeMinimumInstances() throws Exception {
		ManagedPool<String> pool = new ManagedPool<String>(this.factory, 5, 10);
		pool.start();

		while (pool.size() < 5) {
			Thread.sleep(500);
		}

		Thread.sleep(1000);

		assertEquals(5, pool.size());
		assertEquals(5, pool.availableSize());
		assertEquals(0, pool.borrowedSize());
	}

	@Test
	public void testGrowWithinBounds() throws Exception {
		ManagedPool<String> pool = new ManagedPool<String>(this.factory, 5, 10);
		pool.start();
		pool.waitForInitialFill();

		Set<String> instances = new HashSet<String>();

		instances.add(assertBorrow(pool));
		instances.add(assertBorrow(pool));
		instances.add(assertBorrow(pool));
		instances.add(assertBorrow(pool));
		instances.add(assertBorrow(pool));
		
		assertEquals( 5, pool.size() );
		assertEquals( 5, pool.borrowedSize() );
		assertEquals( 0, pool.availableSize() );
		
		instances.add(assertBorrow(pool));
		instances.add(assertBorrow(pool));
		instances.add(assertBorrow(pool));
		
		assertEquals( 8, pool.size() );
		assertEquals( 8, pool.borrowedSize() );
		assertEquals( 0, pool.availableSize() );
		
		for ( String each : instances ) {
			pool.releaseInstance( each );
		}
		
		assertEquals( 8, pool.size() );
		assertEquals( 0, pool.borrowedSize() );
		assertEquals( 8, pool.availableSize() );
	}
	
	@Test
	public void testGrowToBounds() throws Exception {
		ManagedPool<String> pool = new ManagedPool<String>(this.factory, 5, 10);
		pool.start();
		pool.waitForInitialFill();

		Set<String> instances = new HashSet<String>();

		instances.add(assertBorrow(pool));
		instances.add(assertBorrow(pool));
		instances.add(assertBorrow(pool));
		instances.add(assertBorrow(pool));
		instances.add(assertBorrow(pool));
		
		assertEquals( 5, pool.size() );
		assertEquals( 5, pool.borrowedSize() );
		assertEquals( 0, pool.availableSize() );
		
		instances.add(assertBorrow(pool));
		instances.add(assertBorrow(pool));
		instances.add(assertBorrow(pool));
		instances.add(assertBorrow(pool));
		instances.add(assertBorrow(pool));
		
		assertEquals( 10, pool.size() );
		assertEquals( 10, pool.borrowedSize() );
		assertEquals( 0, pool.availableSize() );
		
		assertBorrowTimeout( pool );
		
		int available = 0;
		for ( String each : instances ) {
			assertEquals( available, pool.availableSize() );
			pool.releaseInstance( each );
			++available;
			assertEquals( available, pool.availableSize() );
		}
		
		assertEquals( 10, pool.size() );
		assertEquals( 0, pool.borrowedSize() );
		assertEquals( 10, pool.availableSize() );
	}
	
	@Test
	public void testGrowToBoundsLoop() throws Exception {
		for ( int i = 0 ; i < 40 ; ++i ) {
			testGrowToBounds();
		}
		
	}
	
	
}
