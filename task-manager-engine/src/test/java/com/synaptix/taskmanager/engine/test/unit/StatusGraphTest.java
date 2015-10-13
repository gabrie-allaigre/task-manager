package com.synaptix.taskmanager.engine.test.unit;

import com.synaptix.taskmanager.engine.graph.IStatusGraph;
import com.synaptix.taskmanager.engine.graph.StatusGraphsBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Objects;

public class StatusGraphTest {

    public static <E> void assertUniqueContains(List<IStatusGraph<E>> statusGraphs, E previousStatus, E currentStatus, String statusTaskServiceCode) {
        int i = 0;
        if (statusGraphs != null && !statusGraphs.isEmpty()) {
            for (IStatusGraph<E> statusGraph : statusGraphs) {
                if (Objects.equals(statusGraph.getPreviousStatus(), previousStatus) && Objects.equals(statusGraph.getCurrentStatus(), currentStatus) && Objects
                        .equals(statusGraph.getStatusTaskServiceCode(), statusTaskServiceCode)) {
                    i++;
                }
            }
        }
        if (i == 0) {
            Assert.assertTrue("Not contains " + previousStatus + " " + currentStatus + " " + statusTaskServiceCode, false);
        } else if (i > 1) {
            Assert.assertTrue("Not unique " + previousStatus + " " + currentStatus + " " + statusTaskServiceCode, false);
        }
    }

    /**
     * null -> A
     */
    @Test
    public void test1() {
        List<IStatusGraph<String>> statusGraphs = StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask").build();

        Assert.assertNotNull(statusGraphs);
        Assert.assertEquals(statusGraphs.size(), 1);

        assertUniqueContains(statusGraphs, null, "A", "ATask");
    }

    /**
     * DEBUT -> A
     */
    @Test
    public void test2() {
        List<IStatusGraph<String>> statusGraphs = StatusGraphsBuilder.newBuilder("DEBUT").addNextStatusGraph("A", "ATask").build();

        Assert.assertNotNull(statusGraphs);
        Assert.assertEquals(statusGraphs.size(), 1);

        assertUniqueContains(statusGraphs, "DEBUT", "A", "ATask");
    }

    /**
     * null -> (A,B)
     */
    @Test
    public void test3() {
        List<IStatusGraph<String>> statusGraphs = StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask").addNextStatusGraph("B", "BTask").build();

        Assert.assertNotNull(statusGraphs);
        Assert.assertEquals(statusGraphs.size(), 2);

        assertUniqueContains(statusGraphs, null, "A", "ATask");
        assertUniqueContains(statusGraphs, null, "B", "BTask");
    }

    /**
     * null -> (A -> C,B)
     */
    @Test
    public void test4() {
        List<IStatusGraph<String>> statusGraphs = StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask", StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("C", "CTask"))
                .addNextStatusGraph("B", "BTask").build();

        Assert.assertNotNull(statusGraphs);
        Assert.assertEquals(statusGraphs.size(), 3);

        assertUniqueContains(statusGraphs, null, "A", "ATask");
        assertUniqueContains(statusGraphs, "A", "C", "CTask");
        assertUniqueContains(statusGraphs, null, "B", "BTask");
    }
}
