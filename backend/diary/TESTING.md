# 日记后端 JUnit 测试说明

本套测试针对 `com.silver.diary` 包及当前接口编写，未照搬旧整合版的包名、DTO 或路由。测试方法名使用简短英文：register、badPwd、hashPwd、noRepeat、rollback、authHeader 等。需要强调意图的用例增加中文 DisplayName。

## 运行结果

2026-10-02 在本目录实际执行 `mvn -B -ntp test`：**61 个测试全部通过，0 个失败，0 个执行错误，0 个跳过**。此前发现的三个业务缺陷已修复，原有断言保留。

| 文件（src/test/java/com/silver/diary 下） | 数量 | 覆盖内容 |
| --- | ---: | --- |
| controller/UserControllerTest.java | 13 | 注册散列、重复用户、输入校验、写入失败、登录、用户信息、密码更新 |
| controller/ArticleControllerTest.java | 10 | 发布、正文状态校验、分类归属、跨用户读写删除、保留封面 |
| controller/CategoryControllerTest.java | 6 | 创建者身份、名称校验、在用分类、越权更新、保留创建者、删除 |
| handler/GlobalExceptionHandlerTest.java | 8 | 业务异常、错误 JSON、缺参、类型错误、401、405、500、503 |
| interceptor/LoginInterceptorTest.java | 3 | 无 Token、无效 Token、真实控制器与拦截器的请求头契约 |
| upload/UploadValidatorTest.java | 6 | 空文件、伪造图片、超限图片、真实 PNG、WAV 容器头、伪造音频 |
| upload/LocalFileStorageTest.java | 4 | 临时目录保存、安全文件名、非法目录、受限删除 |
| upload/UploadServiceTest.java | 7 | 提交后删旧文件、回滚清理新文件、提交失败、保留封面、磁盘失败、无账号、新建日记 |
| utils/JwtUtilTest.java | 4 | 正常签发和解析、过期、错误密钥、错误格式 |

另有 support/TestData.java，用于构造测试用户、MyBatis 查询替身和真实 PNG，不是测试类。

## 在 IDEA 一键运行全部测试

1. 用 IDEA 打开此后端目录，将 pom.xml 作为 Maven 项目导入。
2. 点击右侧 Maven 面板的 Reload All Maven Projects，等待依赖下载和索引完成。本次新增 junit-platform-launcher，scope 为 test，版本由 Spring Boot 管理。
3. 展开 src → test → java → com.silver.diary。
4. 打开 DiaryTestLauncher.java。
5. 点击 main 方法左侧的绿色三角，选择 Run 'DiaryTestLauncher.main()'。
6. 在 Run 控制台末尾查看统计：61 tests found、61 tests successful、0 tests failed，进程退出码为 0。
7. 测试失败时，末尾会输出失败详情并以退出码 1 结束；未找到测试也会返回 1。修复后点击重新运行。

这是测试启动类，放在 src/test/java 中，不是 Spring Boot 服务启动类。它发现 com.silver.diary 包下以 Test 结尾的 JUnit Jupiter 测试类，以后按同样命名添加测试即可自动纳入。它自身不匹配测试类名，不会递归运行。正式打包不包含该类和 test 范围的启动器依赖。

如果看不到绿色运行按钮，检查项目 SDK 为 JDK 17 或以上，并确认 src/test/java 在 IDEA 中标记为 Test Sources Root。运行配置应使用 diary_backend 模块的 classpath，包含测试输出和测试依赖。

## 单独运行和 Maven 运行

- 在任意测试类名旁点击绿色三角，可运行该类，IDEA 会显示测试树。
- 在测试方法旁点击绿色三角，可单独运行该方法。
- Maven 面板 → Lifecycle → test，可运行全部测试并生成报告。main 启动器输出控制台摘要，不生成 Surefire XML 报告。

Git Bash：

```bash
cd /c/.ProgramS/Projects/vibe-diray/backend/diary
mvn -B -ntp test
mvn "-Dtest=UserControllerTest#hashPwd" test
mvn "-Dtest=UploadServiceTest" test
```

报告位于 target/surefire-reports。不要把 target 上传到 Git。

这些测试不会启动真实 HTTP 端口，不需要启动 MySQL，不读取真实 JWT 密钥；文件写入只使用 JUnit TempDir。UploadServiceTest 使用真实 TransactionTemplate 配合模拟事务管理器验证调用与补偿顺序，不能替代真实数据库事务集成测试。

异常处理用例会主动制造异常，因此控制台可能出现 ERROR 日志和异常堆栈，这是测试输入导致的预期日志。判断是否通过，请看测试统计和退出码。

## 已修复的问题

### hashPwd：修改密码保存了明文

UserController.updatePassword 现在调用 passwordEncoder.encode(dto.getNewPwd()) 保存 BCrypt 散列，与注册和登录的密码处理一致。测试确认保存的值不是明文，并能通过 BCrypt matches 验证。

此修改影响今后的密码更新；不会自动修复数据库里以前已保存的明文密码。

### noRepeat：确认密码缺失时空指针

在已校验 newPwd 非空后，用 dto.getNewPwd().equals(dto.getReNewPwd()) 比较。确认密码缺失或不一致时抛出 BusinessException，由全局异常处理器返回 400，而不会发生空指针错误。测试同时确认数据库没有被更新。

### authHeader：登录请求头不一致

LoginInterceptor 现在与控制器统一读取 Authorization。继续使用项目现有的原始 Token 格式，不加 Bearer 前缀。例如：Authorization: 登录接口返回的token。

authHeader 使用真实 UserController、LoginInterceptor 和 GlobalExceptionHandler 验证请求贯通。badToken 也改为传 Authorization，并校验 JWT 验证方法确实被调用及返回 401，避免只因请求头缺失而误通过。

## 建议提交信息

确认本地测试通过后，可使用：

fix(diary): 修复密码更新与认证请求头并添加测试启动类

本次未自动执行 git commit。