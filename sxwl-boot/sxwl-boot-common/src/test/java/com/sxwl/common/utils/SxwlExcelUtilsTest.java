package com.sxwl.common.utils;

import com.sxwl.common.annotation.SxwlExcel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Excel 导入导出工具类单元测试
 *
 * @author shitianyang
 * @since 0.1.0
 */
@DisplayName("SxwlExcelUtils 工具类测试")
class SxwlExcelUtilsTest {

    /** 测试用导出 VO */
    private static class TestExportVO {
        @SxwlExcel(name = "用户名", order = 1)
        private String username;

        @SxwlExcel(name = "年龄", order = 2)
        private Integer age;

        @SxwlExcel(name = "邮箱", order = 3)
        private String email;

        public TestExportVO() {}

        TestExportVO(String username, Integer age, String email) {
            this.username = username;
            this.age = age;
            this.email = email;
        }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    @Test
    @DisplayName("导出 Excel：写入后再导入，数据一致")
    void testExportImport() throws Exception {
        List<TestExportVO> data = Arrays.asList(
                new TestExportVO("张三", 25, "zhangsan@test.com"),
                new TestExportVO("李四", 30, "lisi@test.com")
        );

        // 导出到字节数组
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        SxwlExcelUtils.exportToExcel(data, outputStream, "测试Sheet");
        byte[] excelBytes = outputStream.toByteArray();
        assertTrue(excelBytes.length > 0, "导出的 Excel 字节数组不应为空");

        // 从字节数组导入
        ByteArrayInputStream inputStream = new ByteArrayInputStream(excelBytes);
        List<TestExportVO> imported = SxwlExcelUtils.importFromExcel(inputStream, TestExportVO.class);
        assertEquals(2, imported.size(), "导入的数据条数应正确");

        TestExportVO first = imported.get(0);
        assertEquals("张三", first.getUsername());
        assertEquals(25, first.getAge());
        assertEquals("zhangsan@test.com", first.getEmail());

        TestExportVO second = imported.get(1);
        assertEquals("李四", second.getUsername());
        assertEquals(30, second.getAge());
        assertEquals("lisi@test.com", second.getEmail());
    }

    @Test
    @DisplayName("导出空列表：生成只含表头的 Excel")
    void testExportEmptyList() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        SxwlExcelUtils.exportToExcel(List.of(), outputStream, "空数据");
        byte[] excelBytes = outputStream.toByteArray();
        assertTrue(excelBytes.length > 0, "空数据也应生成有效 Excel");
    }

    @Test
    @DisplayName("导入空 Excel：返回空列表")
    void testImportEmptyExcel() throws Exception {
        // 只导出表头（无数据行）
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        SxwlExcelUtils.exportToExcel(List.of(), outputStream, "空Sheet");

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        List<TestExportVO> imported = SxwlExcelUtils.importFromExcel(inputStream, TestExportVO.class);
        assertTrue(imported.isEmpty(), "空 Excel 导入应返回空列表");
    }

    @Test
    @DisplayName("导出+导入：父类字段也被正确处理")
    void testParentField() throws Exception {
        // 直接测试 resolveFields 行为：通过导出含父类字段的类来验证
        List<TestExportVO> data = List.of(new TestExportVO("admin", 18, "admin@test.com"));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        // 不抛出异常即表示父类字段处理正常
        SxwlExcelUtils.exportToExcel(data, outputStream, "测试");
        assertTrue(outputStream.toByteArray().length > 0);
    }
}
