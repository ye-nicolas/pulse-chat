# PulseChat
High QPS Stand-alone chat system
# API
## Account
| Url                   | Method | 功能            |
| --------------------- | ------ | --------------- |
| /accounts/            | Get    | 查找全部        |
| /accounts/{accountId} | Get    | 查找AccountById |

## Role
| Url             | Method | 功能         |
| --------------- | ------ | ------------ |
| /roles/         | Get    | 查找全部     |
| /roles/{roleId} | Get    | 查找RoleById |
| /roles/         | Post   | 建立Create   |

## Auth
| Url           | Method | 功能        |
| ------------- | ------ | ----------- |
| /auth/login   | Post   | 登陸        |
| /auth/refresh | Post   | 更新Token   |
| /auth/account | Post   | 建立Account |

## Friend Ship
| Url                          | Method | 功能                      |
| ---------------------------- | ------ | ------------------------- |
| /friend-ships/               | Get    | 獲取Account對應的好友清單 |
| /friend-ships/               | Post   | 添加好友                  |
| /friend-ships/{friendShipId} | Patch  | 確認交友邀請              |

## Chat Room
| Url                         | Method | 功能             |
| --------------------------- | ------ | ---------------- |
| /chat-rooms/                | Get    | 查詢所有聊天室   |
| /chat-rooms/{roomId}        | Get    | 根據ID查詢聊天室 |
| /chat-rooms/                | Post   | 創建聊天室       |
| /chat-rooms/{roomId}/member | Post   | 添加用戶         |
| /chat-rooms/{roomId}/member | Delete | 移除用戶         |
| /chat-rooms/{roomId}        | Delete | 刪除聊天室       |

# WebSocket
| endpoint                        | 功能                 |
| ------------------------------- | -------------------- |
| session.open.room.{roomId}      | 根據房間ID建立連線   |
| chat.message.add                | 發送訊息             |
| chat.message.update.{messageId} | 更新信息             |
| chat.message.delete.{messageId} | 刪除信息             |
| chat.message.read.{messageId}   | 讀取信息             |
| chat.history.get.{roomId}       | 獲取該聊天室歷史資訊 |