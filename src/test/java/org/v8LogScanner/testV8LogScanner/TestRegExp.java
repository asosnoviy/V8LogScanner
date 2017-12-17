package org.v8LogScanner.testV8LogScanner;

import org.junit.Test;
import org.v8LogScanner.commonly.Filter;
import org.v8LogScanner.rgx.RegExp;
import org.v8LogScanner.rgx.RegExp.PropTypes;
import org.v8LogScanner.rgx.StrokeFilter;
import org.v8LogScanner.rgx.TimeFilter;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TestRegExp {

    @Test
    public void testGetProfile() throws Exception {

        RegExp regExp = new RegExp();
        regExp.getFilter(PropTypes.CallID).add("test val");
        regExp.getFilter(PropTypes.CallID).add("test val2");
        regExp.getFilter(PropTypes.Time).add("15:34");

        Map<PropTypes, Filter> testFilters = regExp.getFilters();

        assertEquals(testFilters.get(PropTypes.CallID).get(0), "test val");
        assertEquals(testFilters.get(PropTypes.CallID).get(1), "test val2");
        assertEquals(testFilters.get(PropTypes.Time).get(0), "1534");
    }

    @Test
    public void testSetProfile() throws Exception {

        Map<PropTypes, Filter> tFilters = new HashMap<>();

        Filter tFilter = new StrokeFilter();
        tFilter.add("test val");
        tFilter.add("test val2");
        tFilters.put(PropTypes.CallID, tFilter);

        Filter tFilter2 = new TimeFilter();
        tFilter2.add("15:34");
        tFilters.put(PropTypes.Time, tFilter2);

        RegExp regExp = new RegExp();
        regExp.setFilters(tFilters);

        Map<PropTypes, Filter> testFilters = regExp.getFilters();

        assertEquals(testFilters.get(PropTypes.CallID).get(0), "test val");
        assertEquals(testFilters.get(PropTypes.CallID).get(1), "test val2");
        assertEquals(testFilters.get(PropTypes.Time).get(0), "1534");

    }

}
