package com.nicolas.perf;

import com.nicolas.pulse.adapter.repository.DbMeta;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

@Slf4j
public class CleanTestDataUseCase {
    public void execute(Input input) {
        try {
            Connection connection = DriverManager.getConnection(input.getUrl(), input.getUser(), input.getPwd());
            connection.setAutoCommit(false);
            delete(DbMeta.AccountData.TABLE_NAME, input.getDeleteAfterDate(), connection);
            delete(DbMeta.ChatRoomData.TABLE_NAME, input.getDeleteAfterDate(), connection);
            delete(DbMeta.ChatRoomMemberData.TABLE_NAME, input.getDeleteAfterDate(), connection);
            delete(DbMeta.ChatMessageData.TABLE_NAME, input.getDeleteAfterDate(), connection);
            log.info("刪除測試數據成功");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void delete(String tableName, LocalDate deleteAfterDate, Connection connection) {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM \"%s\" where \"%s\" > '%s';".formatted(tableName, DbMeta.AccountData.COL_CREATED_AT, deleteAfterDate.toString()));
            connection.commit();
        } catch (SQLException e) {
            log.error(e.getMessage());
            try {
                connection.rollback();
            } catch (SQLException ex) {
                log.error(e.getMessage());
            }
            throw new RuntimeException("資料庫數據刪除失敗", e);
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class Input {
        private String url;
        private String user;
        private String pwd;
        private LocalDate deleteAfterDate;
    }

    public static void main(String[] args) {
        CleanTestDataUseCase testDataUseCase = new CleanTestDataUseCase();
        CleanTestDataUseCase.Input build = CleanTestDataUseCase.Input.builder()
                .url("jdbc:postgresql://localhost:5433/pulse_chat?currentSchema=pulse_chat")
                .user("nicolas")
                .pwd("123456789")
                .deleteAfterDate(LocalDate.now())
                .build();
        testDataUseCase.execute(build);
    }
}
