[TOC]

# Mapper 
## select 属性
* id：SQL 标识
* parameterType：传入参数类型
* resultType/Map：返回值类型，Map 为定制类型

## 自动映射/驼峰映射
* 自动映射：默认
* 驼峰映射：即 real_name 对应 realName，在 settings 中开启 mapUnderscoreToChmelCase 属性

## 传递多个参数
* 注解传递：在 Mapper 方法参数列表使用 @Param("paramId")，然后在 Mapper.xml 中使用 paramId 引用参数
* bean 传递：传入 bean，直接使用 bean 的成员变量名
* 混合使用：在 Mapper.xml 中使用 paramId.property 引用参数

## 分页参数 RowBounds
直接在参数列表添加 RowBounds，Mybatis 会自动识别并分页
```java
    new RowBounds(2, 5);
```

## 插入得到主键
#### 主键回填
得到主键，并赋值给 id 属性
```xml
    <insert id="insertRole1" parameterType="role"
          useGeneratedKeys="true" keyProperty="id">
        insert into t_role(role_name, note) values(#{roleName}, #{note})
    </insert>
```
#### 自定义主键
采用 selectKey 元素完成数据库主键与 bean 的 id 属性的映射
```xml
    <insert id="insertRole2" parameterType="role">
        <selectKey keyProperty="id" resultType="int" order="BEFORE">
            select if (max(id) = null, 1, max(id) + 3) from t_role
        </selectKey>
        insert into t_role(role_name, note) values(#{roleName}, #{note})
    </insert>
```

## sql 引用
#### 普通 sql 
* 定义
```xml
    <sql id="roleCols1">
        id, role_name, note
    </sql>
```
* 引用
```xml
    <select id="getRole1" resultType="role" parameterType="int">
        select <include refid="roleCols1" /> from t_role where id = #{id}
    </select>
```
#### 带变量别名的 sql
* 定义
```xml
    <sql id="roleCols2">
        ${alias}.id, ${alias}.role_name, ${alias}.note
    </sql>
```
* 引用（需要给别名赋值）
```xml
    <select id="getRole2" resultType="role" parameterType="int">
        select
        <include refid="roleCols2">
            <!-- 给变量别名赋值 -->
            <property name="alias" value="t_role" />
        </include>
         from t_role where id = #{id}
    </select>
```

## resultMap
子元素：
###### constructor
配置构造方法，子元素：
* idArg：定义主键
* arg：其他参数
###### id
主键
###### result
其他参数
### 级联
###### association / collection
属性：
* property：对应级联属性
* column：根据该列键值查询
* select：指定查询方法（Mapper 接口 + 方法名）
###### discriminator
属性：
* column：作为鉴别器的字段（相当于 switch 中的东西）
* javaType：该字段对应的 java 类型（根据 java 类型完成级联/ 枚举的实质是下标）

子元素 case 属性（与 switch 的 case 相同）：
* value：字段的值
* resultMap：装填鉴别器的实现类（不同字段值对应不同的实现类）