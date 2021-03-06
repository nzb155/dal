package com.ctrip.framework.dal.cluster.client.cluster;

import com.ctrip.framework.dal.cluster.client.Cluster;
import com.ctrip.framework.dal.cluster.client.config.ClusterConfig;
import com.ctrip.framework.dal.cluster.client.config.ClusterConfigProvider;
import com.ctrip.framework.dal.cluster.client.config.DefaultLocalConfigProvider;
import com.ctrip.framework.dal.cluster.client.database.DatabaseCategory;
import com.ctrip.framework.dal.cluster.client.sharding.context.DbShardContext;
import com.ctrip.framework.dal.cluster.client.sharding.context.MappedShardData;
import com.ctrip.framework.dal.cluster.client.sharding.context.ShardData;
import com.ctrip.framework.dal.cluster.client.sharding.context.TableShardContext;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.ctrip.framework.dal.cluster.client.database.DatabaseCategory.MYSQL;

/**
 * Created by @author zhuYongMing on 2019/11/14.
 */
public class DefaultClusterTest {

    private static Cluster cluster;

    @BeforeClass
    public static void init() {
        ClusterConfigProvider provider = new DefaultLocalConfigProvider("demo-cluster");
        ClusterConfig config = provider.getClusterConfig();
        cluster = config.generate();
    }

    @Test
    public void getClusterNameTest() {
        final String clusterName = cluster.getClusterName();
        Assert.assertEquals("demo-cluster", clusterName);
    }

    @Test
    public void getDatabaseCategoryTest() {
        final DatabaseCategory databaseCategory = cluster.getDatabaseCategory();
        Assert.assertEquals(MYSQL, databaseCategory);
    }

    @Test
    public void getDbShardNullTest() {
        final Integer table1ShardIndex = cluster.getDbShard("table1", new DbShardContext("whatever"));
        Assert.assertNull(table1ShardIndex);
    }

    @Test
    public void getDbShardTest() {
        final DbShardContext assignationShardIdContext = new DbShardContext("whatever").setShardId(0);
        final Integer AssignationShardIdShardIndex = cluster.getDbShard("table13", assignationShardIdContext);
        Assert.assertEquals(0, (int) AssignationShardIdShardIndex);

        final DbShardContext assignationShardValueContext = new DbShardContext("whatever");
        assignationShardValueContext.setShardValue(10);
        final Integer assignationShardValueShardIndex = cluster.getDbShard("table13", assignationShardValueContext);
        Assert.assertEquals(0, (int) assignationShardValueShardIndex);

        final DbShardContext assignationShardColValueContext = new DbShardContext("whatever");
        final Map<String, Object> data = new HashMap<>();
        data.put("other", 101); // ignore
        data.put("id", 100001); // ignore
        final ShardData shardData = new MappedShardData(data);
        assignationShardColValueContext.setShardColValues(shardData);
        final Integer assignationShardColValueShardIndex = cluster.getDbShard("table14", assignationShardColValueContext);
        Assert.assertEquals(1, (int) assignationShardColValueShardIndex);

        final DbShardContext assignationShardDataCandidates = new DbShardContext("whatever");
        final Map<String, Object> data1 = new HashMap<>();
        data1.put("age", 100); // ignore
        data1.put("id", 100002); // ignore
        final ShardData shardData1 = new MappedShardData(data1);
        assignationShardDataCandidates.addShardData(shardData1);
        final Integer assignationShardDataCandidatesShardIndex = cluster.getDbShard("table14", assignationShardDataCandidates);
        Assert.assertEquals(0, (int) assignationShardDataCandidatesShardIndex);
    }

    @Test
    public void getDbShardIndexShardOffsetTest() {
        final DbShardContext context = new DbShardContext("whatever");
        final Map<String, Object> data = new HashMap<>();
        data.put("id", 8);
        final ShardData shardData = new MappedShardData(data);
        context.setShardColValues(shardData);
        final Integer shardIndexHasOffset = cluster.getDbShard("table11", context);
        Assert.assertEquals(1, (int) shardIndexHasOffset);
    }

    @Test
    public void getDbShardShardIdWorkTest() {
        final DbShardContext context = new DbShardContext("whatever");
        context.setShardId(100);
        context.setShardValue(100);

        final Map<String, Object> data1 = new HashMap<>();
        data1.put("id", 7);
        final ShardData shardData1 = new MappedShardData(data1);
        context.setShardColValues(shardData1);

        final Map<String, Object> data2 = new HashMap<>();
        data2.put("id", 8);
        final ShardData shardData2 = new MappedShardData(data2);
        context.addShardData(shardData2);

        final Integer shardIdWordShardIndex = cluster.getDbShard("table12", context);
        Assert.assertEquals(100, (int) shardIdWordShardIndex);
    }

