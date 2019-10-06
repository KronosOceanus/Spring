package redis_service;

public interface RedisTemplateService {
    //执行多个命令
    void execMultiCommand();
    //执行 Redis 事务
    void execTransaction();
    //执行流水线
    void execPipeline();
}
