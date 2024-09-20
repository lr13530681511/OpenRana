# OpenRana 用户及权限系统

## Git 仓库
- Repository URL: https://github.com/lr13530681511/OpenRana.git

## *项目介绍*
## 一个简单的用户权限系统（包含功能：用户账户、权限管理、单点登录）


## 功能详细及使用介绍

- ## **用户账户及权限系统（支持单点登录及其他系统权限注册）** 
  
  - ##  **用户账户部分**
  - 
    - 用户登录方式：账号密码、手机号。（暂时仅支持这两种方式，后续计划增加微信等第三方登录方式。）

  - ## **权限管理部分**
    - ### 权限精度
      - **权限精度1**：系统级权限（admin、user、guest）
      - **权限精度2**：接口级权限（精确到每个 API 接口）
    - ### **运行逻辑**：
      
      - ***权限注册器***： `PermissionRequiredScanner`系统启动时自动扫描权限注解，注册权限
      - ***权限注册***：`@PermissionRequired`兼容两种精度的权限管理，使用该注解可自动注册权限
      - 例子 ： `@PermissionRequired(value = RoleType.GUEST, appName = "用户权限系统", moduleName = "account", permissionName = "找回密码")`
      - value 为权限精度，默认为系统级权限 （默认为 guest）
      - moduleName 为权限所属模块名称 （默认为空，如不自定义鉴权部分走系统级权限精度， 如自定义则走接口级权限进入鉴权）
      - 
      - ***鉴权逻辑***：
        - ***权限拦截器***：`PermissionAspect`鉴权拦截切面，拦截所有带权限注解的Method
        - ***权限验证器***：`RoleFilter`权限验证器，验证用户是否拥有权限。本系统鉴权与SSO单点登录共用一套权限验证器
      

  - ### **单点登录部分**
  - 
    - 支持其他系统权限注册（权限精度与本系统保持一致）
    - 其他系统使用 Token 由本系统产生
    - 若要使用本系统作为单点登录解决方案，其他系统需执行以下步骤参考上面权限系统的介绍（用法一样）
    - ### 需要配置文件如下：
    - 配置文件：`application.yml`需要添加以下配置
    - `sso:
         hostPort: https://sso.linergou.ink
         checkUrl: /account/SSO/checkRole
         ssoLoginUrl: /account/SSO/ssoLogin?appName=$appName&redirectUrl=$redirectUrl
         registrationUrl: /account/SSO/permissionRegistration`
    - `hostPort`为单点登录系统地址，
    - `checkUrl`为单点登录系统权限验证接口，
    - `ssoLoginUrl`为单点登录系统登录页面，
    - `registrationUrl`为单点登录系统权限注册接口
      - 
    - ***还需要添加三个文件***：
      - 权限注册器
      - 权限验证器
      - 跨域访问配置
      - 模板文件存在：src/main/resources/SSOOtherProjectConfig