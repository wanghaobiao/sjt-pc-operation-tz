package com.acrabsoft.web.service.sjt.pc.operation.web.kafka.service;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.service.HBaseService;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.hbaseHuawei.*;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.ParamEnum;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.FirstKeyOnlyFilter;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.security.UserGroupInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class HbaseHuaweiService {
    private Logger logger = LoggerFactory.getLogger( this.getClass() );

    public static final String ZOOKEEPER_SERVER_PRINCIPAL_KEY = "zookeeper.server.principal";
    public static final String ZOOKEEPER_DEFAULT_SERVER_PRINCIPAL = "zookeeper/hadoop.hadoop.com";

    @Autowired(required = false)
    @Qualifier("connection")
    private Connection conn;
    @Value("${hbase.config.path}")
    private String dir;

    @Value("${spring.profiles.active}")
    private String mode;
    @Value("${hbaseUser}")
    private String hbaseUser;
    /**
     * 获得链接
     *
     * @return
     */
    private synchronized Connection getConnection() throws IOException {
        if (conn == null || conn.isClosed()) {
            if (mode.equals( ParamEnum.active.dev.getCode() )) {
                org.apache.hadoop.conf.Configuration conf = HBaseConfiguration.create();
                conf.set("hbase.zookeeper.quorum", "ubuntu-vm");   //hadoop14,hadoop15,hadoop16为hostname
                conf.set("hbase.zookeeper.property.clientPort", "2181");
                conf.set("zookeeper.znode.parent", "/hbase-unsecure");
                UserGroupInformation userGroupInformation = UserGroupInformation.createRemoteUser("zyx");
                return ConnectionFactory.createConnection(conf, User.create(userGroupInformation));
            } else if (mode.equals(ParamEnum.active.test.getCode())) {
                logger.info("配置文件目录:" + dir);
                Configuration conf = HBaseConfiguration.create();
                conf.addResource(new Path(dir + "core-site.xml"));
                conf.addResource(new Path(dir + "hdfs-site.xml"));
                conf.addResource(new Path(dir + "hbase-site.xml"));
                if (User.isHBaseSecurityEnabled(conf)) {
                    try {
                        String userName = hbaseUser;
                        String userKeytabFile = dir + "user.keytab";
                        logger.info("userKeytabFile:" + userKeytabFile);
                        String krb5File = dir + "krb5.conf";
                        logger.info("krb5File:" + krb5File);
                        HbaseLoginUtil.setJaasConf( HBaseService.ZOOKEEPER_DEFAULT_LOGIN_CONTEXT_NAME, userName, userKeytabFile);
                        HbaseLoginUtil.setZookeeperServerPrincipal(ZOOKEEPER_SERVER_PRINCIPAL_KEY, ZOOKEEPER_DEFAULT_SERVER_PRINCIPAL);
                        logger.info("==================================>2");
                        HbaseLoginUtil.login(userName, userKeytabFile, krb5File, conf);
                        this.conn = ConnectionFactory.createConnection(conf);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return conn;
    }


    public void createNamespaceNX(String nameSpace) throws IOException {
        Connection connection = getConnection();
        Admin admin = connection.getAdmin();

        //以下是正确代码：
        try {
            admin.getNamespaceDescriptor(nameSpace);
        } catch (NamespaceNotFoundException e) {
            //若发生特定的异常，即找不到命名空间，则创建命名空间
            NamespaceDescriptor namespaceDescriptor = NamespaceDescriptor.create(nameSpace).build();
            admin.createNamespace(namespaceDescriptor);
        }
    }

    /**
     * 传入对象的class 创建hbase表
     *
     * @param clazz 需要创建表的实体对象
     * @throws IOException
     * @throws IntrospectionException
     */
    public void createTable(Class clazz) throws IOException {
        Connection connection = getConnection();
        Class pclazz = clazz;
        HTableName annotation = null;
        while (pclazz != null && annotation == null) {
            annotation = (HTableName) pclazz.getAnnotation(HTableName.class);
            pclazz = pclazz.getSuperclass();
        }
        Assert.notNull(annotation, "[HTableName] 注解未获取到");
        TableName tableName = TableName.valueOf(annotation.value());
        Admin admin = connection.getAdmin();

        Assert.isTrue(!admin.tableExists(tableName), String.format("表 %s 已存在", tableName.getNameAsString()));
        HTableDescriptor tableDescriptorBuilder = new HTableDescriptor(tableName);
        //获取对象引用
        pclazz = clazz;
        List<Field> fields = new ArrayList<>();
        //递归获取属性
        while (pclazz != null) {
            fields.addAll(Arrays.asList(pclazz.getDeclaredFields()));
            pclazz = pclazz.getSuperclass();
        }
        for (Field field : fields) {
            HTableField hTableField = field.getAnnotation(HTableField.class);
            HTableFieldEntity HTableFieldEntity = field.getAnnotation(HTableFieldEntity.class);
            Set<byte[]> familiesKeys = tableDescriptorBuilder.getFamiliesKeys();
            //添加列簇
            if (hTableField != null && !familiesKeys.contains(hTableField.family().getBytes(StandardCharsets.UTF_8))) {
                HColumnDescriptor hColumnDescriptor = new HColumnDescriptor(hTableField.family());
                tableDescriptorBuilder.addFamily(hColumnDescriptor);
            }
            if (HTableFieldEntity != null && !familiesKeys.contains(HTableFieldEntity.family().getBytes(StandardCharsets.UTF_8))) {
                HColumnDescriptor hColumnDescriptor = new HColumnDescriptor(HTableFieldEntity.family());
                tableDescriptorBuilder.addFamily(hColumnDescriptor);
            }
        }
        admin.createTable(tableDescriptorBuilder);

    }

    /**
     * 根据hbase Scan 获取数据对象
     *
     * @param scan  scan对象
     * @param clazz 返回类型的class对象
     * @return
     * @throws IOException
     * @throws InvocationTargetException
     * @throws IntrospectionException
     * @throws InstantiationException
     * @throws ParseException
     * @throws IllegalAccessException
     */
    public List<?> findByScan(Scan scan, Class clazz) throws
            IOException, InvocationTargetException, IntrospectionException, InstantiationException, ParseException, IllegalAccessException {
        Connection connection = getConnection();
        Admin admin = connection.getAdmin();
        HTableName annotation = (HTableName) clazz.getAnnotation(HTableName.class);
        Assert.notNull(annotation, "[" + clazz.getName() + "]" + "缺少注解信息");
        TableName tableName = TableName.valueOf(annotation.value());
        Assert.isTrue(admin.tableExists(tableName), "表不存在");
        Table table = connection.getTable(tableName);
        ResultScanner scanner = table.getScanner(scan);
        List<Object> list = resultToBeanList(scanner, clazz);
        return list;
    }


    /**
     * 保存数据
     *
     * @param dataList
     * @param createTable
     * @return
     * @throws IOException
     * @throws IllegalAccessException
     * @throws IntrospectionException
     * @throws InvocationTargetException
     */
    public int saveBatch(List<?> dataList, boolean createTable) throws
            IOException, IllegalAccessException, IntrospectionException, InvocationTargetException {
        Connection connection = getConnection();
        if (dataList == null || dataList.size() == 0) {
            return 0;
        }
        Object item = dataList.get(0);
        Class pclazz = item.getClass();
        HTableName annotation = null;
        while (pclazz != null && annotation == null) {
            annotation = (HTableName) pclazz.getAnnotation(HTableName.class);
            pclazz = pclazz.getSuperclass();
        }
        Assert.notNull(annotation, "[HTableName]注解 未找到！");
        TableName tableName = TableName.valueOf(annotation.value());
        List<Put> putList = new ArrayList<>();
        Admin admin = connection.getAdmin();
        if (createTable && !admin.tableExists(tableName)) {
            this.createTable(item.getClass());
        }
        Assert.isTrue(admin.tableExists(tableName), String.format("表 [%s] 不存在", tableName.getNameAsString()));
        Table table = connection.getTable(tableName);
        for (Object obj : dataList) {
            Put put = getPut(obj);
            putList.add(put);
        }
        table.put(putList);
        table.close();
        return dataList.size();
    }


    /**
     * 根据ID查找数据
     *
     * @param rowKey
     * @param clazz
     * @return
     * @throws IOException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws IntrospectionException
     * @throws InstantiationException
     */
    public Object findById(String rowKey, Class clazz) throws
            IOException, InvocationTargetException, IllegalAccessException, IntrospectionException, InstantiationException, ParseException {
        Connection connection = getConnection();
        HTableName hTableName = (HTableName) clazz.getAnnotation(HTableName.class);
        TableName tableName = TableName.valueOf(hTableName.value());
        Table table = connection.getTable(tableName);
        Get get = new Get( Bytes.toBytes(rowKey));
        Result result = table.get(get);
        if (result.isEmpty()) {
            return null;
        }
        //获取实例对象
        Object obj = resultToBean(result, clazz);
        table.close();
        return obj;
    }

    /**
     * hbase查询结果转bean
     *
     * @param result
     * @param clazz
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws IntrospectionException
     * @throws InvocationTargetException
     * @throws ParseException
     */
    private Object resultToBean(Result result, Class clazz) throws
            IllegalAccessException, InstantiationException, IntrospectionException, InvocationTargetException, ParseException {

        //获取实例对象
        Object obj = clazz.newInstance();
        Class pclazz = clazz;
        List<Field> fields = new ArrayList<>();
        //递归获取属性
        while (pclazz != null) {
            fields.addAll(Arrays.asList(pclazz.getDeclaredFields()));
            pclazz = pclazz.getSuperclass();
        }
        for (Field field : fields) {
            //过滤字段
            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {
                continue;
            }
            PropertyDescriptor pd = new PropertyDescriptor(field.getName(), clazz);
            HTableId annotation = field.getAnnotation(HTableId.class);
            HTableField hTableField = field.getAnnotation(HTableField.class);
            HTableFieldEntity hTableFieldEntity = field.getAnnotation(HTableFieldEntity.class);
            Method setMethod = pd.getWriteMethod();//获得set方法
            //配置主键的值
            if (annotation != null) {
                setMethod.invoke(obj, Bytes.toString(result.getRow()));
            }
            //配置非主键字段
            else if (hTableField != null) {
                byte[] value = result.getValue(hTableField.family().getBytes(StandardCharsets.UTF_8), hTableField.column().getBytes(StandardCharsets.UTF_8));
                String strValue = Bytes.toString(value);
                if (strValue == null) {
                    continue;
                }
                Class<?> type = field.getType();
                //类型处理
                if (type.isInstance(new String())) {
                    setMethod.invoke(obj, strValue);
                } else if (type.isInstance(new Integer(1))) {
                    setMethod.invoke(obj, Integer.valueOf(strValue));
                } else if (type.isInstance(new Float(1))) {
                    setMethod.invoke(obj, Float.valueOf(strValue));
                } else if (type.isInstance(new Double(1))) {
                    setMethod.invoke(obj, Double.valueOf(strValue));
                } else if (type.isInstance(new Date())) {
                    try {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
                        simpleDateFormat.setLenient(false);
                        Date parse = simpleDateFormat.parse(strValue);
                        setMethod.invoke(obj, parse);
                    } catch (Exception e) {
                        try {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            simpleDateFormat.setLenient(false);
                            Date parse = simpleDateFormat.parse(strValue);
                            setMethod.invoke(obj, parse);
                        } catch (Exception e1) {

                        }
                    }
                }
            } else if (hTableFieldEntity != null) {//处理实体类对象
                Class<?> type = field.getType();
                Object resultToBean = resultToBean(result, type);
                setMethod.invoke(obj, resultToBean);
            }
        }
        return obj;
    }

    /**
     * hbase查询结果 转换为bean列表
     *
     * @param resultScanner
     * @param clazz
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws IntrospectionException
     * @throws InvocationTargetException
     * @throws ParseException
     */
    private List<Object> resultToBeanList(ResultScanner resultScanner, Class clazz) throws
            IllegalAccessException, InstantiationException, IntrospectionException, InvocationTargetException, ParseException {
        List<Object> resultList = new ArrayList<>();
        for (Result result : resultScanner) {
            Object o = this.resultToBean(result, clazz);
            resultList.add(o);
        }
        return resultList;
    }

    public long rowCount(String prefix, Class aclass) throws IOException {
        Connection connection = getConnection();
        HTableName hTableName = (HTableName) aclass.getAnnotation(HTableName.class);
        Assert.isTrue(hTableName != null, "[HTableName] 注解未找到！");
        long rowCount = 0;
        try {
            TableName tableName = TableName.valueOf(hTableName.value());
            Table table = connection.getTable(tableName);
            Scan scan = new Scan();
            FilterList filterList = new FilterList();
            FirstKeyOnlyFilter firstKeyOnlyFilter = new FirstKeyOnlyFilter();
            PrefixFilter prefixFilter = new PrefixFilter(prefix.getBytes());
            filterList.addFilter(firstKeyOnlyFilter);
            filterList.addFilter(prefixFilter);
            scan.setFilter(filterList);
            ResultScanner resultScanner = table.getScanner(scan);
            for (Result result : resultScanner) {
                rowCount += result.size();
            }
        } catch (IOException e) {
            logger.info(e.getMessage(), e);
        }
        return rowCount;
    }

    /**
     * 生成hbase put对象
     *
     * @param obj
     * @return
     * @throws IntrospectionException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private Put getPut(Object obj) throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        Class clazz = obj.getClass();//获得实体类名
        Class pclazz = clazz;
        Put put = null;
        Field idField = null;
        ArrayList<Field> fields = new ArrayList<>();
        while (pclazz != null) {
            fields.addAll(Arrays.asList(pclazz.getDeclaredFields()));
            pclazz = pclazz.getSuperclass();
        }
        //拿到主键
        for (Field field : fields) {
            if (field.isAnnotationPresent(HTableId.class)) {
                idField = field;
                PropertyDescriptor pd = new PropertyDescriptor(field.getName(), clazz);
                Method getMethod = pd.getReadMethod();//获得get方法
                Object result = getMethod.invoke(obj);
                put = new Put(result.toString().getBytes(StandardCharsets.UTF_8));
                break;
            }
        }
        if (put == null) {
            return null;
        }
        //配置字段
        for (Field field : fields) {
            if (field.getName().equals(idField.getName())
                    || (!field.isAnnotationPresent(HTableField.class)
                    && !field.isAnnotationPresent(HTableFieldEntity.class))) {
                continue;
            }
            //获取列簇和列名
            HTableField hTableField = field.getAnnotation(HTableField.class);
            HTableFieldEntity hTableFieldEntity = field.getAnnotation(HTableFieldEntity.class);
            PropertyDescriptor pd = new PropertyDescriptor(field.getName(), clazz);
            Method getMethod = pd.getReadMethod();//获得get方法
            Object result = getMethod.invoke(obj);
            if (hTableFieldEntity != null) {
                setPutData(result, put);
                continue;
            }
            if (result == null) {
                result = "";
            }
            if (result instanceof Date) {
                Date tempDate = (Date) result;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                result = simpleDateFormat.format(tempDate);
            }
            put.addColumn(hTableField.family().getBytes(StandardCharsets.UTF_8), hTableField.column().getBytes(StandardCharsets.UTF_8), result.toString().getBytes(StandardCharsets.UTF_8));
        }
        return put;
    }

    private void setPutData(Object obj, Put put) throws InvocationTargetException, IllegalAccessException, IntrospectionException {
        List<Field> fieldList = getFieldList(obj.getClass());
        for (Field field : fieldList) {
            //获取列簇和列名
            HTableField hTableField = field.getAnnotation(HTableField.class);
            HTableFieldEntity hTableFieldEntity = field.getAnnotation(HTableFieldEntity.class);
            PropertyDescriptor pd = new PropertyDescriptor(field.getName(), obj.getClass());
            Method getMethod = pd.getReadMethod();//获得get方法
            Object result = getMethod.invoke(obj);
            if (hTableFieldEntity != null) {
                setPutData(result, put);
                continue;
            } else if (hTableField != null) {
                if (result == null) {
                    result = "";
                }
                if (result instanceof Date) {
                    Date tempDate = (Date) result;
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    result = simpleDateFormat.format(tempDate);
                }
            }
            put.addColumn(hTableField.family().getBytes( StandardCharsets.UTF_8), hTableField.column().getBytes(StandardCharsets.UTF_8), result.toString().getBytes(StandardCharsets.UTF_8));
        }
    }

    private List<Field> getFieldList(Class clazz) {
        Class pclazz = clazz;
        ArrayList<Field> fields = new ArrayList<>();
        while (pclazz != null) {
            fields.addAll( Arrays.asList(pclazz.getDeclaredFields()));
            pclazz = pclazz.getSuperclass();
        }
        return fields;
    }
}
