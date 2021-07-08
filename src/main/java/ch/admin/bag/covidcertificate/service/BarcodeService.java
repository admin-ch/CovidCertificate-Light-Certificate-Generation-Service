package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import com.upokecenter.cbor.CBORObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.digg.dgc.encoding.Barcode;
import se.digg.dgc.encoding.BarcodeException;
import se.digg.dgc.service.DGCBarcodeEncoder;

import java.io.IOException;
import java.security.SignatureException;
import java.time.Instant;

import static ch.admin.bag.covidcertificate.api.Constants.CREATE_BARCODE_FAILED;

@Service
@Slf4j
@RequiredArgsConstructor
public class BarcodeService {
    private final DGCBarcodeEncoder dgcBarcodeEncoder;

    public Barcode createBarcode(String dgcJSON, Instant expirationDate) {
        try {
            log.debug("Creating Barcode");
            return dgcBarcodeEncoder.encodeToBarcode(CBORObject.FromJSONString(dgcJSON).EncodeToBytes(), expirationDate);
        } catch (BarcodeException | IOException | SignatureException e) {
            log.error("Barcode creation failed");
            throw new CreateCertificateException(CREATE_BARCODE_FAILED);
        }
    }
}
