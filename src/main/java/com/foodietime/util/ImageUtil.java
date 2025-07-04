package com.foodietime.util;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

public class ImageUtil {

    /**
     * 【優化版】將圖形縮小後回傳，會自動偵測並盡可能保留原始圖片格式 (JPG/PNG)。
     * 如果原始格式無法寫入或無法識別，會預設使用 PNG 格式以保留透明度。
     *
     * @param srcImageData 來源圖形資料 (byte[])
     * @param scaleSize    欲將圖形最長邊縮至的目標尺寸 (像素)
     * @return 縮小完畢的圖形資料 (byte[])，若發生錯誤則回傳原圖。
     */
    public static byte[] shrink(byte[] srcImageData, int scaleSize) {
        // ==================== 1. 前置檢查 ====================
        if (srcImageData == null || srcImageData.length == 0 || scaleSize <= 1) {
            return srcImageData;
        }

        // ==================== 2. 偵測原始圖片格式 ====================
        String formatName = getFormatName(srcImageData);
        // 如果能識別格式且不是 GIF，就使用原始格式；否則，預設使用 "png" 以確保透明度能被保留。
        String outputFormat = (formatName != null && !formatName.equalsIgnoreCase("gif")) ? formatName : "png";

        try (ByteArrayInputStream bais = new ByteArrayInputStream(srcImageData)) {
            BufferedImage srcBufferedImage = ImageIO.read(bais);

            if (srcBufferedImage == null) {
                // 如果 ImageIO 無法讀取此圖片，直接回傳原數據
                return srcImageData;
            }

            // ==================== 3. 計算縮放後尺寸 ====================
            int originalWidth = srcBufferedImage.getWidth();
            int originalHeight = srcBufferedImage.getHeight();

            if (originalWidth == 0 || originalHeight == 0) {
                return srcImageData;
            }

            int longer = Math.max(originalWidth, originalHeight);
            if (longer <= scaleSize) {
                // 如果圖片已經比目標尺寸小，無需縮放，但仍根據目標格式重新輸出以統一格式
                return reEncode(srcBufferedImage, outputFormat);
            }

            double scale = scaleSize / (double) longer;
            int scaledWidth = (int) (originalWidth * scale);
            int scaledHeight = (int) (originalHeight * scale);

            // ==================== 4. 高品質縮圖 ====================
            // 建立一個支援透明度的畫布
            BufferedImage scaledBufferedImage = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = scaledBufferedImage.createGraphics();

            // 設定高品質渲染提示
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // 執行繪製 (縮放)
            g2d.drawImage(srcBufferedImage, 0, 0, scaledWidth, scaledHeight, null);
            g2d.dispose();

            // ==================== 5. 輸出為 byte[] ====================
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                ImageIO.write(scaledBufferedImage, outputFormat, baos);
                return baos.toByteArray();
            }

        } catch (IOException e) {
            e.printStackTrace();
            // 發生任何異常時，安全地回傳原始圖片數據
            return srcImageData;
        }
    }

    /**
     * 輔助方法：從圖片數據的開頭讀取，以判斷其格式 (例如 "jpeg", "png")。
     *
     * @param imageData 圖片的 byte[] 數據
     * @return 圖片格式的字串，如果無法識別則返回 null。
     */
    private static String getFormatName(byte[] imageData) {
        try (ImageInputStream iis = ImageIO.createImageInputStream(new ByteArrayInputStream(imageData))) {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
            if (readers.hasNext()) {
                ImageReader reader = readers.next();
                return reader.getFormatName().toLowerCase();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 輔助方法：將一個 BufferedImage 重新編碼為指定的格式。
     */
    private static byte[] reEncode(BufferedImage image, String format) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, format, baos);
            return baos.toByteArray();
        }
    }
}