package unitTest;

import comp3111.covid.datastorage.LocationData;
import comp3111.covid.datastorage.LocationProperty;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestLocationData {
	LocationData testLocationData;
	Number[] expectedValues = {1L, 2d, 3d, 4d, 5d, 6d, 7d};
	
	@Before
	public void setUp() {
		this.testLocationData = new LocationData("tst", "testing land", "test", 1L, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0);
	}
	
	@Test
	public void testGetLocationProperty() {
		int i = 0;
		for(LocationProperty prop : LocationProperty.values()) {
			Assert.assertEquals(this.expectedValues[i++], testLocationData.getLocationProperty(prop));
		}
	}
}