    @Test
    public void getDbShardShardValuedWorkTest() {
        final DbShardContext context = new DbShardContext("whatever");
        context.setShardValue(1002);

        final Map<String, Object> data1 = new HashMap<>();
        data1.put("id", 7);
        final ShardData shardData1 = new MappedShardData(data1);
        context.setShardColValues(shardData1);

        final Map<String, Object> data2 = new HashMap<>();
        data2.put("id", 8);
        final ShardData shardData2 = new MappedShardData(data2);
        context.addShardData(shardData2);

        final Integer shardValueWordShardIndex = cluster.getDbShard("table12", context);
        Assert.assertEquals(2, (int) shardValueWordShardIndex);
    }

    @Test
    public void getDbShardShardColValuedWorkTest() {
        final DbShardContext context = new DbShardContext("whatever");
        final Map<String, Object> data1 = new HashMap<>();
        data1.put("id", 7);
        final ShardData shardData1 = new MappedShardData(data1);
        context.setShardColValues(shardData1);

        final Map<String, Object> data2 = new HashMap<>();
        data2.put("id", 8);
        final ShardData shardData2 = new MappedShardData(data2);
        context.addShardData(shardData2);

        final Integer shardColValueWordShardIndex = cluster.getDbShard("table12", context);
        Assert.assertEquals(3, (int) shardColValueWordShardIndex);
    }

    @Test
    public void getDbShardMultiShardValueTypeTest() {

    }

    @Test
    public void getTableShardMultiShardValueTypeTest() {

    }

    @Test
    public void getUndefinedTableDbShardTest() {
        final DbShardContext context = new DbShardContext("whatever");
        final Map<String, Object> data1 = new HashMap<>();
        data1.put("id", 7);
        final ShardData shardData1 = new MappedShardData(data1);
        context.setShardColValues(shardData1);

        final Integer undefinedTable1ShardIndex = cluster.getDbShard("table1", context);
        Assert.assertEquals(1, (int) undefinedTable1ShardIndex);

        final boolean table1Enabled = cluster.tableShardingEnabled("table1");
        Assert.assertTrue(table1Enabled);

        final String undefinedTable1Separator = cluster.getTableShardSeparator("table1");
        Assert.assertEquals("", undefinedTable1Separator);
    }



    /* table shard */
    @Test
    public void getTableShardNullTest() {
        final String tableShardIndex = cluster.getTableShard("table1", new TableShardContext("whatever"));
        Assert.assertNull(tableShardIndex);
    }

    @Test
    public void getTableShardTest() {
        final TableShardContext assignationShardIdContext = new TableShardContext("whatever").setShardId("100");
        final String assignationShardIdShardIndex = cluster.getTableShard("table13", assignationShardIdContext);
        Assert.assertEquals("100", assignationShardIdShardIndex);

        final TableShardContext assignationShardValueContext = new TableShardContext("whatever");
        assignationShardValueContext.setShardValue(10);
        final String assignationShardValueShardIndex = cluster.getTableShard("table13", assignationShardValueContext);
        Assert.assertEquals("2", assignationShardValueShardIndex);

        final TableShardContext assignationShardColValueContext = new TableShardContext("whatever");
        final Map<String, Object> data = new HashMap<>();
        data.put("other", 101); // ignore
        data.put("id", 100001); // ignore
        data.put("age", 18);
        final ShardData shardData = new MappedShardData(data);
        assignationShardColValueContext.setShardColValues(shardData);
        final String assignationShardColValueShardIndex = cluster.getTableShard("table14", assignationShardColValueContext);
        Assert.assertEquals("2", assignationShardColValueShardIndex);

        final TableShardContext assignationShardDataCandidates = new TableShardContext("whatever");
        final Map<String, Object> data1 = new HashMap<>();
        data1.put("age", 100);
        data1.put("id", 100002);
        final ShardData shardData1 = new MappedShardData(data1);
        assignationShardDataCandidates.addShardData(shardData1);
        final String assignationShardDataCandidatesShardIndex = cluster.getTableShard("table14", assignationShardDataCandidates);
        Assert.assertEquals("0", assignationShardDataCandidatesShardIndex);
    }

    @Test
    public void getTableShardIndexShardOffsetTest() {
        final TableShardContext context = new TableShardContext("whatever");
        final Map<String, Object> data = new HashMap<>();
        data.put("id", 8); // ignore
        data.put("age", 18);
        final ShardData shardData = new MappedShardData(data);
        context.setShardColValues(shardData);
        final String shardIndexHasOffset = cluster.getTableShard("table11", context);
        Assert.assertEquals("1", shardIndexHasOffset);
    }

