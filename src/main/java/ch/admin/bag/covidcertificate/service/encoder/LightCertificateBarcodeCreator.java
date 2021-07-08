/**
 * Heavily inspired by https://stackoverflow.com/a/59955617
 * <p>
 * Adjusted for better PNG compression by using a color palette and using max compression
 */

package ch.admin.bag.covidcertificate.service.encoder;

import com.google.common.base.Charsets;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.encoder.Encoder;
import com.google.zxing.qrcode.encoder.QRCode;
import se.digg.dgc.encoding.Barcode;
import se.digg.dgc.encoding.BarcodeCreator;
import se.digg.dgc.encoding.BarcodeException;
import se.digg.dgc.encoding.impl.DefaultBarcodeCreator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class LightCertificateBarcodeCreator implements BarcodeCreator {

    public static final Charset DEFAULT_CHARSET = Charsets.UTF_8;

    public static final int DEFAULT_WIDTH_AND_HEIGHT = 800;

    /**
     * See {@link BarcodeCreator#create(String)}
     * The barcode returned does not contain an SVG.
     */
    @Override
    public Barcode create(String contents) throws BarcodeException {
        return this.create(contents, DEFAULT_CHARSET);
    }

    /**
     * See {@link BarcodeCreator#create(String, Charset)}
     * The barcode returned does not contain an SVG.
     */
    @Override
    public Barcode create(String contents, Charset characterSet) throws BarcodeException {
        final Map<EncodeHintType, Object> encodingHints = new HashMap<>();
        encodingHints.put(EncodeHintType.CHARACTER_SET, characterSet);
        try {
            QRCode code = Encoder.encode(contents, ErrorCorrectionLevel.Q, encodingHints);
            BufferedImage image = renderQRImage(code, DEFAULT_WIDTH_AND_HEIGHT, DEFAULT_WIDTH_AND_HEIGHT, 4);

            byte[] bytes = bufferedImageToBytes(image);
            return new Barcode(DefaultBarcodeCreator.DEFAULT_TYPE, bytes, DefaultBarcodeCreator.DEFAULT_IMAGE_FORMAT,
                    "", DEFAULT_WIDTH_AND_HEIGHT, DEFAULT_WIDTH_AND_HEIGHT, contents);
        } catch (WriterException | IOException e) {
            throw new BarcodeException("Failed to create barcode - " + e.getMessage(), e);
        }
    }

    private static byte[] bufferedImageToBytes(BufferedImage image) throws IOException {
        var baos = new ByteArrayOutputStream();
        var writer = ImageIO.getImageWriters(ImageTypeSpecifier.createFromRenderedImage(image), "png").next();
        var params = writer.getDefaultWriteParam();
        if (params.canWriteCompressed()) {
            params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            params.setCompressionQuality(0.0f);
        }

        try (ImageOutputStream out = ImageIO.createImageOutputStream(baos)) {
            writer.setOutput(out);
            writer.write(null, new IIOImage(image, null, null), params);
            writer.dispose();
        }
        // ImageIO.write(image,"png", baos);
        return baos.toByteArray();
    }

    private final static Color darkBlue = Color.decode("#1e3889");
    private final static Color lightBlue = Color.decode("#8292c7");

    public static BufferedImage renderQRImage(QRCode code, int width, int height, int quietZone) {
        IndexColorModel cm = new IndexColorModel(2, 4,
                //          dark blue   light blue     white      aliasing
                new byte[]{(byte) 0x1e, (byte) 0x82, (byte) 255, (byte) 0xb7},
                new byte[]{(byte) 0x38, (byte) 0x92, (byte) 255, (byte) 0xbe},
                new byte[]{(byte) 0x89, (byte) 0xc7, (byte) 255, (byte) 0xdc}
        );
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY, cm);
        Graphics2D graphics = image.createGraphics();

        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setBackground(Color.white);
        graphics.clearRect(0, 0, width, height);
        graphics.setColor(darkBlue);

        ByteMatrix input = code.getMatrix();
        if (input == null) {
            throw new IllegalStateException();
        }
        int inputWidth = input.getWidth();
        int inputHeight = input.getHeight();
        int qrWidth = inputWidth + (quietZone * 2);
        int qrHeight = inputHeight + (quietZone * 2);
        int outputWidth = Math.max(width, qrWidth);
        int outputHeight = Math.max(height, qrHeight);

        int multiple = Math.min(outputWidth / qrWidth, outputHeight / qrHeight);
        int leftPadding = (outputWidth - (inputWidth * multiple)) / 2;
        int topPadding = (outputHeight - (inputHeight * multiple)) / 2;
        final int FINDER_PATTERN_SIZE = 7;
        final float CIRCLE_SCALE_DOWN_FACTOR = 28f / 30f;
        int circleSize = (int) (multiple * CIRCLE_SCALE_DOWN_FACTOR);

        for (int inputY = 0, outputY = topPadding; inputY < inputHeight; inputY++, outputY += multiple) {
            for (int inputX = 0, outputX = leftPadding; inputX < inputWidth; inputX++, outputX += multiple) {
                if (input.get(inputX, inputY) == 1) {
                    if (!(inputX <= FINDER_PATTERN_SIZE && inputY <= FINDER_PATTERN_SIZE
                            || inputX >= inputWidth - FINDER_PATTERN_SIZE && inputY <= FINDER_PATTERN_SIZE
                            || inputX <= FINDER_PATTERN_SIZE && inputY >= inputHeight - FINDER_PATTERN_SIZE)) {
                        // graphics.fillRoundRect(outputX, outputY, circleSize, circleSize, (int)(round*circleSize), (int)(round*3));
                        graphics.fillOval(outputX, outputY, circleSize, circleSize);
                    }
                }
            }
        }

        int circleDiameter = multiple * FINDER_PATTERN_SIZE;
        drawFinderPatternCircleStyle(graphics, leftPadding, topPadding, circleDiameter);
        drawFinderPatternCircleStyle(graphics, leftPadding + (inputWidth - FINDER_PATTERN_SIZE) * multiple, topPadding,
                circleDiameter);
        drawFinderPatternCircleStyle(graphics, leftPadding, topPadding + (inputHeight - FINDER_PATTERN_SIZE) * multiple,
                circleDiameter);

        return image;
    }

    static final float round = 0.6f;

    private static void drawFinderPatternCircleStyle(Graphics2D graphics, int x, int y, int circleDiameter) {
        final int WHITE_CIRCLE_DIAMETER = circleDiameter * 5 / 7;
        final int WHITE_CIRCLE_OFFSET = circleDiameter / 7;
        final int MIDDLE_DOT_DIAMETER = circleDiameter * 3 / 7;
        final int MIDDLE_DOT_OFFSET = circleDiameter * 2 / 7;

        graphics.setColor(lightBlue);
        graphics.fillOval(x, y, circleDiameter, circleDiameter);
        graphics.setColor(Color.white);
        graphics.fillOval(x + WHITE_CIRCLE_OFFSET, y + WHITE_CIRCLE_OFFSET, WHITE_CIRCLE_DIAMETER,
                WHITE_CIRCLE_DIAMETER);
        graphics.setColor(darkBlue);
        graphics.fillOval(x + MIDDLE_DOT_OFFSET, y + MIDDLE_DOT_OFFSET, MIDDLE_DOT_DIAMETER, MIDDLE_DOT_DIAMETER);
    }
}