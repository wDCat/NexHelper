# Nex Helper

自用的Funtouch OS辅助工具集合。

只支持简中系统，有其他需求请自行修改keyword。

Funtouch ~~OS~~ 可真够恶心的，谁用谁知道。



## 基本功能

- 安装辅助
  - 自动输入密码
  - 自动跳过vivo推荐界面
  - 自动点击安装
- 通知清理
  - 移除vivo开发者模式警告黄条

  - 移除常驻通知组标题


## TODO

- 应用安装历史记录

- 常驻通知移除白名单

- 自定义主题导入


## 杂七杂八

- 移除其他应用的常驻通知
  - `cancelNotification`方法是不能移除常驻通知的，使用`snoozeNotification(sbNoti.key, 86400000)`循环延迟通知达到移除的效果。
- 移除通知组头
  - Funtouch会自动将第三方音乐控制通知收缩，导致我无法愉悦地切歌。移除通知组头之后可以缓解这个问题。
- 等了快3个月，play市场无法自动安装软件的bug终于修好了，~~感天动地~~。
- 任意应用分屏
  - 升级到9.0之后，大部分软件都可以分屏了，但是游戏之类的App依旧无法分屏。
  - Funtouch使用的是白名单分屏机制，白名单内的应用才可以正常分屏。
  - 因此可以使用双开软件`平行空间`或者开源的`VirtualXposed`，修改其包名为白名单内的应用，如`com.baidu.searchbox`，将无法分屏的应用安装到其中，就可以愉悦地分屏了。