    @Test
    public void getTableShardShardIdWorkTest() {
        final TableShardContext context = new TableShardContext("whatever");
        context.setShardId("100");
        context.setShardValue(100);

        final Map<String, Object> data1 = new HashMap<>();
        data1.put("id", 7); // ignore
        data1.put("age", 17);
        final ShardData shardData1 = new MappedShardData(data1);
        context.setShardColValues(shardData1);

        final Map<String, Object> data2 = new HashMap<>();
        data2.put("id", 8); // ignore
        data2.put("age", 18);
        final ShardData shardData2 = new MappedShardData(data2);
        context.addShardData(shardData2);

        final String shardIdWordShardIndex = cluster.getTableShard("table12", context);
        Assert.assertEquals("100", shardIdWordShardIndex);
    }

    @Test
    public void getTableShardShardValueWorkTest() {
        final TableShardContext context = new TableShardContext("whatever");
        context.setShardValue(100);

        final Map<String, Object> data1 = new HashMap<>();
        data1.put("id", 7); // ignore
        data1.put("age", 17);
        final ShardData shardData1 = new MappedShardData(data1);
        context.setShardColValues(shardData1);

        final Map<String, Object> data2 = new HashMap<>();
        data2.put("id", 8); // ignore
        data2.put("age", 18);
        final ShardData shardData2 = new MappedShardData(data2);
        context.addShardData(shardData2);

        final String shardValueWordShardIndex = cluster.getTableShard("table12", context);
        Assert.assertEquals("1", shardValueWordShardIndex);
    }

    @Test
    public void getTableShardShardColValuedWorkTest() {
        final TableShardContext context = new TableShardContext("whatever");
        final Map<String, Object> data1 = new HashMap<>();
        data1.put("id", 7); // ignore
        data1.put("age", 17);
        final ShardData shardData1 = new MappedShardData(data1);
        context.setShardColValues(shardData1);

        final Map<String, Object> data2 = new HashMap<>();
        data2.put("id", 8); // ignore
        data2.put("age", 18);
        final ShardData shardData2 = new MappedShardData(data2);
        context.addShardData(shardData2);

        final String shardColValueWordShardIndex = cluster.getTableShard("table12", context);
        Assert.assertEquals("2", shardColValueWordShardIndex);
    }

    @Test
    public void getTableShardingEnabledTest() {
        final boolean table14Enabled = cluster.tableShardingEnabled("table14");
        Assert.assertTrue(table14Enabled);

        final boolean table15Enabled = cluster.tableShardingEnabled("table15");
        Assert.assertFalse(table15Enabled);
    }

    @Test
    public void getAllTableShardTest() {
        final Set<String> expected = new HashSet<>();
        expected.add("0");
        expected.add("1");
        expected.add("2");
        final Set<String> table15 = cluster.getAllTableShards("table15");
        Assert.assertEquals(expected, table15);
    }

    @Test
    public void getTableShardSeparatorTest() {
        final String table14Separator = cluster.getTableShardSeparator("table14");
        Assert.assertEquals("_", table14Separator);
        final String table15Separator = cluster.getTableShardSeparator("table15");
        Assert.assertEquals("", table15Separator);
    }

    @Test
    public void dbShardingTest() {
        Assert.assertTrue(cluster.dbShardingEnabled());
        Assert.assertEquals(4, cluster.getAllDbShards().size());
        Assert.assertTrue(cluster.getAllDbShards().contains(1));
        Assert.assertTrue(cluster.getAllDbShards().contains(2));
        Assert.assertTrue(cluster.getAllDbShards().contains(3));
        Assert.assertTrue(cluster.getAllDbShards().contains(4));
    }

    @Test
    public void testShardingOffset() {
        DbShardContext dbShardCtx = new DbShardContext("demo-cluster");
        Assert.assertNull(cluster.getDbShard("table31", dbShardCtx));
        dbShardCtx.setShardId(1);
        Assert.assertEquals(1, (int) cluster.getDbShard("table31", dbShardCtx));
        TableShardContext tbShardCtx = new TableShardContext("demo-cluster");
        Assert.assertNull(cluster.getTableShard("table31", tbShardCtx));
        tbShardCtx.setShardId("2");
        Assert.assertEquals("2", cluster.getTableShard("table31", tbShardCtx));

        dbShardCtx = new DbShardContext("demo-cluster");
        dbShardCtx.setShardValue(5);
        Assert.assertEquals(2, (int) cluster.getDbShard("table11", dbShardCtx));
        dbShardCtx.setShardId(1);
        Assert.assertEquals(1, (int) cluster.getDbShard("table11", dbShardCtx));
        tbShardCtx = new TableShardContext("demo-cluster");
        tbShardCtx.setShardValue(5);
        Assert.assertEquals("3", cluster.getTableShard("table11", tbShardCtx));
        tbShardCtx.setShardId("2");
        Assert.assertEquals("2", cluster.getTableShard("table11", tbShardCtx));
    }

}
