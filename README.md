# PulseChat
* High QPS Stand-alone chat system
* 練習`Webflux`與`Spring RSocket`的開發
* 完成項目：
  * API
  * 日誌與追蹤
    * AOP 進出紀錄
    * Reactive 環境下追蹤 ID (TraceID)
  * 異常處裡機制，統一返回`PrombleDetail`
  * 整合測試(TestContainer)
* 待處理：
  * 壓力測試
  * 前端頁面
# API
## Account
| Url                   | Method | 功能            |
| --------------------- | ------ | --------------- |
| /accounts/            | Get    | 查找全部        |
| /accounts/{accountId} | Get    | 查找AccountById |

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
| endpoint                        | 功能                  |
| ------------------------------- | --------------------- |
| chat.room.{roomId}              | 根據房間ID建立Channel |
| chat.message.add                | 發送訊息              |
| chat.message.update.{messageId} | 更新信息              |
| chat.message.delete.{messageId} | 刪除信息              |
| chat.message.read.{messageId}   | 讀取信息              |
| chat.history.get.{roomId}       | 獲取該聊天室歷史資訊  |

# 模擬正式環境測試
## 準備數據 - 目標
* Account 與 Chat Room 佔比 `5 比 1`
* Chat Room: 10,000 筆
  | 成員占比 | 群組類型 | 群組人數範圍 | 訊息筆數範圍    |
  | -------- | -------- | ------------ | --------------- |
  | 20%      | 一對一   | 2 人         | 1 ～ 10         |
  | 70%      | 小群組   | 3 ～ 6 人    | 20 ～ 100       |
  | 9%       | 中型群組 | 10 ～ 51 人  | 500 ～ 1,500    |
  | 1%       | 大型群組 | 51 ～ 100 人 | 5,000 ～ 10,001 |

## 測試邏輯
* 測試時間：30分鐘
* 單一用戶
  1. 獲取Token
  2. 建立RSocket連線
  3. 隨即抽取RoomId 
  4. 區分兩項
        * 獲取當前Room的連線(Channel)
          1. 讀取最新10數據
          2. 有時間間隔(800~2500毫秒)更新當前已的讀取數據，看能否判斷當前數據是否有已讀
        * 有時間間隔(500~2000毫秒)發送數據，一次共發送1~5條訊息，訊息為隨機大小
          * 訊息從 Payload 池（不同 Size）中隨機選一個
  5. 重複4步驟，需要有時間間隔(2~5秒)