package com.foodietime.util;

import net.coobird.thumbnailator.Thumbnails;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageUtil {

    // ShrinkResult 內部類維持不變
    public static class ShrinkResult {
        private final byte[] imageData;
        private final String formatName;

        public ShrinkResult(byte[] imageData, String formatName) {
            this.imageData = imageData;
            this.formatName = formatName;
        }
        public byte[] getImageData() { return imageData; }
        public String getFormatName() { return formatName; }
    }

    /**
     * 【最終高效能版】使用 Thumbnailator 函式庫來壓縮圖片。
     *
     * @param srcImageData 來源圖形資料
     * @param scaleSize    目標尺寸
     * @return 包含壓縮後資料和格式名稱的 ShrinkResult 物件
     */
    public static ShrinkResult shrink(byte[] srcImageData, int scaleSize) {
        if (srcImageData == null || srcImageData.length == 0 || scaleSize <= 1) {
            return new ShrinkResult(srcImageData, "jpeg");
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            // ★★ 核心修改：一行程式碼完成所有壓縮工作 ★★
            Thumbnails.of(new ByteArrayInputStream(srcImageData))
                    .size(scaleSize, scaleSize)      // 設定目標尺寸，它會自動保持長寬比
                    .outputFormat("jpeg")            // 預設輸出為 jpeg，效能較好
                    .toOutputStream(baos);           // 將結果寫入輸出流

            // 假設輸出格式為 jpeg
            return new ShrinkResult(baos.toByteArray(), "jpeg");

        } catch (IOException e) {
            // 如果 Thumbnailator 處理失敗（例如不支援的格式），安全地回傳原圖
            e.printStackTrace();
            return new ShrinkResult(srcImageData, "jpeg");
        }
    }
}