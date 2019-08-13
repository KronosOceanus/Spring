[TOC]

# 常见陷阱

## @Transactional 自调用失效
#### 错误示范
```java
    public class RoleServiceImpl implements RoleService{
        
        @Transactional
        public int insertRole(){};
        
        @Transactional
        public int insertRoleList(){
            //调用自身方法，产生自调用问题
            insertRole();
        }
    }
```
@Transactional 注解底层是 AOP 实现，而这两个方法中并不存在代理对象的调用，所以会失效
#### 解决
```java
        @Transactional
        public int insertRoleList(){
            //利用 Spring IoC 容器产生代理对象
            RoleService roleService = applicationContext.getBean("roleService", RoleService.class); 
            roleService.insertRole();
        }
```

## 错误使用 Service
在 Controller 层中多次调用 roleService 操作多个对象，而多次操作并不属于同一个事务

## 过长时间占用事务
#### 错误
在 @Transactional 修饰的方法中，使用与事务无关的、消耗资源多的操作
#### 改正
在 Controller 层执行与事务无关的、消耗资源多的操作

## 错误捕捉异常
#### 错误
在 @Transactional 修饰的方法中，自己加入 try...catch...块，导致 Spring 总会提交事务
#### 改正
捕获异常之后，抛出 RuntimeException